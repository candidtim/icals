(defproject icals "1.0.0-SNAPSHOT"
  :description "A program that prints today's events from a given iCal resource (URL, file)"
  :url "https://github.com/candidtim/icals"
  :license {:name "MIT" :url "https://choosealicense.com/licenses/mit"}
  :main icals.core
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.mnode.ical4j/ical4j "3.1.1"]
                 [org.slf4j/slf4j-nop "1.7.32"]]
  :repl-options {:init-ns icals.core})
