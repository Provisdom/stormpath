(ns pro.stormpath.group
  (:import (com.stormpath.sdk.group Group GroupStatus)))

(defn status->group-status
  [status]
  (condp = status
    :enabled (GroupStatus/ENABLED)
    :disabled (GroupStatus/DISABLED)
    nil))

(defn create-group
  [client directory group-spec]
  (let [group (.instantiate client Group)]
    (doto group
      (.setName (:name group-spec))
      (.setDescription (:description group-spec))
      (.setStatus (-> group-spec :status status->group-status)))
    (.createGroup directory group)))