(ns provisdom.stormpath.core
  (:require [provisdom.stormpath.util :as u])
  (:import [com.stormpath.sdk.client Clients Client]
           [com.stormpath.sdk.api ApiKeys]
           [com.stormpath.sdk.application Applications]
           [com.stormpath.sdk.accountStoreMapping AccountStoreMapping]
           (com.stormpath.sdk.impl.application DefaultApplicationAccountStoreMapping)))

(defn get-tenant [client] (.getCurrentTenant client))

(defn client
  "Builds a Stormpath client given a name and a creds map containing {:id \"yourid\" :secret \"your secret\"}. The
  client object should be passed around the application.
  IMPORTANT:
  The client instance is intended to be an application singleton. You should reuse this instance throughout your
  application code. You should not create multiple Client instances as it could negatively affect caching."
  [creds]
  [{:pre [(map? creds) (u/contains-many? creds :id :secret)]}]
  (let [{:keys [id secret]} creds
        api-key (.. ApiKeys (builder) (setId id) (setSecret secret) (build))]
    (.. Clients (builder) (setApiKey api-key) (build))))

(defn application
  [client name]
  [{:pre [(instance? Client client) (string? name)]}]
  (let [tenant (.getCurrentTenant client)
        apps (.. tenant (getApplications (Applications/where (.. Applications (name) (eqIgnoreCase name)))))]
    (.. apps (iterator) (next))))

(defn map-account-store
  [client application account-store]
  ;; TODO: For some reason I get a UnknownClassException when instantiating AccountStoreMapping
  (let [mapping (doto (.instantiate client DefaultApplicationAccountStoreMapping)
                  (.setAccountStore account-store)
                  (.setApplication application))]
    (.createAccountStoreMapping application mapping)))