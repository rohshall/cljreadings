(defproject cljreadings "0.1.0-SNAPSHOT"
  :description "REST based device readings service"
  :url "http://github.com/rohshall/cljreadings"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [org.clojure/java.jdbc "0.3.0-alpha4"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [org.apache.tomcat/tomcat-dbcp "7.0.40"]
                 [org.apache.tomcat/tomcat-jdbc "7.0.40"]
                 [ring/ring-json "0.2.0"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler cljreadings.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
