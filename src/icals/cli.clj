(ns icals.cli
  (:gen-class)
  (:require [icals.ical :refer [filter-by-day read-ics-file]])
  (:require [icals.core :refer [format-event]])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:import [java.time LocalDate]))

(def cli-options
  [["-f" "--format FORMAT" "Format of the events"
    :default "{{start}} - {{end}}: {{summary}}"
    :id :fmt]
   ["-d" "--date DATE" "Date to print the events for"
    :default (LocalDate/now)
    :parse-fn #(LocalDate/parse %)]])

(defn -main [& args]
  (let [parsed (parse-opts args cli-options)
        {{:keys [fmt date]} :options [path] :arguments} parsed
        cal (read-ics-file path)
        events (filter-by-day cal date)]
    (doseq [e events]
      (println (format-event e fmt)))))
