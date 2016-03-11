(ns provisdom.stormpath.oauth.google
  (:require
    #?(:clj [clj-http.client :as http])
    #?(:clj
            [cheshire.core :as json])
            [cemerick.url :as cu]
            [provisdom.stormpath.util :as u]))

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
  (-> base
      cu/url
      (update :query (fn [q]
                       (into {} (filter second (merge q (u/dash->underscore params))))))
      str))

;https://developers.google.com/identity/protocols/OpenIDConnect#authenticationuriparameters
(defn auth-url
  [base-url opts]
  (url base-url (merge {:response-type "code"
                        :scope         "openid email"}
                       (u/update-if opts :state pr-str))))

#?(:clj
   (defn validate-token!
     [token]
     (-> (http/get (-> token-validation-url
                       cu/url
                       (assoc-in [:query "id_token"] token)
                       str))
         :body
         (json/decode true))))