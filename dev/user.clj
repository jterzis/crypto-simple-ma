(ns user
  "Development user namespace not included in the jar"
  (:require [clojure
             [string :as str]
             [pprint :refer [pprint]]
             [test :refer :all]]
            [app.dynamodb
             [middlelayer :refer :all]]
            [app.api.utils.util :refer :all]
            [app.service
             [dynamodb :refer [ddb-cred ddb-env]]
             [http :refer [start-server stop-server]]
             [config :refer [load-config config]]]
            [environ.core :refer [env]]
            [taoensso.faraday :as far]
            [mount.core :as mount]
            [ns-tracker.core :as ns]))

(def track-src (ns/ns-tracker ["src" "test" "dev"]))

(defn go []
  (doseq [service (:started (mount/start))]
    (println "started:" service)))

(defn stop []
  (doseq [service (:stopped (mount/stop))]
    (println "stopped:" service)))

(defn reset []
  (stop)
  (go))

(defn refresh []
  (doseq [ns-sym (track-src)]
    (require ns-sym :reload)
    (println "reloaded:" ns-sym)))

(defn refresh-wait []
  (try
    (refresh)
    (catch Throwable e
      (.printStackTrace e)))
  (Thread/sleep 500))

(defn track []
  (doto
      (Thread.
       #(while true
          (refresh-wait)))
    (.setDaemon true)
    (.start)))
