(ns icals.cli
  (:gen-class)
  (:require [icals.ical :refer [filter-by-day read-ics-file]]
            [icals.core :refer [format-event]]
            [icals.tools :refer [apply-safe]]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]])
  (:import [java.time LocalDate]))

(defn- valid-location? [location]
  (and (some? location)
       (or (.isFile (io/file location))
           (boolean (:ok (apply-safe io/as-url location))))))

(def cli-options
  [["-f" "--format FORMAT" "Format of the events"
    :default "{{start}} - {{end}}: {{summary}}"
    :id :fmt]
   ["-d" "--date DATE" "Date to print the events for"
    :default (LocalDate/now)
    :parse-fn #(LocalDate/parse %)]
   ["-h" "--help" "Show help"]])

(defn usage [options-summary]
  (->> ["A program that prints today's events from a given iCal URL or file."
        ""
        "Options:"
        options-summary
        ""
        "Argument: a path or a URL to an iCal file (*.ics file)"]
       (string/join \newline)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn parse-args [args]
  "Parse and validate the arguments. Can stop the program and print relevant
  messages. Returns parsed arguments if no error is detected."
  (let [parsed (parse-opts args cli-options)
        {:keys [options arguments summary errors]} parsed
        {:keys [fmt date help]} options
        [location] arguments]
    (cond
      ; print help, if requested:
      help
      (exit 0 (usage summary))
      ; check if there were errors:
      errors
      (exit 80 (string/join \newline errors))
      ; check if location is defined:
      (nil? location)
      (exit 81 "No ics file location is provided. Use --help for usage.")
      ; validate location:
      (not (valid-location? location))
      (exit 82 (str "Not a valid ics file location: " location))
      ; otherwise, return the parsed arguments:
      :else
      {:location location, :fmt fmt, :date date})))

(defn -main [& args]
  (let [{:keys [location fmt date]} (parse-args args)
        cal (read-ics-file location)
        events (filter-by-day cal date)]
    (doseq [e events]
      (println (format-event e fmt)))))
