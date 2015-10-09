(ns pro.stormpath.directory
  (:require [pro.stormpath.core :refer [get-tenant]]
            [pro.stormpath.util :refer [doto-not-nil]])
  (:import (com.stormpath.sdk.directory Directory DirectoryStatus)))

(defn status->directory-status
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

(defn create-directory
  "Creates a directory with the spec:
  `:name`: The name of the directory to create
  `:description`: The description of the directory
  `:status`: Either :enabled or :disabled"
  ([client tenant spec]
   (let [directory (.instantiate client Directory)]
     (set-directory-spec directory spec)
     (.createDirectory tenant directory)))
  ([client spec]
   (create-directory client (get-tenant client) spec)))

(defn update-directory
  "Updates a directory"
  [directory spec]
  (-> directory
      (set-directory-spec spec)
      .save))