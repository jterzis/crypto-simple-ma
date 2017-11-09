(ns app.dynamodb.migration
  (:require [app.service.dynamodb :refer [ddb-cred ddb-env]]
            [clojure.tools.logging :as log]
            [taoensso.faraday :as far]
            [app.dynamodb.middlelayer :refer [table]]))

(declare delete-simple-ma-table!)

(defn table-list
  "List DynamoDB tables"
  []
  (log/info "Tables:" (far/list-tables ddb-cred)))

(defn create-simple-ma-table!
  "Create simple-ma table iff it not exists
     hash: pair (string)
     range: timestamp (long)"
  []
  (far/ensure-table
    ddb-cred (table :SIMPLE.MA)
    [:pair :s]
    {:range-keydef [:timestamp :n]
     :throughput {:read 50 :write 50}
     :block? false}))

(defn delete-simple-ma-table!
  "Delete funding-alert table"
  []
  (far/delete-table ddb-cred (table :SIMPLE.MA)))

(defn migrate-up
  "Create DynamoDB tables and load data"
  []
  (create-simple-ma-table!)
  (log/info (table :SIMPLE.MA) "table created!"))

(defn migrate-down
  "Create DynamoDB tables and load data"
  []
  (delete-simple-ma-table!)
  (log/info (table :SIMPLE.MA) "table deleted!"))
