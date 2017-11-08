(ns app.server
  (:import [cryptoquoteapp PoloniexQuoteApp])
  (:gen-class)
  (:require [app.dynamodb.migration :as ddb]
            [app.service
             [config :refer [config]]
             [dynamodb :refer [ddb-cred ddb-env]]
             [http :refer [http-server]]
             [s3 :refer [s3-config]]]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [mount.core :as mount]))

(defn stop-app
  "Stop application"
  []
  (doseq [service (:stopped (mount/stop))]
    (log/info service "stopped!"))
  (shutdown-agents))

(defn start-app
  "Start application"
  []
  (try
    (doseq [service (:started (mount/start))]
      (log/info service "started!"))
    (catch Exception e
      (log/error (str "service start exception: "(.getMessage e)))))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn dynamo-migrate
  "Migrate DynamoDB tables. Options:
   dynamo-list "
  [migration]
  (mount/start #'app.service.config/config
               #'app.service.dynamodb/ddb-cred
               #'app.service.dynamodb/ddb-env)
  (cond
    (= migration "dynamo-list") (ddb/table-list))
  (mount/stop))

(defn -main [& args]
  (if-let [arg (first args)]
    (cond
      (str/starts-with? arg "dynamo") (dynamo-migrate arg)
      :else (do (future (-> (PoloniexQuoteApp.) (.runClient arg)))))
    (start-app)))
