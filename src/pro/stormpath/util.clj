(ns pro.stormpath.util)

(defn- fail
  [content]
  (merge {:success false} content))

(defn resource-ex->map
  [ex]
  (fail {:message           (.getMessage ex)
         :developer-message (.getDeveloperMessage ex)
         :trace             (.getStackTrace ex)}))

(defmacro doto-not-nil
  "Evaluates x then calls all of the methods and functions with the
  value of x supplied at the front of the given arguments. The value you are setting must not
  be nil. The forms
  are evaluated in order.  Returns x."
  {:added "1.0"}
  [x & forms]
  (let [gx (gensym)]
    `(let [~gx ~x]
       ~@(map (fn [f]
                (if (seq? f)
                  `(when (some? ~(second f))
                     (~(first f) ~x ~@(next f)))
                  `(~f ~gx)))
              forms)
       ~gx)))
