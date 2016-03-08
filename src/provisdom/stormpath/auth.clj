(ns provisdom.stormpath.auth
  (:require [provisdom.stormpath.util :as u :refer [success]])
  (:import (com.stormpath.sdk.authc UsernamePasswordRequest)
           (com.stormpath.sdk.resource ResourceException)))

(defn do-auth
  [application username password]
  (try
    (let [auth-req (UsernamePasswordRequest. username password)
          auth-result (.authenticateAccount application auth-req)]
      (success {:account (.getAccount auth-result)}))
    (catch ResourceException ex
      (u/resource-ex->map ex))))