# AGENTS.md — Retro Adventure

Working notes for coding agents. Read this before touching the project; it
records the commands that work and the traps that cost time.

**What it is:** a Kotlin port of Colossal Cave Adventure — the 1995 430-point
Crowther/Woods version, by way of Eric S. Raymond's *open-adventure* — packaged
as an iOS and Android app. One shared Kotlin engine, Compose Multiplatform front
ends. Fully offline; there is nothing to talk to.

**Location:** `~/Code/RetroAdventure`
**App Store:** `com.xndev.retroAdventure`, Team `A69JRS6V57` — **no App Store
Connect record exists yet.** Price not set.
**Upstream:** <https://gitlab.com/esr/open-adventure>, vendored at the commit in
`vendor/open-adventure/UPSTREAM-COMMIT.txt` (1.22, 2026-07-08).

---

## House style

**US English, not British.** Color, not colour. License, not licence. Behavior,
center, organize, recognize. This applies to prose, code comments, identifiers,
and anything the app puts on screen.

The one exception is game text, which is quoted verbatim from upstream and must
never be "corrected" — the transcripts compare byte for byte, and the 1977
authors' spelling is the spec.

## The one big idea

**Upstream ships 107 recorded transcripts, and they are the whole reason this
port is tractable.** `vendor/open-adventure/tests` holds `.log`/`.chk` pairs: a
`.log` is a script of keystrokes, and the matching `.chk` is exactly what
upstream's C binary printed when fed it. Nearly every one starts with a `seed
NNNN` command that pins the RNG, so a run is deterministic and a diff is a
verdict, not a judgment call.

That converts "did I port 1,677 lines of C correctly" from a question you argue
about into a number. Everything below is arranged around keeping that number
honest.

## Layout

```
engine/    Pure Kotlin. Framework-free, no dependencies, no Android or iOS
           imports. This is where the game lives.
           Dungeon.kt is GENERATED — see below. Do not hand-edit it.
scripts/   gen_dungeon.py regenerates engine/.../Dungeon.kt from the vendored
           adventure.yaml. A fork of upstream's make_dungeon.py.
vendor/    open-adventure, vendored: adventure.yaml (the game data), the C
           sources kept as porting reference, the 107 test transcripts, COPYING,
           and the upstream docs. Treat as read-only.
```

`app/` and `iosApp/` do not exist yet. When they arrive, follow EncounterDeck's
layout exactly — it is the same stack and the same two stores.

## Commands

Requires **JDK 21**, and `pyyaml` for the generator (`python3 -m pip install
--user pyyaml`; it is not preinstalled here).

```bash
./gradlew :engine:jvmTest          # the suite, including the transcript scoreboard
python3 scripts/gen_dungeon.py     # regenerate Dungeon.kt from adventure.yaml
```

`:engine:jvmTest` prints a scoreboard every run:

```
Transcript suite: 0/103 transcripts match upstream exactly
Matching prefix:  6744/134281 lines (5%), 4 savegame tests skipped
  axebear: line 40: expected "Your lamp is now on." but got "[not ported yet] verb on"
```

The first failing line of each transcript *is* the to-do list. Work the most
common one, re-run, watch the number move.

### The reference oracle

Upstream's C binary builds and runs on this machine, and is worth having: it
answers "what does the real game do here" in seconds and takes no arguing with.

```bash
git clone --depth 1 https://gitlab.com/esr/open-adventure.git /tmp/oa
cd /tmp/oa && make advent          # needs libedit; builds clean on macOS
./advent < tests/axebear.log | diff - tests/axebear.chk
./advent -d < some.log             # -d prints every RNG draw, for chasing
                                   # divergences in seeded runs
