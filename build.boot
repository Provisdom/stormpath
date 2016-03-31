(set-env!
  :resource-paths #{"src"}
  :wagons '[[s3-wagon-private "1.2.0"]]
  :repositories [["clojars" "http://clojars.org/repo/"]
                 ["maven-central" "http://repo1.maven.org/maven2/"]
                 ["provisdom" {:url        "s3p://provisdom-artifacts/releases/"
                               :username   (System/getenv "AWS_ACCESS_KEY")
                               :passphrase (System/getenv "AWS_SECRET_KEY")}]]

  :dependencies '[[provisdom/boot-tasks "0.4.1" :scope "test" :exclusions [commons-codec]]])

(require
  '[provisdom.boot-tasks :refer :all])

(set-project-deps!)
(default-task-options!)