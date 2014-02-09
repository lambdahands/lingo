(defproject lingo "0.1.0-SNAPSHOT"
  :description "A Clojure natural language generator built on top of Google's simplenlg library."
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :java-source-paths ["src/simplenlg"]
  :resource-paths ["lib/junit-4.4.jar"
                   "lib/lexAccess2011dist.jar"
                   "lib/lexCheck2006api.jar"
                   "lib/lvg2001api.jar"])
