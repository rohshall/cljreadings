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

; Utility functions

(defn str-to-timestamp [s]
  (and s (java.sql.Timestamp. (.getTime (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd") s (java.text.ParsePosition. 0))))))


(defn timestamp-to-str [t]
  (and t (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") t)))


(defn device-to-str [{:keys [mac_addr device_type_id manufactured_at registered_at]}]
  (hash-map "mac_addr" mac_addr "device_type_id" device_type_id 
            "manufactured_at" (timestamp-to-str manufactured_at)
            "registered_at" (timestamp-to-str registered_at)))


(defn reading-to-str [{:keys [value created_at]}]
  (hash-map "value" value "created_at" (timestamp-to-str created_at)))


; Database query functions

(defn get-devices []
  (let [devices (jdbc/query db-spec ["SELECT * FROM devices"])]
    (map device-to-str devices)))


(defn create-device [{:keys [mac_addr device_type_id manufactured_at registered_at] :as device-info}]
  (jdbc/insert! db-spec :devices
    {:mac_addr mac_addr :device_type_id device_type_id
     :manufactured_at (str-to-timestamp manufactured_at)
     :registered_at (str-to-timestamp registered_at)}))


(defn get-device [device-id]
  (let [device (jdbc/query db-spec ["SELECT * FROM devices WHERE mac_addr = ?" device-id])]
    (device-to-str device)))


(defn get-device-readings [device-id {:keys [from to]}]
  (let [sql-params 
        (cond 
          (and from to) ["SELECT * FROM readings WHERE device_mac_addr = ? AND created_at BETWEEN ? AND ?"
                         device-id (str-to-timestamp from) (str-to-timestamp to)]
          from ["SELECT * FROM readings WHERE device_mac_addr = ? AND created_at > ?"
                device-id (str-to-timestamp from)]
          to ["SELECT * FROM readings WHERE device_mac_addr = ? AND created_at < ?"
              device-id (str-to-timestamp to)]
          :else ["SELECT * FROM readings WHERE device_mac_addr = ?" device-id])
        readings (jdbc/query db-spec sql-params)]
    (map reading-to-str readings)))


(defn create-device-reading [device-id {:keys [value]}]
  (jdbc/insert! db-spec :readings
    {:device_mac_addr device-id :value value :created_at (java.sql.Timestamp. (.getTime (java.util.Date.)))}))


; Route definitions

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

