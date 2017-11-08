(ns app.api.schema
  (:require [schema.core :as s]))

(s/defschema Message
  {:message s/Str})

(s/defschema DynamoDB
  {:environment s/Str
   :tables s/Int})

(s/defschema PostTest
  {:name s/Str
   (s/optional-key :description) s/Str
   :status (s/enum :current :preapproved)
   :origin {:state (s/enum :NY :CA :TX)
            :city s/Str}})

(s/defschema CreditCard
  {:user-id s/Str
   :app-id s/Str
   :enc-cc s/Str
   (s/optional-key :description) s/Str
   :trunc-pan s/Str})

(s/defschema Verified
  {:verified      s/Bool
   (s/optional-key :description) s/Str})
