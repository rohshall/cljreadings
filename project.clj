(defproject cljreadings "0.1.0-SNAPSHOT"
  :description "REST based device readings service"
  :url "http://github.com/rohshall/cljreadings"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler cljreadings.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
