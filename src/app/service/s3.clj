(ns app.service.s3
  (:require [mount.core :as mount]
            [amazonica.aws.s3 :as s3]
            [app.service.config :refer [config]]))

(defn s3-client
  ""
  []
  (let [cred (get-in config [:aws-s3 :cred])
        bucket (get-in config [:aws-s3 :bucket])]
    (s3/set-s3client-options cred :path-style-access true)
    {:cred cred :bucket bucket}))

(mount/defstate s3-config
  :start (s3-client))
