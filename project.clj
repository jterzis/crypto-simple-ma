(defproject crypto-quotron "1.0.0"
  :description "Crypto Quote Microservice"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 ;; Logging
                 [org.clojure/tools.logging "0.3.1"]
                 [ch.qos.logback/logback-classic "1.1.7"]
                 ;; Web
                 [org.immutant/web "2.1.6"
                  :exclusions [ch.qos.logback/logback-classic]]
                 [metosin/compojure-api "1.1.10"]
                 [prismatic/schema "1.1.3"]
                 ;; DynamoDB
                 [com.taoensso/faraday "1.9.0"
                  :exlcusions [com.taoensso/nippy]]
                 ;; AWS
                 [amazonica "0.3.48"
                  :exclusions [com.amazonaws/aws-java-sdk
                               com.amazonaws/amazon-kinesis-client]]
                 [com.amazonaws/aws-java-sdk-core "1.10.77"
                  :exclusions [com.fasterxml.jackson.core/jackson-annotations]]
                 [com.amazonaws/aws-java-sdk-s3 "1.10.77"]
                 ;; Utils
                 [environ "1.1.0"]
                 [mount "0.1.10"]
                 [ns-tracker "0.3.1"]
                 ;; Data Structures
                 [com.google.guava/guava "21.0"]
                 ;; Crypto
                 [buddy/buddy-hashers "1.2.0"]
                 ;; Serialization
                 [com.taoensso/nippy "2.13.0"]
                 ;; WAMP Exchange Data Layer
                 [org.johnterzis.websockets/websocket-client "1.1"]
                 ]

  :uberjar-name "server.jar"
  :offline? false
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]
                                  [ring/ring-mock "0.3.0"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}
                   :plugins [[ikitommi/lein-ring "0.9.8-FIX"]]}
             :uberjar {:aot :all}}

  :main ^:skip-aot app.server)
