(ns provisdom.stormpath.marshal
  (:import [com.stormpath.sdk.oauth OauthGrantAuthenticationResult JwtAuthenticationResult]
           [com.stormpath.sdk.account Account AccountStatus]
           [com.stormpath.sdk.group GroupList Group GroupStatus]
           [com.stormpath.sdk.resource ResourceException]
           [com.stormpath.sdk.impl.resource AbstractResource]))

(defmulti ^:private marshal*
          "Marshalls the given object based on its class" class)

(defmethod marshal* :default [obj] obj)

(defmethod marshal* OauthGrantAuthenticationResult
  [obj]
  {:access-token                (.getAccessTokenString obj)
   :refresh-token               (.getRefreshTokenString obj)
   :token-type                  (.getTokenType obj)
   :expires-in                  (.getExpiresIn obj)
   :stormpath-access-token-href (.getAccessTokenHref obj)})

(defmethod marshal* JwtAuthenticationResult
  [obj]
  {:account     (.getAccount obj)
   :application (.getApplication obj)
   :jwt         (.getJwt obj)
   :href        (.getHref obj)})

(defmethod marshal* Account
  [obj]
  {:username                 (.getUsername obj)
   :email                    (.getEmail obj)
   :fname                    (.getGivenName obj)
   :mname                    (.getMiddleName obj)
   :lname                    (.getSurname obj)
   :status                   (.getStatus obj)
   :groups                   (.getGroups obj)
   :directory                (.getDirectory obj)
   :tenant                   (.getTenant obj)
   :email-verification-token (.getEmailVerificationToken obj)
   :refresh-tokens           (.getRefreshTokens obj)
   :custom-data              (.getCustomData obj)
   :created-at               (.getCreatedAt obj)
   :modified-at              (.getModifiedAt obj)})

(defmethod marshal* AccountStatus
  [obj]
  (condp = obj
    AccountStatus/ENABLED :enabled
    AccountStatus/DISABLED :disabled
    AccountStatus/UNVERIFIED :unverifed
    nil))

(defmethod marshal* GroupList
  [obj]
  ())

(defmethod marshal* Group
  [obj]
  {:name        (.getName obj)
   :description (.getDescription obj)
   :status      (.getStatus obj)})

(defmethod marshal* GroupStatus
  [obj]
  (condp = obj
    GroupStatus/ENABLED :enabled
    GroupStatus/DISABLED :disabled
    nil))

(defmethod marshal* ResourceException
  [obj]
  {:message           (.getMessage obj)
   :status            (.getStatus obj)
   :code              (.getCode obj)
   :more-info         (.getMoreInfo obj)
   :developer-message (.getDeveloperMessage obj)
   :trace             (.getStackTrace obj)})

(defn materialized?
  [obj]
  (let [field (.getDeclaredField AbstractResource "materialized")
        _ (.setAccessible field true)
        value (.get field obj)]
    (.setAccessible field false)
    (boolean value)))

(defn marshal
  [obj]
  (let [marshalled (marshal* obj)]
    (if (map? marshalled)
      (assoc
        (into {}
              (map (fn [[k v]]
                     [k
                      (if (instance? AbstractResource v)
                        (if (materialized? v)
                          (marshal v)
                          v)
                        (marshal v))]) marshalled))
        :obj obj)
      marshalled)))

(defmacro obj->map [o & bindings]
  (let [s (gensym "local")]
    `(let [~s ~o]
       ~(->> (partition 2 bindings)
             (map (fn [[k v]]
                    (if (vector? v)
                      [k (list (last v) (list (first v) s))]
                      [k (list v s)])))
             (into {})))))