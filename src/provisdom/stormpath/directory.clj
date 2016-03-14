(ns provisdom.stormpath.directory
  (:require [provisdom.stormpath.core :refer [get-tenant]]
            [provisdom.stormpath.util :refer [doto-not-nil]]
            [provisdom.stormpath.marshal :as m]
            [provisdom.stormpath.util :as u])
  (:import [com.stormpath.sdk.directory Directory DirectoryStatus Directories]
           [com.stormpath.sdk.provider Providers]
           [com.stormpath.sdk.resource ResourceException]))

(defn- status->directory-status
  [status]
  (condp = status
    :enabled (DirectoryStatus/ENABLED)
    :disabled (DirectoryStatus/DISABLED)
    (DirectoryStatus/ENABLED)))

(defn- set-directory-spec
  [directory spec]
  (doto-not-nil directory
                (.setName (:name spec))
                (.setDescription (:description spec))
                (.setStatus (-> spec :status status->directory-status))))

(defn get-directory
  "Gets a directory given a `name`."
  [client name]
  (let [directories (.getDirectories (get-tenant client) (Directories/where (.. Directories (name) (eqIgnoreCase name))))]
    (first directories)))

(defn- create-director-req
  [directory opts]
  (let [req (Directories/newCreateRequestFor directory)
        provider (-> (u/provider-for (:type opts))
                     (.builder)
                     (.setClientId (:id opts))
                     (.setClientSecret (:secret opts))
                     (.setRedirectUri (:redirect-uri opts))
                     (.build))]
    (.. req (forProvider provider) (build))))

(defn create-directory
  "Creates a directory with the spec:
  `:name`: The name of the directory to create
  `:description`: The description of the directory
  `:status`: Either :enabled or :disabled"
  ([client spec]
   (create-directory client spec nil))
  ([client spec opts]
   (create-directory client (get-tenant client) spec opts))
  ([client tenant spec opts]
   (let [directory (.instantiate client Directory)
         _ (set-directory-spec directory spec)
         directory (if opts
                     (create-director-req directory opts)
                     directory)]
     (try
       (.createDirectory tenant directory)
       (catch ResourceException ex (m/marshal ex))))))

(defn update-directory
  "Updates a directory"
  [directory spec]
  (-> directory
      (set-directory-spec spec)
      .save))

(defn delete-directory
  [directory]
  (.delete directory))