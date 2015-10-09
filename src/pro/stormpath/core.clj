(ns pro.stormpath.core
  (:import [com.stormpath.sdk.client Clients]
           [com.stormpath.sdk.api ApiKeys]
           [com.stormpath.sdk.application Applications]))

(def home (System/getProperty "user.home"))
(def path (str home "/.stormpath/apiKey-1HW331LGYY2V70AUO2JLF73S6.properties"))
(def app-name "My Application")

(defn build-api-key
  "Builds a Stormpath API key"
  [path]
  (.. ApiKeys (builder) (setFileLocation path) (build)))

(defn build-client
  "Builds a Stormpath client. The client object should be passed around the application.
  IMPORTANT:
  The client instance is intended to be an application singleton. You should reuse this instance throughout your
  application code. You should not create multiple Client instances as it could negatively affect caching."
  [api-key]
  (.. Clients (builder) (setApiKey api-key) (build)))

(defn get-tenant
  [client]
  (.getCurrentTenant client))

(defn get-tenant-application
  "Gets an application for a given tenant."
  [tenant name]
  (let [applications (.. tenant (getApplications (Applications/where (.. Applications (name) (eqIgnoreCase name)))))]
    (.. applications (iterator) (next))))

(defn get-application
  "Gets an application for the current tenant."
  [client name]
  (get-tenant-application (get-tenant client) name))