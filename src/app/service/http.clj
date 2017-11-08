(ns app.service.http
  (:require [mount.core :as mount]
            [app.api.routes :refer [api-routes]]
            [app.service.config :refer [config]]
            [immutant.web :as web]))

(defn start-server
  "Start http server"
  [http-config]
  (web/run #(api-routes %) http-config))

(defn stop-server
  "Stop http server"
  [server]
  (web/stop server))

(mount/defstate http-server
  :start (start-server (:http config))
  :stop (stop-server http-server))

