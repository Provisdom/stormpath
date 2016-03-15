(ns provisdom.stormpath.auth
  (:require [provisdom.stormpath.util :as u :refer [success]]
            [provisdom.stormpath.marshal :as m])
  (:import [com.stormpath.sdk.authc UsernamePasswordRequest]
           [com.stormpath.sdk.resource ResourceException]
           [com.stormpath.sdk.application Application]))

(defn do-auth
  [application username password]
  {:pre [(instance? Application application) (string? username) (string? password)]}
  (try
    (let [auth-req (UsernamePasswordRequest. username password)
          auth-result (.authenticateAccount application auth-req)]
      (success {:account (.getAccount auth-result)}))
    (catch ResourceException ex
      (m/marshal ex))))