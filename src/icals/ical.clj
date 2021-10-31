(ns icals.ical
  (:require [clojure.java.io :refer [reader]])
  (:import [icals.core Event])
  (:import [java.time ZoneId]
           [net.fortuna.ical4j.data CalendarBuilder]
           [net.fortuna.ical4j.model Component DateTime Period]
           [net.fortuna.ical4j.filter.predicate PeriodRule]))

(defn- to-ical-datetime [java-local-date]
  "Convert Java LocalDate to ical4j DateTime at midnight, in UTC"
  (let [java-local-datetime (.atStartOfDay java-local-date (ZoneId/of "UTC"))
        java-date (java.util.Date/from (.toInstant java-local-datetime))]
    (DateTime. java-date)))

(defn- to-java-time [ical-datetime]
  "Convert ical4j DateTime to Java's OffsetDateTime"
  (.toLocalTime (.atZone (.toInstant ical-datetime) (ZoneId/systemDefault))))

(defn- get-start-end [vevent ical-date-start ical-date-end]
  "Get start and end time of a given ical4j VEvent today"
  (let [periods (.getConsumedTime vevent ical-date-start ical-date-end)]
    (if (empty? periods) ; FIXME this is ugly; why this can be empty anyway?
      [nil nil]
      (let [occurrence (first periods)
            start-time (to-java-time (.getStart occurrence))
            end-time (to-java-time (.getEnd occurrence))]
        [start-time end-time]))))

(defn- vevent-to-event [vevent ical-date-start ical-date-end]
  "Convert ical4j VEvent to Event record with a today's event time"
  (let [[start end] (get-start-end vevent ical-date-start ical-date-end)
        summary (.getValue (.getSummary vevent))]
    (Event. start end summary)))

(defn filter-by-day [cal day]
  (let [ical-day (to-ical-datetime day)
        ical-next-day (to-ical-datetime (.plusDays day 1))
        period (Period. ical-day ical-next-day)
        rule (PeriodRule. period)
        all-vevents (.getComponents cal Component/VEVENT)
        matching-vevents (filter #(.test rule %) all-vevents)
        events (distinct (map #(vevent-to-event % ical-day ical-next-day) matching-vevents))
        sorted-events (sort-by :start events)]
    sorted-events))

(defn read-ics-file [path]
  (let [builder (CalendarBuilder.)
        input (reader path)]
    (.build builder input)))
