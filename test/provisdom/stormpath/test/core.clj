(ns provisdom.stormpath.test.core
  (:require [provisdom.stormpath.core :as s]
            [provisdom.stormpath.oauth :as oauth]))

(defonce client (s/client {:id     (System/getenv "STORMPATH_APP_ID")
                           :secret (System/getenv "STORMPATH_APP_SECRET")}))

(def application (s/application client "Postal Messenger"))

(comment
  (def jwt-map (oauth/access-token application "john@example.com" "Password1"))
  (oauth/validate-token application (str (:access-token jwt-map)) true)
  )