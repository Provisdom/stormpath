(ns provisdom.stormpath.test.core
  (:require [provisdom.stormpath.core :as s]
            [provisdom.stormpath.oauth.core :as oauth]
            [provisdom.stormpath.directory :as dir]))

(defonce client (s/client {:id     (System/getenv "STORMPATH_APP_ID")
                           :secret (System/getenv "STORMPATH_APP_SECRET")}))

(defonce application (s/application client "Test"))

(comment
  (def jwt-map (oauth/access-token application "john@example.com" "Password1"))
  (oauth/validate-token application (str (:access-token jwt-map)) true)

  (def goog-dir (dir/create-directory client
                                      {:name        "google-directory"
                                       :description "a google directory"}
                                      {:type         :google
                                       :id           (System/getenv "GOOG_CLIENT_ID")
                                       :secret       (System/getenv "GOOG_CLIENT_SECRET")
                                       :redirect-uri "postmessage"}))
  (def goog-dir (dir/get-directory client "google-directory"))
  )