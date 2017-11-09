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


(defn write-simple-ma-ddb-safe
  "Write an item to simple ma table
  only if the time stamp exceeds the max timestamp
  in the table to enforce immutability."
  [pair ts simple-ma]
  (far/put-item ddb-cred (table :SIMPLE.MA)
                {:pair pair :timestamp ts :simple-ma simple-ma}
                {:cond-expr "attribute_not_exists(pair) AND attribute_not_exists(#st)"
                 :expr-attr-names {"#st" "timestamp"}}))


(defn query-max-date-prices
  "Get max date of a pair in prices
  table and return as unix timestamp in ms
  Args:
  pair (string)
  "
  [pair]
  (:timestamp (last (far/query ddb-cred (table :PRICES) {:pair [:eq pair]}))))






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
