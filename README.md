# icals

A program that prints today's events from a given iCal resource (URL, file).

`icals` is a concatenation of `ical` and `ls`.

## Usage

Print today's events from an iCal file (`*.ics` file) available at a given
location:

    icals [-h|--help] [-f|--format FORMAT] [-d|--date DATE] LOCATION

where:

 - `LOCATION` can be an URL or a file path
 - `FORMAT` is a template string to use to format each event as text
 - `DATE` is a date to print the events for, in YYYY-MM-DD format; default is
   today

Default `FORMAT` is `{{start}} - {{end}}: {{summary}}`. `FORMAT` uses a
mustache-like syntax ([Selmer](https://github.com/yogthos/Selmer)), and accepts
following arguments: `start`, `end`, `summary`.

Using Leiningen:

    lein run [OPTS] LOCATION

where `OPTS` are the same options as described bove.

## License

MIT License

Copyright (c) 2021 candidtim

See LICENSE file for more details.
