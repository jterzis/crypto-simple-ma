(ns app.service.config
  (:require [environ.core :refer [env]]
            [mount.core :as mount]))

(defn load-config
  "Set system wide configuration."
  []
  {:http {:host (env :crypto-host)
          :port (Integer/parseInt (env :crypto-port))}
   :aws-s3 {:cred {:access-key (env :aws-access-key-id)
                   :secret-key (env :aws-secret-access-key)
                   :endpoint (env :aws-region)}}
   :dynamo {:cred {:access-key (env :aws-access-key-id)
                   :secret-key (env :aws-secret-access-key)
                   :endpoint (env :crypto-dynamodb-url)}
            :ddb-env (env :crypto-dynamodb-env)}})

(mount/defstate config
  :start (load-config)
  :stop nil)
