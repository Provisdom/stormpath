(ns provisdom.stormpath.oauth.google
  (:require
    #?(:clj [cheshire.core :as json])
    #?(:clj
            [provisdom.stormpath.marshal :as m])
    #?(:clj
            [clj-http.client :as http])
            [cemerick.url :as cu]
            [provisdom.stormpath.util :as u])
  #?(:clj
     (:import [com.stormpath.sdk.application Application])))

;https://developers.google.com/identity/protocols/OpenIDConnect#discovery
#?(:clj
   (do
     (def discovery-doc-url "https://accounts.google.com/.well-known/openid-configuration")

     (defonce discovery-document (-> discovery-doc-url http/get :body (json/parse-string true)))
     (def auth-req-base-url (:authorization_endpoint discovery-document))
     (def token-endpoint (:token_endpoint discovery-document))
     (def token-validation-url "https://www.googleapis.com/oauth2/v3/tokeninfo")))

(defn url
  [base params]
  {:pre [(string? base) (map? params)]}
  (-> base
      cu/url
      (update :query (fn [q]
                       (into {} (filter second (merge q (u/dash->underscore params))))))
      str))

;https://developers.google.com/identity/protocols/OpenIDConnect#authenticationuriparameters
(defn auth-url
  [base-url opts]
  {:pre [(string? base-url) (map? opts)]}
  (url base-url (merge {:response-type "code"
                        :scope         "openid email"}
                       (u/update-if opts :state pr-str))))

#?(:clj
   ;; TODO: Add local? option where validation will occur locally w/o another http req
   (defn validate-token!
     [token]
     {:pre [(string? token)]}
     (-> (http/get (-> token-validation-url
                       cu/url
                       (assoc-in [:query "id_token"] token)
                       str))
         :body
         (json/decode true))))

#?(:clj
   (defn account
     [application code]
     {:pre [(instance? Application application) (string? code)]}
     (let [req (-> (u/provider-for :google) (.account) (.setCode code) (.build))]
       (-> application
           (.getAccount req)
           (.getAccount)
           m/marshal))))