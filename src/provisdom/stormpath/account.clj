(ns provisdom.stormpath.account
  (:require [provisdom.stormpath.util :as u :refer [doto-not-nil success]]
            [provisdom.stormpath.marshal :as m])
  (:import (com.stormpath.sdk.account Account Accounts)
           (com.stormpath.sdk.resource ResourceException)
           (com.stormpath.sdk.application Application)
           (com.stormpath.sdk.client Client)))

(def account-whitelist-keys [:fname :lname :email :password :username])

(defn account-from-username
  "Gets an account from a username. Returns an `Account` object."
  [application username]
  {:pre [(instance? Application application) (string? username)]}
  (let [criteria (Accounts/where (.. Accounts (username) (eqIgnoreCase username)))]
    (.. application (getAccounts criteria) (single))))

(defn- set-account-spec
  [account opts]
  {:pre [(instance? Account account) (map? opts)]}
  (let [account (doto-not-nil account
                              (.setGivenName (:fname opts))
                              (.setSurname (:lname opts))
                              (.setEmail (:email opts))
                              (.setUsername (:username opts))
                              (.setPassword (:password opts)))
        custom-data (.getCustomData account)]
    (doseq [[k v] (apply dissoc opts account-whitelist-keys)]
      (.put custom-data k v))))

(defn- make-account
  [client]
  (.instantiate client Account))

(defn create-account
  "Creates an account with the spec for `opts` below:
  `:fname`: Given name
  `:lname`: Surname
  `:email`: Email
  `:password` Password
  `:username`: Optional, defaults to email if unset
  And any custom data you want (up to 10MB per user)"
  [client application opts]
  {:pre [(instance? Client client) (instance? Application application) (map? opts)]}
  (let [account (-> client
                    make-account
                    (set-account-spec opts))]
    (try
      (success {:account (.createAccount application account)})
      (catch ResourceException ex
        (m/marshal ex)))))

(defn update-account
  "Updates a user account. Calls the Stormpath `.save()` function."
  [account account-spec]
  {:pre [(instance? Account account) (map? account-spec)]}
  (let [account (set-account-spec account account-spec)]
    (try
      (success (.save account))
      (catch ResourceException ex
        (m/marshal ex)))))

(defn delete-account
  [account]
  {:pre [(instance? Account account)]}
  (try
    (success (.delete account))
    (catch ResourceException ex
      (m/marshal ex))))

(defn get-groups
  [account]
  {:pre [(instance? Account account)]}
  (.getGroups account))

(defn get-group-names
  [account]
  {:pre [(instance? Account account)]}
  (map (fn [group]
         (.getName group)) (get-groups account)))