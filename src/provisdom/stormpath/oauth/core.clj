(ns provisdom.stormpath.oauth.core
  (:require [provisdom.stormpath.marshal :as m])
  (:import [com.stormpath.sdk.oauth Oauth2Requests Authenticators]
           [com.stormpath.sdk.resource ResourceException]
           [com.stormpath.sdk.application Application]))

(defn access-token
  [application username password]
  [{:pre [(instance? Application application) (string? username) (string? password)]}]
  (let [req-builder (.builder Oauth2Requests/PASSWORD_GRANT_REQUEST)
        req (doto req-builder
              (.setLogin username)
              (.setPassword password))]
    (-> Authenticators/PASSWORD_GRANT_AUTHENTICATOR
        (.forApplication application)
        (.authenticate (.build req))
        m/marshal)))

(defn validate-token
  "Validates a token. Takes the Stormpath application, the token you want to validate, and
  if you want to validate the token locally or with Stormpath. Default is locally to avoid extra
  http req to Stormpath.
  For more info: http://docs.stormpath.com/guides/token-management/#validating-access-tokens"
  ([application jwt-str] (validate-token application jwt-str true))
  ([application jwt-str local?]
   [{:pre [(instance? Application application) (string? jwt-str)]}]
   (let [jwt-req-builder (.builder Oauth2Requests/JWT_AUTHENTICATION_REQUEST)
         req (doto jwt-req-builder
               (.setJwt jwt-str))]
     (try
       (letfn [(do-local [a] (if local? (.withLocalValidation a) a))]
         (-> Authenticators/JWT_AUTHENTICATOR
             (.forApplication application)
             (do-local)
             (.authenticate (.build req))
             bean))
       (catch ResourceException ex
         (m/marshal ex))))))

(defn refresh-access-token
  [application refresh-token-str]
  [{:pre [(instance? Application application) (string? refresh-access-token)]}]
  (let [builder (.builder Oauth2Requests/REFRESH_GRANT_REQUEST)
        req (.setRefreshToken builder refresh-token-str)]
    (-> Authenticators/REFRESH_GRANT_AUTHENTICATOR
        (.forApplication application)
        (.authenticate (.build req))
        m/marshal)))

(defn revoke-access-token
  [jwt-map]
  [{:pre [(map? jwt-map)]}]
  (-> jwt-map :obj .getAccessToken .delete))

(defn revoke-refresh-token
  [jwt-map]
  [{:pre [(map? jwt-map)]}]
  (-> jwt-map :obj .getRefreshToken .delete))