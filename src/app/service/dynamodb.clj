(ns app.service.dynamodb
  (:require [mount.core :as mount]
            [app.service.config :refer [config]]))

(mount/defstate ddb-cred
  :start (get-in config [:dynamo :cred]))

(mount/defstate ddb-env
  :start (get-in config [:dynamo :ddb-env]))
