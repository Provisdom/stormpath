(ns provisdom.stormpath.marshall
  (:import [com.stormpath.sdk.oauth Oauth2AuthenticationResult]))

(def marshalls
  {Oauth2AuthenticationResult {:access-token .getAccessTokenString
                               :refresh-token .getRefreshTokenString
                               :token-type .getTokenType
                               :expires-in .getExpiresIn
                               :stormpath-access-token-href .getAccessTokenHref}})

(defmacro obj->map [o & bindings]
  (let [s (gensym "local")]
    `(let [~s ~o]
       ~(->> (partition 2 bindings)
             (map (fn [[k v]]
                    (if (vector? v)
                      [k (list (last v) (list (first v) s))]
                      [k (list v s)])))
             (into {})))))