(ns provisdom.stormpath.oauth
  (:require [provisdom.stormpath.util :as u])
  (:import [com.stormpath.sdk.oauth Oauth2Requests Authenticators]
           (com.stormpath.sdk.resource ResourceException)))

(defn access-token
  [application username password]
  (let [req-builder (.builder Oauth2Requests/PASSWORD_GRANT_REQUEST)
        req (doto req-builder
              (.setLogin username)
              (.setPassword password))]
    (-> Authenticators/PASSWORD_GRANT_AUTHENTICATOR
        (.forApplication application)
        (.authenticate (.build req))
        (u/obj->map :access-token .getAccessTokenString
                    :refresh-token .getRefreshTokenString
                    :token-type .getTokenType
                    :expires-in .getExpiresIn
                    :stormpath-access-token-href .getAccessTokenHref))))

(defn validate-token
  "Validates a token. Takes the Stormpath application, the token you want to validate, and
  if you want to validate the token locally or with Stormpath. Default is locally to avoid extra
  http req to Stormpath.
  For more info: http://docs.stormpath.com/guides/token-management/#validating-access-tokens"
  ([application jwt-str] (validate-token application jwt-str true))
  ([application jwt-str local?]
   [{:pre [(string? jwt-str)]}]
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
         (.getCode ex))))))