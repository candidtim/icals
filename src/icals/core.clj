(ns icals.core
  (:gen-class)
  (:use [clojure.java.io :only [reader]])
  (:import [java.time ZoneId]
           [java.time.format DateTimeFormatter]
           [net.fortuna.ical4j.data CalendarBuilder]
           [net.fortuna.ical4j.filter Filter]
           [net.fortuna.ical4j.model Component Date DateTime Period]
           [net.fortuna.ical4j.filter.predicate PeriodRule]))

(def time-formatter (DateTimeFormatter/ofPattern "HH:mm"))

(defn ical-datetime [java-local-date]
  "Convert Java LocalDate to ical4j DateTime at midnight, in UTC"
  (let [java-local-datetime (.atStartOfDay java-local-date (ZoneId/of "UTC"))
        java-date (java.util.Date/from (.toInstant java-local-datetime))]
    (DateTime. java-date)))

(def today (ical-datetime (java.time.LocalDate/now)))

(def tomorrow (ical-datetime (.plusDays (java.time.LocalDate/now) 1)))

(defrecord Event [start end summary])

(defn to-java-time [ical-datetime]
  "Convert ical4j DateTime to Java's OffsetDateTime"
  (.toLocalTime (.atZone (.toInstant ical-datetime) (ZoneId/systemDefault))))

(defn get-start-end [vevent]
  "Get start and end time of a given ical4j VEvent today"
  (let [periods (.getConsumedTime vevent today tomorrow)]
    (if (empty? periods) ; FIXME this is ugly; why this could be empty anyway?
      [nil nil]
      (let [occurrence (first periods)
            start-time (to-java-time (.getStart occurrence))
            end-time (to-java-time (.getEnd occurrence))]
        [start-time end-time]))))

(defn vevent-to-event [vevent]
  "Convert ical4j VEvent to Event record with a today's event time"
  (let [[start end] (get-start-end vevent)
        summary (.getValue (.getSummary vevent))]
    (->Event start end summary)))

(defn format-time [t]
  (if (nil? t) "" (.format time-formatter t)))

(defn format-event [event]
  (let [start (format-time (:start event))
        end (format-time (:end event))]
    (str start " - " end " " (:summary event))))

(defn configure-ical4j []
  (System/setProperty
    "net.fortuna.ical4j.timezone.cache.impl"
    "net.fortuna.ical4j.util.MapTimeZoneCache"))

(defn read-ics-file [path]
  (let [builder (CalendarBuilder.)
        input (reader path)]
    (.build builder input)))

(defn filter-today [cal]
  (let [period (Period. today tomorrow)
        rule (PeriodRule. period)
        all-vevents (.getComponents cal Component/VEVENT)
        matching-vevents (filter #(.test rule %) all-vevents)
        events (distinct (map vevent-to-event matching-vevents))
        sorted-events (sort-by :start events)]
    sorted-events))

(defn -main [path]
  (configure-ical4j)
  (let [cal (read-ics-file path)
        todays-events (filter-today cal)]
    (doseq [e (filter-today cal)]
      (println (format-event e)))))
