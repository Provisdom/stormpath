(ns provisdom.stormpath.oauth.google
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [cemerick.url :as cu]
            [provisdom.stormpath.util :as u]))

;https://developers.google.com/identity/protocols/OpenIDConnect#discovery
(def discovery-doc-url "https://accounts.google.com/.well-known/openid-configuration")

(defonce discovery-document (-> discovery-doc-url http/get :body (json/parse-string true)))
(def auth-req-base-url (:authorization_endpoint discovery-document))

(defn auth-url {}
  [base-url
   {:keys [client-id response-type scope
           redirect-uri state login-hint]
    :or   {response-type "code"
           scope         "openid email"}}]
  (-> base-url
      cu/url
      (update :query #(u/assoc-if %
                                  :client_id client-id
                                  :response-type response-type
                                  :scope scope
                                  :redirect_uri redirect-uri
                                  :state state
                                  :login_hint login-hint))
      str))