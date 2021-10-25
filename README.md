# icals

A program that prints today's events from a given iCal resource (URL, file).

## Usage

Print events from a iCal file available via HTTP:

    icals URL

or from a local iCal file:

    icals FILE

Or, using Leiningen:

    lein run URL

## Building a binary

Assuming that GraalVM CLI is available in `PATH`:

    ./build.sh

## License

MIT License

Copyright (c) 2021 candidtim

See LICENSE file for more details.
