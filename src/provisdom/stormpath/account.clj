(ns provisdom.stormpath.account
  (:require [provisdom.stormpath.util :as u :refer [doto-not-nil success]])
  (:import (com.stormpath.sdk.account Account Accounts)
           (com.stormpath.sdk.resource ResourceException)))

(defn account-from-username
  "Gets an account from a username. Returns an `Account` object."
  [application username]
  (let [criteria (Accounts/where (.. Accounts (username) (eqIgnoreCase username)))]
    (.. application (getAccounts criteria) (single))))

(defn- set-account-spec
  [account account-spec]
  (doto-not-nil account
                (.setGivenName (:fname account-spec))
                (.setSurname (:lname account-spec))
                (.setEmail (:email account-spec))
                (.setPassword (:password account-spec))))

(defn- make-account
  [client]
  (.instantiate client Account))

(defn create-account
  "Creates an account with the spec:
  `:fname`: Given name
  `:lname`: Surname
  `:email`: Email
  `:password` Password"
  [client application account-spec]
  (let [account (-> client
                    make-account
                    (set-account-spec account-spec))]
    (try
      (success {:account (.createAccount application account)})
      (catch ResourceException ex
        (u/resource-ex->map ex)))))

(defn update-account
  "Updates a user account. Calls the Stormpath `.save()` function."
  [account account-spec]
  (let [account (set-account-spec account account-spec)]
    (try
      (success (.save account))
      (catch ResourceException ex
        (u/resource-ex->map ex)))))

(defn delete-account
  [account]
  (try
    (success (.delete account))
    (catch ResourceException ex
      (u/resource-ex->map ex))))

(defn get-groups
  [account]
  (.getGroups account))

(defn get-group-names
  [account]
  (map (fn [group]
         (.getName group)) (get-groups account)))