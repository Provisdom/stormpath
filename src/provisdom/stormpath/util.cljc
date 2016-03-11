(ns provisdom.stormpath.util
  (:require [clojure.set :as cs]
            [clojure.string :as str]))

(defn fail
  [content]
  (merge {:success false} content))

(defn success
  [content]
  (merge {:success true} content))

(defn contains-many? [m & ks]
  (every? true? (map #(contains? m %) ks)))

(defn assoc-if
  ([map key val] (if val (assoc map key val) map))
  ([map key val & kvs]
   (let [ret (assoc-if map key val)]
     (if (empty? kvs)
       ret
       (if (next kvs)
         (apply assoc-if ret (first kvs) (second kvs) (nnext kvs))
         (let [msg "assoc expects even number of arguments after map/vector, found odd number"]
           (throw #?(:clj  (IllegalArgumentException. msg)
                     :cljs (js/Error. msg)))))))))

(defn update-if
  [m k f]
  (if-let [v (get m k)]
    (assoc-if m k (f v))
    m))

(defn map_->-
  [m]
  (let [ks (keys m)]
    (cs/rename-keys m (into {}
                            (map (fn [k]
                                   [k (-> k name (str/replace "_" "-") keyword)]) ks)))))

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