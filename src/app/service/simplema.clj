(ns app.service.simplema
  (:require [app.dynamodb.middlelayer :refer :all]
            [app.dynamodb.migration :refer :all]
            [app.api.utils.util :refer :all]
            [clojure.tools.logging :as log]
            ))


(defn calc-average
  [numbers]
  (/ (apply + numbers) (count numbers)))


(defn calc-simple-ma
  "Pull in latest price ts and check for previous minutes' prices, calc simple ma and
  store it, then sleep for a minute.
  Args:
   pair (string)
  "
  ([pair] (let [now-ts (query-max-date-prices pair)]
            (->> (map (fn [x] (Double/valueOf (:last x))) (query-pair-quotes-ddb pair (add-subx-min-ts now-ts -1) now-ts))
                 (calc-average) (log/info (str pair " Simple MA at time " (convert-ts-human-readable now-ts ))))
            ))

  ([pair ts] (->> (map (fn [x] (Double/valueOf (:last x))) (query-pair-quotes-ddb pair (+ ts 1) (add-subx-min-ts ts 1)))
                  (calc-average) (log/info (str pair " Simple MA at time " (convert-ts-human-readable (add-subx-min-ts ts 1))))
                  )))


(defn calc-simple-ma-main
  "Call calc-simple-ma to calculate last minute's simple moving
  average and then sleep for a minute
  "
  [pair]
  (while (> 1 0) (do (try
                       (log/info (str "Running simple Moving Average calculator on... " pair)) (calc-simple-ma pair) (Thread/sleep (* 60 1000))
                       (catch Exception e (log/error (str (.getMessage e))))))))