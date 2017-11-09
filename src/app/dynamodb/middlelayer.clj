(ns app.dynamodb.middlelayer
  (:require [app.service.dynamodb :refer [ddb-cred ddb-env]]
            [app.api.utils.util :refer [utc-now add-subx-min-ts
                                        convert-ts-human-readable
                                        to-dte-from-long]]
            [clojure.tools.logging :as log]
            [taoensso.faraday :as far])
  (:import [com.amazonaws.services.dynamodbv2.model
            ConditionalCheckFailedException]))

(defn table
  "Get table name from environment and keyword"
  [table-name]
  (keyword (str (name table-name) "." ddb-env)))

(declare pull-session-pair-ddb)

(declare query-pair-quotes-ddb)

(declare query-max-date-quote)


(declare update-item-ddb-unsafe)

(declare update-item-ddb-safe)

(defn append-to-decision-list
  "Search through a user-id's decision
  list for an existing hash-enc-cc and
  append a new decision returning a
  new list that is ready for insertion
  at the user-id attribute of table
  old-item (map)
  user-id (string)
  hash-enc-cc (string)
  verified (boolean)
  app-id (string)
  Returns:
  a list of maps
  "
  [old-item user-id hash-enc-cc verified app-id]
  (let [hash-cc-list (-> old-item ((keyword user-id)) ((keyword hash-enc-cc)))]
    (conj (if hash-cc-list hash-cc-list [])
          {:timestamp (utc-now)
           :verified  verified
           :app-id    app-id})))

(defn pull-item-ddb
  "Get an item and all its
   attributes given a truncated
   pan, the hash key
   Args:
   trunc-pan (string)
   remove-hash (bool) removes hash key from output
   "
  [trunc-pan & [remove-hash]]
  (let [ddb-item (far/get-item ddb-cred (table :fraud-detect-table)
                      {:trunc_pan trunc-pan})]
    (if-not remove-hash ddb-item (dissoc ddb-item :trunc_pan))))

(defn push-item-ddb
  "Check and safely push an item
   to dynamo db appending if
   attribute, user-id, already
   exists in item corresponding
   to trunc-pan hash key.

   Takes a map of
   :trunc-pan (string)
   :hash-enc-cc (string)
   :user-id (string)
   :app-id (string)
   :verified (bool)
   old-item (optional map)
  Returns map of updated attributes
  in item or nil on error
  "
  [trunc-pan hash-enc-cc user-id app-id verified & [old-item]]
  (try
    (cond
      (not (nil? old-item)) (update-item-ddb-unsafe
                              trunc-pan user-id {(keyword hash-enc-cc)
                                                 (append-to-decision-list
                                                   old-item
                                                   user-id
                                                   hash-enc-cc
                                                   verified
                                                   app-id)})
      :else (update-item-ddb-safe trunc-pan user-id hash-enc-cc
                                  {(keyword hash-enc-cc)
                                   [{:verified  verified
                                     :app-id    app-id
                                     :timestamp (utc-now)}]}))
    (catch Exception e (log/error "Problem writing to dynamodb" e))))

(defn update-item-ddb-safe
    "Sets user-id attribute to a
    specific value by first checking
    whether the item already has that
    attribute and short circuiting if
    so.
    Args:
    trunc-pan (string) hash key of table
    user-id (string)
    new-val (map i.e. {:ADSFDASF234 [{:verified true :app-id \"4353543\"}]
    hash-enc-cc (string)
    Returns existing attribute value
    if already exists, o/w nil
    "
    [trunc-pan user-id hash-enc-cc new-val]
    (assert ((keyword hash-enc-cc) new-val) "Hash Encoded CC should be key of new-val map")
    (try (far/update-item ddb-cred (table :fraud-detect-table)
                          {:trunc_pan trunc-pan}
                          {:update-expr (format "SET %s = :val" user-id)
                           :expr-attr-vals {":val" new-val}
                           :cond-expr (format "attribute_not_exists(%s)" user-id)
                           :return :updated-new})
         (catch ConditionalCheckFailedException e
           (let [old-item (pull-item-ddb trunc-pan)
                 {[{:keys [app-id verified]}] (keyword hash-enc-cc)} new-val]
             (update-item-ddb-unsafe trunc-pan user-id
                                     ((keyword user-id)
                                       (assoc-in old-item
                                                 [(keyword user-id) (keyword hash-enc-cc)]
                                                 (append-to-decision-list
                                                   old-item user-id hash-enc-cc verified app-id))))))))

(defn update-item-ddb-unsafe
  "Sets a specific attribute
  to a specific value without checking
  whether the item already has that
  attribute
  Args:
  trunc-pan (string) hash key of table
  user-id (string)
  new-val (map of list of maps)
  "
  [trunc-pan user-id new-val]
  (far/update-item ddb-cred (table :fraud-detect-table)
                   {:trunc_pan trunc-pan}
                   {:update-expr    (format "SET %s = :val" user-id)
                    :expr-attr-vals {":val"
                                     new-val}
                    :return :updated-new
                    }))



(defn write-simple-ma-ddb-safe
  "Write an item to simple ma table
  only if the time stamp exceeds the max timestamp
  in the table to enforce immutability."
  [pair ts simple-ma]
  (far/put-item ddb-cred (table :SIMPLE.MA)
                {:pair pair :timestamp ts :simple-ma simple-ma}
                {:cond-expr "attribute_not_exists(pair) AND attribute_not_exists(#st)"
                 :expr-attr-names {"#st" "timestamp"}})
  )

(defn query-max-date-simple-ma
  "Get max date of a pair in simple ma
  table and return as unix timestamp in ms
  Args:
  pair (string)
  "
  [pair]
  (:timestamp (last (far/query ddb-cred (table :SIMPLE.MA) {:pair [:eq pair]}))))

(defn pull-session-pair-ddb
  []
  (map
    (fn [y] (:pair y))
    (far/scan ddb-cred (table :PAIRS.SESSION))))


(defn query-pair-quotes-ddb
  " Indexed query of pair
  quotes from Poloniex within
  a specific timespan
  Args:
  pairs (seq of strings)
  min-time (clojure.lang.BigInt)
  max-time (clojure.lang.BigInt)
  "
  [pairs min-time max-time]
  (far/query ddb-cred (table :PRICES) {:pair [:eq pairs]
                                       :timestamp [:between [min-time max-time]]}))
