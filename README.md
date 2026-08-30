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

Copyright © 2026 Matthew Heusser.

**Upstream first.** This is a port of
[Open Adventure](https://gitlab.com/esr/open-adventure), which is BSD-2-Clause:
game code © 1977, 2005 Will Crowther and Don Woods; `adventure.yaml` and the test
suite © Eric S. Raymond. That material stays BSD-2-Clause for everyone,
commercial use included. Its full text is in `LICENSE.upstream` and
`vendor/open-adventure/COPYING`, and it is reproduced in the app's About screen,
as clause 2 requires.

**This port's own code** — the Kotlin engine, the generator, the build and the
tests — is released under the
[PolyForm Noncommercial License 1.0.0](LICENSE): use, run, modify and share it
for any **noncommercial** purpose. Commercial use of *this port* is not permitted
for third parties. If you want to build a commercial Adventure, start from
upstream's BSD-2 sources, which is exactly what they are there for.

**Author's commercial rights** — as the copyright holder of the port, Matthew
Heusser retains all commercial rights in it and may sell or license it (e.g. as a
paid app). A license grants rights to others; it does not limit the owner.

**Contributing** — by contributing you agree to the
[Contributor License Agreement](CONTRIBUTING.md), which lets your contribution be
shared publicly under PolyForm Noncommercial **and** included in the author's
commercial versions. This is what keeps the app sellable.
