(ns app.api.routes
  (:require [app.api
             [health :as health]
             [schema :refer :all]]
            [app.api.utils.util :as util]
            [compojure.api.sweet :refer :all]
            [ring.util
             [http-response :refer :all]
             [response :as resp]])
  (:import java.io.ByteArrayInputStream))

(defn wrap-cors
  "Allow requests from all origins"
  [handler]
  (let [cors-headers {"Access-Control-Allow-Origin" "*"
                      "Access-Control-Allow-Headers" "Content-Type"
                      "Access-Control-Allow-Methods" "GET,POST" }]
    (fn [request]
      (let [response (handler request)]
        (update-in response [:headers] merge cors-headers)))))

(defapi api-routes
  {:swagger
   {:ui "/"
    :spec "/swagger.json"
    :data {:info {:title "Crypto Quotron Microservice"
                  :description "Produces and stores 1 minute average of Poloniex prices"}
           :tags [{:name "api" :description "service root api"}
                  {:name "example" :description "example api"}
                  {:name "health" :description "service health checks api"}]}}}

  (context "/api" [] :tags ["api"]
           :middleware [wrap-cors]

           (GET "/about" []
                :return Message
                :summary "service description"
                (ok {:message "Crypto Simpla MA data service"}))

           (context "/health" [] :tags ["health"]
                    (GET "/http-ping" []
                         :return Message
                         :summary "ping http server"
                         (ok {:message "http server running"}))

                    (GET "/dynamodb-ping" []
                         :return DynamoDB
                         :summary "ping dynamodb"
                         (ok (health/dynamodb-ping)))

                    (POST "/echo" []
                          :return PostTest
                          :body [request PostTest]
                          :summary "echoes a post request"
                          (ok request)))))
