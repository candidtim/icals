#!/bin/bash

lein do clean, uberjar

native-image --report-unsupported-elements-at-runtime \
             --initialize-at-build-time \
             --initialize-at-run-time=net.fortuna.ical4j.util.JCacheTimeZoneCache \
             --no-server \
             --no-fallback \
             -jar ./target/icals-1.0.0-standalone.jar \
             -H:Name=./target/icals
