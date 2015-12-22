(set-env!
  :resource-paths #{"src"}
  :repositories [["clojars" "http://clojars.org/repo/"]
                 ["maven-central" "http://repo1.maven.org/maven2/"]]

  :dependencies '[[provisdom/boot-tasks "0.3.0" :scope "test" :exclusions [commons-codec]]])

(require
  '[provisdom.boot-tasks :refer :all])

(set-project-deps!)
(default-task-options!)