(ns provisdom.stormpath.group
  (:require [provisdom.stormpath.util :refer [doto-not-nil]]
            [provisdom.stormpath.util :as u]
            [provisdom.stormpath.marshal :as m])
  (:import (com.stormpath.sdk.group Group GroupStatus)
           (com.stormpath.sdk.resource ResourceException)))

(defn status->group-status
  [status]
  (condp = status
    :enabled (GroupStatus/ENABLED)
    :disabled (GroupStatus/DISABLED)
    nil))

(defn get-group
  "Returns a `Group` object"
  [directory name]
  (let [groups-it (-> directory (.getGroups) (.iterator))]
    (loop [group (.next groups-it)]
      (if (.. group (getName) (equals name))
        group
        (if (.hasNext groups-it)
          (recur (.next groups-it))
          nil)))))

(defn create-group
  [client directory group-spec]
  (try
    (let [group (.instantiate client Group)]
      (doto-not-nil group
                    (.setName (:name group-spec))
                    (.setDescription (:description group-spec))
                    (.setStatus (-> group-spec :status status->group-status)))
      (.createGroup directory group))
    (catch ResourceException ex
      (m/marshal ex))))

(defn delete-group
  [group]
  (.delete group))