```

**103, not 107, is the ceiling.** `cheatresume`, `cheatresume2`, `resumefail2`
and `savetamper` need savegame files made by upstream's `cheat` binary. The
reference C binary fails them here too, for that reason and not because of
anything wrong. They are skipped by name in `TranscriptTest`.

## Porting status

Done and verified:

- **`Dungeon.kt`** — all 185 locations, 70 objects, 213 messages, 878 travel ops.
  Cross-checked field by field against upstream's generated `dungeon.c`: 878/878
  travel ops identical, `tkey` identical, all 185 condition bitmasks identical,
  702 symbol values with zero mismatches.
- **`Rng.kt`** — the LCG, including the five draws `set_seed` burns on the
  magic word.
- **`Vocabulary.kt`** — 5-character truncated lookup, motion/object/action order.
- **`Output.kt`** — `vspeak` and its `%d`/`%s`/`%S`/`%V` handling and the
  floor→ground swap.
- **`GameState.kt`** — the game struct, `carry`/`drop`/`move`, `initialise`.
- **`Adventure.kt`** — `describe_location`, `listobjects`, `playermove`,
  `do_command`'s loop shape, and the verbs `seed`, `take`, `drop`, `inventory`.

Not done: **most of `actions.c`** (1,677 lines), dwarf and pirate movement,
death and resurrection, the closing sequence, scoring, hints, and save/resume.
Unported verbs answer with the `NOT_PORTED` marker rather than failing silently,
so a transcript diff points straight at the next one to write.

The recommended order is whatever the scoreboard says is most common. Right now
that is `on`/`off` (the lamp), then `open`/`close`, `attack`, `rub`.

## Traps

**`Dungeon.kt` is generated. Do not hand-edit it.** Change `adventure.yaml` or
`scripts/gen_dungeon.py` and regenerate. A hand fix survives exactly until the
next person runs the generator, and the diff that eats it is 3,200 lines long.

**Do not "clean up" `buildtravel()` in the generator.** It is upstream's code,
kept deliberately verbatim, and it compiles the per-location `travel:` rules into
the flat `travel[]`/`tkey[]` arrays that movement walks. Upstream's own comment
on it begins "THIS CODE IS WAAAY MORE COMPLEX THAN IT NEEDS TO BE" and then
explains why it has not been simplified. The acceptance test is that the emitted
table does not change; there is a cross-check recipe in the git history of this
file's first commit.

**The RNG is not an implementation detail.** Substituting `kotlin.random` makes
every seeded transcript diverge, which throws away the only oracle this project
has. `set_seed` also burns five draws generating the bird's magic word before
returning — a port that defers that work is off by five draws forever after.

**Blank lines are part of the output.** Upstream emits one before nearly every
message and one before every prompt, and the `.chk` files record them. Tidying
the spacing looks better on a phone and fails all 103 transcripts.

**`rspeak` and an action's own message are different tables.** Several verbs —
`seed`, `waste`, `listen` — speak `actions[verb].message`, not
`arbitraryMessages[verb]`. Using the wrong one silently prints some unrelated
line: `rspeak(SEED, n)` printed "With what? Your bare hands?" and looked like a
parser bug for a while.

**Words compare on their first five characters, case-insensitively.** That is
why the YAML spells the lantern "lante" and downstream "downs". Matching whole
words instead breaks dozens of accepted commands at once and looks like a data
problem.

**`do_command`'s two nested loops are load-bearing.** The outer one describes
the location; the inner one takes commands until one actually moves the player.
Collapse them and the room gets re-described after every verb — a transcript
diff catches that instantly and a play-through does not.

**The engine pulls input; it is not `String -> String`.** The loop asks
questions mid-turn ("Would you like instructions?"), so a per-command function
would mean rewriting every yes/no site as a state machine. `Adventure` takes an
`InputSource` instead. The transcript harness hands it a file. **The UI will
need to hand it a blocking queue fed from the text field, on a background
thread — that bridge is not written yet and is the first real decision the app
layer has to make.**

**Keep `engine/` framework-free.** It will be compiled for JVM, Android and iOS.
Android and iOS targets are deliberately not enabled yet so that
`:engine:jvmTest` runs without the Android SDK; add them when `app/` lands.

## Licensing

**Upstream is BSD-2-Clause** — code © 1977, 2005 Will Crowther and Don Woods,
`adventure.yaml` © Eric S. Raymond. That is permissive: commercial use and paid
App Store distribution are both fine.

The one obligation is clause 2: a binary distribution must reproduce the
copyright notice, the license conditions and the disclaimer "in the
documentation and/or other materials provided with the distribution." **In
practice that means an About screen carrying the full text of
`vendor/open-adventure/COPYING` plus both copyright lines, reachable from the
app.** Copy EncounterDeck's `AboutDialog` pattern, and copy the lesson with it:
its About dialog existed, was linked from three screens, and was never composed,
so the license text was unreachable for three releases and no screenshot showed
it. Write the test that opens the dialog and asserts the attribution appears.

The name "Colossal Cave Adventure" is deliberately **not** the app name. Ken
Williams shipped "Colossal Cave 3D Adventure" in 2023 and Cygnus actively
defends the mark. "Retro Adventure" was chosen to sidestep that; do not rename
toward the original without checking it in the New App dialog first, which per
`~/Code/AppStoreListings/STATUS.md` is the only real test of a name.

This repo's own license is not yet chosen. EncounterDeck uses PolyForm
Noncommercial; that is compatible with BSD-2 upstream, but decide before the
first public push.

## Related conventions

App Store process, account state and gotchas for all of Matt's apps live in
`~/Code/AppStoreListings/STATUS.md`. Store metadata is in `listings.md` there.
Read those before any store work. EncounterDeck's `AGENTS.md` covers the
Compose-Multiplatform-to-App-Store path in detail — archive/export, XcodeGen,
simulator screenshots — and all of it applies here unchanged.
