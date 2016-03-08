(ns provisdom.stormpath.util)

(defn fail
  [content]
  (merge {:success false} content))

(defn success
  [content]
  (merge {:success true} content))

(defn contains-many? [m & ks]
  (every? true? (map #(contains? m %) ks)))

(defn resource-ex->map
  [ex]
  (fail {:message           (.getMessage ex)
         :status            (.getStatus ex)
         :code              (.getCode ex)
         :more-info         (.getMoreInfo ex)
         :developer-message (.getDeveloperMessage ex)
         :trace             (.getStackTrace ex)}))

(defmacro doto-not-nil
  "Evaluates x then calls all of the methods and functions with the
  value of x supplied at the front of the given arguments. The arguments must not be nil.
  The forms are evaluated in order.  Returns x."
  [x & forms]
  (let [gx (gensym)]
    `(let [~gx ~x]
       ~@(map (fn [f]
                (if (seq? f)
                  `(when (and ~@(map (fn [y]
                                       `(some? ~y)) (rest f)))
                     (~(first f) ~x ~@(next f)))
                  `(~f ~gx)))
              forms)
       ~gx)))