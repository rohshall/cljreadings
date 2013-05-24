(ns cljreadings.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.jdbc :as jdbc]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.json :refer [wrap-json-params]]
            ))

(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "sd_ventures_development"
              :user "sd_ventures"
              :password ""})


(defn get-devices []
  (jdbc/with-connection db-spec
    (jdbc/with-query-results rs ["SELECT * FROM devices"]
      (doall rs))))


(defn str-to-timestamp [s]
  (let [date-format (java.text.SimpleDateFormat. "yyyy-MM-dd")
        parse-pos (java.text.ParsePosition. 0)]
    (java.sql.Timestamp. (.getTime (.parse date-format s parse-pos)))))


(defn create-device [{:keys [mac_addr device_type_id manufactured_at created_at] :as device-info}]
  (println "creating device " device-info)
  (jdbc/with-connection db-spec
    (jdbc/insert-record :devices
      {:mac_addr mac_addr :device_type_id device_type_id :manufactured_at (str-to-timestamp manufactured_at)})))


(defn get-device [device-id]
  (jdbc/with-connection db-spec
    (jdbc/with-query-results res ["SELECT * FROM devices WHERE mac_addr = ?" device-id]
      (doall res))))


(defn get-device-readings [device-id {:keys [from to]}]
  (let [sql-params 
        (cond 
          (and from to) ["SELECT * FROM readings WHERE device_mac_addr = ? AND created_at BETWEEN ? AND ?"
                         device-id (str-to-timestamp from) (str-to-timestamp to)]
          from ["SELECT * FROM readings WHERE device_mac_addr = ? AND created_at > ?"
                device-id (str-to-timestamp from)]
          to ["SELECT * FROM readings WHERE device_mac_addr = ? AND created_at < ?"
              device-id (str-to-timestamp to)]
          :else ["SELECT * FROM readings WHERE device_mac_addr = ?" device-id])]
    (jdbc/with-connection db-spec
      (jdbc/with-query-results res sql-params
        (doall res)))))


(defn create-device-reading [device-id {:keys [value]}]
  (jdbc/with-connection db-spec
    (jdbc/insert-record :readings
      {:device_mac_addr device-id :value value :created_at (java.sql.Timestamp. (.getTime (java.util.Date.)))})))


(defroutes app-routes
  (route/resources "/")
  (context "/api/1" []
           (defroutes api-routes
             (GET "/devices" [] (get-devices))
             (POST "/devices" {device-info :params} (create-device device-info))
             (context "/devices/:device-id" [device-id]
                      (defroutes device-routes
                        (GET "/" [] (get-device device-id))
                        (GET "/readings" {params :params} (get-device-readings device-id params))
                        (POST "/readings" {reading-info :params} (create-device-reading device-id reading-info))))))

  (route/not-found "Not Found"))


(def app
  (->
  (handler/api app-routes)
    (wrap-json-params)))


(def war-handler
  (->
    app
    (wrap-resource "public")))

