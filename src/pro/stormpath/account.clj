(ns pro.stormpath.account
  (:require [pro.stormpath.core :as storm]
            [clojure.stacktrace :as stack]
            [pro.stormpath.util :as u])
  (:import (com.stormpath.sdk.account Account Accounts)
           (com.stormpath.sdk.resource ResourceException)))

(defn account-from-username
  "Gets an account from a username. Returns an `Account` object."
  [application username]
  (let [criteria (Accounts/where (.. Accounts (username) (eqIgnoreCase username)))]
    (.. application (getAccounts criteria) (single))))

(defn- set-account-spec
  [account account-spec]
  (doto account
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
      (.createAccount application account)
      (catch ResourceException ex
        (u/resource-ex->map ex)))))

(defn update-account
  "Updates a user account. Calls the Stormpath `.save()` function."
  [account account-spec]
  (let [account (set-account-spec account account-spec)]
    (try
      (.save account)
      (catch ResourceException ex
        ex))))

(defn delete-account
  [account]
  (.delete account))