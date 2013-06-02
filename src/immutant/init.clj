(ns immutant.init
  (:require [immutant.web]
            [cljreadings.handler])
  (:use [ring.middleware.resource :only [wrap-resource]]
        [ring.util.response :only [redirect]]))

#_(defn handler [request]
  (redirect "/index.html"))

(immutant.web/start cljreadings.handler/app #_(wrap-resource handler "public"))
