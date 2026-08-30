# Retro Adventure

Colossal Cave Adventure — the 1995 430-point Crowther/Woods version — ported to
Kotlin for iOS and Android.

The game logic is a port of [open-adventure](https://gitlab.com/esr/open-adventure),
Eric S. Raymond's maintained forward-port of Adventure 2.5, released with the
original authors' permission. This project reuses its game data (`adventure.yaml`)
and its recorded test transcripts, and reimplements the interpreter in Kotlin so
one engine can serve a JVM test harness, an Android app and an iOS app.

## Status

Early. The data tables, RNG, vocabulary, output pipeline and movement are ported
and verified against upstream; most of the verb set is not. `AGENTS.md` has the
current scoreboard and the build commands.

## Building

Requires JDK 21.

    ./gradlew :engine:jvmTest

## License

Upstream open-adventure is BSD-2-Clause:

- Game code © 1977, 2005 Will Crowther and Don Woods
- `adventure.yaml` and the test suite © Eric S. Raymond

The full text is in `vendor/open-adventure/COPYING` and is reproduced in the
app's About screen, as clause 2 requires.

This port's own license has not been chosen yet.
