(ns app.dynamodb.migration
  (:require [app.service.dynamodb :refer [ddb-cred ddb-env]]
            [clojure.tools.logging :as log]
            [taoensso.faraday :as far]))

(defn table-list
  "List DynamoDB tables"
  []
  (log/info "Tables:" (far/list-tables ddb-cred)))
