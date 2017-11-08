(ns app.api.utils.util
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
