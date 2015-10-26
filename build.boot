(set-env!
  :resource-paths #{"src"}
  :repositories [["clojars" "http://clojars.org/repo/"]
                 ["maven-central" "http://repo1.maven.org/maven2/"]]

  :dependencies '[[allgress/boot-tasks "0.2.3" :scope "test" :exclusions [commons-codec]]])

(require
  '[allgress.boot-tasks :refer :all])

(set-project-deps!)
(default-task-options!)