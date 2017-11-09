(ns app.service.config
  (:require [environ.core :refer [env]]
            [mount.core :as mount]))

(defn load-config
  "Set system wide configuration."
  []
  {:http {:host "0.0.0.0"
          :port (Integer/parseInt "5050")}
   :aws-s3 {:cred {:access-key (env :aws-access-key-id)
                   :secret-key (env :aws-secret-access-key)
                   :endpoint (env :aws-region)}}
   :dynamo {:cred {:access-key (env :aws-access-key-id)
                   :secret-key (env :aws-secret-access-key)
                   :endpoint "http://localhost:9000"}
            :ddb-env "local"}})

(mount/defstate config
  :start (load-config)
  :stop nil)
