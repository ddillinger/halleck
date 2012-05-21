(defproject hallex "0.1.0-SNAPSHOT"
  :description "log4j dubstepper"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [log4j "1.2.16"]]
  :dev-dependencies [[org.clojure/tools.logging "0.2.3"]]
  :aot #{hallex.core})
