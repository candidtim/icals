(ns icals.core
  (:require [selmer.parser :refer [render]])
  (:import [java.time.format DateTimeFormatter]))

(defrecord Event [start end summary])

(def ^:private time-formatter (DateTimeFormatter/ofPattern "HH:mm"))

(defn- format-time [t]
  (if (nil? t) "" (.format time-formatter t)))

(defn format-event [event fmt]
  "Format the event to a string using a given Selmer template"
  (render fmt (-> event
                  (update :start format-time)
                  (update :end format-time))))
