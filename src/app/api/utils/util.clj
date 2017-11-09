(ns app.api.utils.util
  (:require [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-time.coerce :as c])
  (:import java.sql.Timestamp
           java.text.SimpleDateFormat
           [java.util Base64 Date TimeZone UUID]))

(defn encode64
  "Encode Base64"
  [to-encode]
  (.encodeToString (Base64/getEncoder) to-encode))

(defn decode64
  "Decode Base64"
  [to-decode]
  (.decode (Base64/getDecoder) to-decode))

(defn uuid
  "Generate random UUID"
  []
  (.toString (UUID/randomUUID)))

(defn update-each
  "Updates each keyword ks in m using fn"
  [m ks fn]
  (reduce #(update-in %1 [%2] fn) m ks))

(defn format-date
  "Convert java date to YYYY-MM-DD string"
  [date]
  (.format (SimpleDateFormat. "yyyy-MM-dd") date))

(defn today
  "Current string date in YYYY-MM-DD format"
  []
  (format-date (Date.)))

(defn utc-now
  "Current time stamp"
  []
  (let [formatter (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss zzz")]
    (.setTimeZone formatter (TimeZone/getTimeZone "UTC"))
    (.format formatter (Date.))))

(defn add-subx-min-ts
  "Add/subtract minutes from a unix timestamp
  Args:
  ts (clojure.lang.BigInt) timestamp
  x (int) num minutes to add sub (-)
  "
  [ts x]
  (+ ts (* 1000 (* 60 x))))

(def built-in-formatter (f/formatters :basic-date-time))

(def custom-formatter (f/formatter :date-hour-minute-second))

(defn to-dte-from-long
  [ts]
  (c/from-long ts))

(defn convert-ts-human-readable
  "Convert a unix timestamp in ms
  to human readable date time string
  Args:
  ts: clojure.lang.BigInt
  parsesr: timestamp parser"
  [ts parser]
  (->> (to-dte-from-long ts) (f/unparse parser)))

