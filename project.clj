(defproject lingo "0.1.0-SNAPSHOT"
  :description "A Clojure natural language generator built on top of Google's simplenlg library."
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [criterium "0.4.3"]
                 [org.clojure/core.match "0.2.1"]]
  :jvm-opts ^:replace ["-server"]
  :java-source-paths ["src/simplenlg"]
  :resource-paths ["lib/simplenlg-v4.4.2.jar"
                   "lib/junit-4.4.jar"
                   "lib/lexAccess2011dist.jar"
                   "lib/lexCheck2006api.jar"
                   "lib/lvg2001api.jar"]
  :repl-options {:port 12345})
