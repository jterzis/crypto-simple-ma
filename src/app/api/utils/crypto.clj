(ns app.api.utils.crypto
  (:require [buddy.hashers :as hashers]))

(defn hash-str
  "Hash name using the `buddy` library.
  Using the bcrypt+blake2b-512 algorithm
  with 2**12 iterations and a random salt."
  [input]
  (hashers/derive input {
    :alg :bcrypt+blake2b-512
    :iterations 12}))

(defn str-matches-hash?
  "Check input-str against input-hash"
  [in-str in-hash]
  (hashers/check in-str in-hash))
