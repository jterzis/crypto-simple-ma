(ns app.api.health
  (:require [app.service.dynamodb :refer [ddb-cred ddb-env]]
            [taoensso.faraday :as far]))

(defn dynamodb-ping
  "Ping DynamoDB"
  []
  {:environment ddb-env
   :tables (count (far/list-tables ddb-cred))})

