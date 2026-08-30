#!/usr/bin/env python3
# SPDX-FileCopyrightText: (C) Eric S. Raymond <esr@thyrsus.com>
# SPDX-License-Identifier: BSD-2-Clause
"""
Regenerate engine/.../Dungeon.kt from vendor/open-adventure/adventure.yaml.

This is a fork of upstream's make_dungeon.py that emits Kotlin instead of C.
The YAML walk and, in particular, buildtravel() are upstream's code kept as
close to verbatim as the port allows: buildtravel compiles the per-location
`travel:` rules down to the flat travel[]/tkey[] arrays playermove() walks, and
it is the one piece of this program that is genuinely hard to get right.
Upstream's own comment on it reads "THIS CODE IS WAAAY MORE COMPLEX THAN IT
NEEDS TO BE" and warns against cleaning it up. Take that seriously.

Only the emitters below the buildtravel() line were rewritten.

Usage:  python3 scripts/gen_dungeon.py        (from the repo root)
"""

# pylint: disable=consider-using-f-string,line-too-long,invalid-name,missing-function-docstring,too-many-branches,global-statement,multiple-imports,too-many-locals,too-many-statements,too-many-nested-blocks,no-else-return,raise-missing-from,redefined-outer-name

import os, sys, yaml

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
YAML_NAME = os.path.join(ROOT, "vendor", "open-adventure", "adventure.yaml")
KT_NAME = os.path.join(
    ROOT,
    "engine/src/commonMain/kotlin/com/xndev/retroadventure/engine/Dungeon.kt",
)
PACKAGE = "com.xndev.retroadventure.engine"

DONOTEDIT = (
    "// Generated from vendor/open-adventure/adventure.yaml by "
    "scripts/gen_dungeon.py - do not hand-hack!\n"
    "// Upstream data is (C) Eric S. Raymond, BSD-2-Clause; see vendor/open-adventure/COPYING.\n"
)

# pacify pylint
db = {}
locnames = []
msgnames = []
objnames = []
motionnames = []
statedefines = []
ignore = ""


def kstr(string):
    """Render a Python string as a Kotlin string literal, or null.

    Deliberately does NOT escape backslashes, matching upstream's
    make_c_string(). adventure.yaml uses single-quoted scalars, where a
    backslash is literal -- a message written with a newline escape arrives here
    as two characters, and upstream relies on the compiler turning it back into
    a newline once it lands in a string literal. Kotlin does the same. Escape
    the backslash and the game prints the escape at the player instead of
    breaking the line. Only newline and tab escapes appear in the YAML, so there
    is nothing else to protect against.
    """
    if string is None:
        return "null"
    string = string.replace('"', '\\"')
    string = string.replace("$", "\\$")
    string = string.replace("\n", "\\n")
    string = string.replace("\t", "\\t")
    return '"' + string + '"'


def klist(strings):
    """Render a list of Python strings as a Kotlin listOf(...)."""
    if not strings:
        return "emptyList()"
    return "listOf(" + ", ".join(kstr(s) for s in strings) + ")"


# ---------------------------------------------------------------------------
# buildtravel() and its helpers are upstream's, unchanged except that the
# C-flavored "true"/"false" strings became Python bools and the cond/dest type
# tags became Kotlin enum references. Do not "simplify" this.
# ---------------------------------------------------------------------------


def buildtravel(locs, objs):
    assert len(locs) <= 300
    assert len(objs) <= 100
    # This function first compiles the YAML to a form identical to the
    # data in section 3 of the old adventure.text file, then a second
    # stage unpacks that data into the travel array.  Here are the
    # rules of that intermediate form:
    #
    # Each row of data contains a location number (X), a second
    # location number (Y), and a list of motion numbers (see section 4).
    # each motion represents a verb which will go to Y if currently at X.
    # Y, in turn, is interpreted as follows.  Let M=Y/1000, N=Y mod 1000.
    # 		If N<=300	it is the location to go to.
    # 		If 300<N<=500	N-300 is used in a computed goto to
    # 					a section of special code.
    # 		If N>500	message N-500 from section 6 is printed,
    # 					and he stays wherever he is.
    # Meanwhile, M specifies the conditions on the motion.
    # 		If M=0		it's unconditional.
    # 		If 0<M<100	it is done with M% probability.
    # 		If M=100	unconditional, but forbidden to dwarves.
    # 		If 100<M<=200	he must be carrying object M-100.
    # 		If 200<M<=300	must be carrying or in same room as M-200.
    # 		If 300<M<=400	game.prop(M % 100) must *not* be 0.
    # 		If 400<M<=500	game.prop(M % 100) must *not* be 1.
    # 		If 500<M<=600	game.prop(M % 100) must *not* be 2, etc.
    # If the condition (if any) is not met, then the next *different*
    # "destination" value is used (unless it fails to meet *its* conditions,
    # in which case the next is found, etc.).
    ltravel = []
    verbmap = {}
    for i, motion in enumerate(db["motions"]):
        try:
            for word in motion[1]["words"]:
                verbmap[word.upper()] = i
        except TypeError:
            pass

    def dencode(action, name):
        "Decode a destination number"
        if action[0] == "goto":
            try:
                return locnames.index(action[1])
            except ValueError:
                sys.stderr.write(
                    "dungeon: unknown location %s in goto clause of %s\n"
                    % (action[1], name)
                )
                raise ValueError
        elif action[0] == "special":
            return 300 + action[1]
        elif action[0] == "speak":
            try:
                return 500 + msgnames.index(action[1])
            except ValueError:
                sys.stderr.write(
                    "dungeon: unknown message %s in speak clause of %s\n"
                    % (action[1], name)
                )
                sys.exit(1)
        else:
            raise ValueError(action)
        return ""  # Pacify pylint

    def cencode(cond, name):
        if cond is None:
            return 0
        if cond == ["nodwarves"]:
            return 100
        elif cond[0] == "pct":
            return cond[1]
        elif cond[0] == "carry":
            try:
                return 100 + objnames.index(cond[1])
            except ValueError:
                sys.stderr.write(
                    "dungeon: unknown object name %s in carry clause of %s\n"
                    % (cond[1], name)
                )
                sys.exit(1)
        elif cond[0] == "with":
            try:
                return 200 + objnames.index(cond[1])
            except IndexError:
                sys.stderr.write(
                    "dungeon: unknown object name %s in with clause of %s\n"
                    % (cond[1], name)
                )
                sys.exit(1)
        elif cond[0] == "not":
            try:
                obj = objnames.index(cond[1])
                if isinstance(cond[2], int):
                    state = cond[2]
                elif cond[2] in objs[obj][1].get("states", []):
                    state = objs[obj][1].get("states").index(cond[2])
                else:
                    for i, stateclause in enumerate(objs[obj][1]["descriptions"]):
                        if isinstance(stateclause, list):
                            if stateclause[0] == cond[2]:
                                state = i
                                break
                    else:
                        sys.stderr.write(
                            "dungeon: unmatched state symbol %s in not clause of %s\n"
                            % (cond[2], name)
                        )
                        sys.exit(1)
                return 300 + obj + 100 * state
            except ValueError:
                sys.stderr.write(
                    "dungeon: unknown object name %s in not clause of %s\n"
                    % (cond[1], name)
                )
                sys.exit(1)
        else:
            raise ValueError(cond)

    for i, (name, loc) in enumerate(locs):
        if "travel" in loc:
            for rule in loc["travel"]:
                tt = [i]
                dest = dencode(rule["action"], name) + 1000 * cencode(
                    rule.get("cond"), name
                )
                tt.append(dest)
                tt += [motionnames[verbmap[e]].upper() for e in rule["verbs"]]
                if not rule["verbs"]:
                    tt.append(1)  # Magic dummy entry for null rules
                ltravel.append(tuple(tt))

    # At this point the ltravel data is in the Section 3
    # representation from the FORTRAN version.  Next we perform the
    # same mapping into what used to be the runtime format.

    travel = [[0, "LOC_NOWHERE", 0, "GOTO", 0, 0, "GOTO", 0, False, False]]
    tkey = [0]
    oldloc = 0

    def decode_condition(cond, objnames):
        if cond == 0:
            return "GOTO", 0, 0
        if cond < 100:
            return "PCT", cond, 0
        if cond == 100:
            return "GOTO", 100, 0
        if cond <= 200:
            return "CARRY", objnames[cond - 100], 0
        if cond <= 300:
            return "WITH", objnames[cond - 200], 0
        return "NOT", cond % 100, (cond - 300) // 100

    def decode_destination(dest, locnames, msgnames):
        if dest <= 300:
            return "GOTO", locnames[dest]
        if dest > 500:
            return "SPEAK", msgnames[dest - 500]
        return "SPECIAL", locnames[dest - 300]

    while ltravel:
        rule = list(ltravel.pop(0))
        loc = rule.pop(0)
        newloc = rule.pop(0)
        if loc != oldloc:
            tkey.append(len(travel))
            oldloc = loc
        elif travel:
            travel[-1][-1] = not travel[-1][-1]
        while rule:
            cond = newloc // 1000
            nodwarves = cond == 100
            condtype, condarg1, condarg2 = decode_condition(cond, objnames)
            dest = newloc % 1000
            desttype, destval = decode_destination(dest, locnames, msgnames)
            travel.append(
                [
                    len(tkey) - 1,
                    locnames[len(tkey) - 1],
                    rule.pop(0),
                    condtype,
                    condarg1,
                    condarg2,
                    desttype,
                    destval,
                    nodwarves,
                    False,
                ]
            )
        travel[-1][-1] = True
    return (travel, tkey)


# ---------------------------------------------------------------------------
# Kotlin emitters. Everything below here is this project's, not upstream's.
# ---------------------------------------------------------------------------


def emit_refs(kind, pairs):
    """Emit `const val NAME = index` for one omap, as the C enums did."""
    out = ["// %s" % kind]
    for i, item in enumerate(pairs):
        out.append("const val %s = %d" % (item[0], i))
    return "\n".join(out)


def emit_arbitrary_messages(arb):
    out = ["val arbitraryMessages: Array<String?> = arrayOf("]
    for name, text in arb:
        out.append("    %s, // %s" % (kstr(text), name))
    out.append(")")
    return "\n".join(out)


def emit_locations(loc):
    out = ["val locations: Array<Location> = arrayOf("]
    for i, (name, item) in enumerate(loc):
        out.append(
            "    Location(small = %s, big = %s, sound = %s, loud = %s), // %d: %s"
            % (
                kstr(item["description"]["short"]),
                kstr(item["description"]["long"]),
                item.get("sound", "SILENT"),
                "true" if item.get("loud") else "false",
                i,
                name,
            )
        )
    out.append(")")
    return "\n".join(out)


def emit_objects(obj):
    """Also accumulates the per-object state constants into statedefines."""
    max_state = 0
    out = ["val objects: Array<Obj> = arrayOf("]
    for i, (name, attr) in enumerate(obj):
        words = attr.get("words") or []
        descriptions = attr.get("descriptions") or []
        # Only objects with a descriptions list can carry state labels; the
        # label list and the description list are index-parallel.
        states = attr.get("states") or []
        if states:
            statedefines.append("// States for %s" % name)
            for n, label in enumerate(states):
                statedefines.append("const val %s = %d" % (label, n))
                max_state = max(max_state, n)
            statedefines.append("")
        locs = attr.get("locations", ["LOC_NOWHERE", "LOC_NOWHERE"])
        immovable = attr.get("immovable", False)
        if isinstance(locs, str):
            locs = [locs, "-1" if immovable else "0"]
        out.append("    Obj( // %d: %s" % (i, name))
        out.append("        words = %s," % klist(words))
        out.append("        inventory = %s," % kstr(attr.get("inventory")))
        out.append("        plac = %s, fixd = %s," % (locs[0], locs[1]))
        out.append(
            "        isTreasure = %s," % ("true" if attr.get("treasure") else "false")
        )
        out.append("        descriptions = %s," % klist(descriptions))
        out.append("        sounds = %s," % klist(attr.get("sounds") or []))
        out.append("        texts = %s," % klist(attr.get("texts") or []))
        out.append("        changes = %s," % klist(attr.get("changes") or []))
        out.append("    ),")
    out.append(")")
    statedefines.append("// Maximum state value")
    statedefines.append("const val MAX_STATE = %d" % max_state)
    return "\n".join(out)


def emit_obituaries(obit):
    out = ["val obituaries: Array<Obituary> = arrayOf("]
    for o in obit:
        out.append(
            "    Obituary(query = %s, yesResponse = %s),"
            % (kstr(o["query"]), kstr(o["yes_response"]))
        )
    out.append(")")
    return "\n".join(out)


def emit_hints(hnt):
    out = ["val hints: Array<Hint> = arrayOf("]
    for member in hnt:
        item = member["hint"]
        out.append(
            "    Hint(number = %d, penalty = %d, turns = %d, question = %s, hint = %s),"
            % (
                item["number"],
                item["penalty"],
                item["turns"],
                kstr(item["question"]),
                kstr(item["hint"]),
            )
        )
    out.append(")")
    return "\n".join(out)


def emit_classes(cls):
    out = ["val classes: Array<ClassMsg> = arrayOf("]
    for item in cls:
        out.append(
            "    ClassMsg(threshold = %d, message = %s),"
            % (item["threshold"], kstr(item["message"]))
        )
    out.append(")")
    return "\n".join(out)


def emit_turn_thresholds(trn):
    out = ["val turnThresholds: Array<TurnThreshold> = arrayOf("]
    for item in trn:
        out.append(
            "    TurnThreshold(threshold = %d, pointLoss = %d, message = %s),"
            % (item["threshold"], item["point_loss"], kstr(item["message"]))
        )
    out.append(")")
    return "\n".join(out)


def emit_conditions(locations):
    out = ["val conditions: IntArray = intArrayOf("]
    for name, loc in locations:
        conditions = loc["conditions"]
        hints = loc.get("hints") or []
        flaglist = [f for f in conditions if conditions[f]]
        terms = ["(1 shl COND_%s)" % f for f in flaglist]
        terms += ["(1 shl COND_H%s)" % h["name"] for h in hints]
        line = " or ".join(terms) if terms else "0"
        out.append("    %s, // %s" % (line, name))
    out.append(")")
    return "\n".join(out)


def emit_motions(motions):
    global ignore
    out = ["val motions: Array<Motion> = arrayOf("]
    for name, contents in motions:
        words = contents["words"] or []
        out.append("    Motion(words = %s), // %s" % (klist(words), name))
        if not contents.get("oldstyle", True):
            for word in words:
                if len(word) == 1:
                    ignore += word.upper()
    out.append(")")
    return "\n".join(out)


def emit_actions(actions):
    global ignore
    out = ["val actions: Array<Action> = arrayOf("]
    for name, contents in actions:
        words = contents["words"] or []
        out.append(
            "    Action(words = %s, message = %s, noAction = %s), // %s"
            % (
                klist(words),
                kstr(contents["message"]),
                "true" if contents.get("noaction") is not None else "false",
                name,
            )
        )
        if not contents.get("oldstyle", True):
            for word in words:
                if len(word) == 1:
                    ignore += word.upper()
    out.append(")")
    return "\n".join(out)


def emit_travel(travel):
    out = ["val travel: Array<TravelOp> = arrayOf("]
    for entry in travel:
        (
            _idx,
            fromname,
            motion,
            condtype,
            condarg1,
            condarg2,
            desttype,
            destval,
            nodwarves,
            stop,
        ) = entry
        out.append(
            "    TravelOp(motion = %s, condType = CondType.%s, condArg1 = %s, "
            "condArg2 = %s, destType = DestType.%s, destVal = %s, noDwarves = %s, "
            "stop = %s), // from %s"
            % (
                motion,
                condtype,
                condarg1,
                condarg2,
                desttype,
                destval,
                "true" if nodwarves else "false",
                "true" if stop else "false",
                fromname,
            )
        )
    out.append(")")
    return "\n".join(out)


def emit_tkey(tkey):
    out = ["val tkey: IntArray = intArrayOf("]
    row = []
    for i, v in enumerate(tkey):
        row.append(str(v))
        if len(row) == 12:
            out.append("    " + ", ".join(row) + ",")
            row = []
    if row:
        out.append("    " + ", ".join(row) + ",")
    out.append(")")
    return "\n".join(out)


CONDBIT_DEFS = """// Symbols for cond bits, from upstream templates/dungeon.h.tpl
const val COND_LIT = 0           // Light
const val COND_OILY = 1          // If bit 2 is on: on for oil, off for water
const val COND_FLUID = 2         // Liquid asset, see bit 1
const val COND_NOARRR = 3        // Pirate doesn't go here unless following
const val COND_NOBACK = 4        // Cannot use "back" to move away
const val COND_ABOVE = 5         // Aboveground, but not in forest
const val COND_DEEP = 6          // Deep - e.g. where dwarves are active
const val COND_FOREST = 7        // In the forest
const val COND_FORCED = 8        // Only one way in or out of here
const val COND_ALLDIFFERENT = 9  // Room is in maze all different
const val COND_ALLALIKE = 10     // Room is in maze all alike

// Bits past 11 indicate areas of interest to "hint" routines
const val COND_HBASE = 11        // Base for location hint bits
const val COND_HCAVE = 12        // Trying to get into cave
const val COND_HBIRD = 13        // Trying to catch bird
const val COND_HSNAKE = 14       // Trying to deal with snake
const val COND_HMAZE = 15        // Lost in maze
const val COND_HDARK = 16        // Pondering dark room
const val COND_HWITT = 17        // At Witt's End
const val COND_HCLIFF = 18       // Cliff with urn
const val COND_HWOODS = 19       // Lost in forest
const val COND_HOGRE = 20        // Trying to deal with ogre
const val COND_HJADE = 21        // Found all treasures except jade

const val SILENT = -1            // no sound
"""

TYPE_DEFS = """// ---------------------------------------------------------------------------
// Table shapes. These mirror the structs in upstream templates/dungeon.h.tpl.
// ---------------------------------------------------------------------------

data class Location(
    val small: String?,
    val big: String?,
    val sound: Int,
    val loud: Boolean,
)

data class Obj(
    val words: List<String>,
    val inventory: String?,
    val plac: Int,
    val fixd: Int,
    val isTreasure: Boolean,
    val descriptions: List<String>,
    val sounds: List<String>,
    val texts: List<String>,
    val changes: List<String>,
)

data class Obituary(val query: String, val yesResponse: String)

data class TurnThreshold(val threshold: Int, val pointLoss: Int, val message: String)

data class ClassMsg(val threshold: Int, val message: String?)

data class Hint(
    val number: Int,
    val penalty: Int,
    val turns: Int,
    val question: String,
    val hint: String,
)

data class Motion(val words: List<String>)

data class Action(val words: List<String>, val message: String?, val noAction: Boolean)

enum class CondType { GOTO, PCT, CARRY, WITH, NOT }

enum class DestType { GOTO, SPECIAL, SPEAK }

data class TravelOp(
    val motion: Int,
    val condType: CondType,
    val condArg1: Int,
    val condArg2: Int,
    val destType: DestType,
    val destVal: Int,
    val noDwarves: Boolean,
    val stop: Boolean,
)
"""


def main():
    global db, locnames, msgnames, objnames, motionnames

    with open(YAML_NAME, "r", encoding="utf-8") as f:
        db = yaml.safe_load(f)

    locnames = [x[0] for x in db["locations"]]
    msgnames = [el[0] for el in db["arbitrary_messages"]]
    objnames = [el[0] for el in db["objects"]]
    motionnames = [el[0] for el in db["motions"]]

    (travel, tkey) = buildtravel(db["locations"], db["objects"])

    # 0-origin index of bird's last song. Bird should die after player hears it.
    deathbird = len(dict(db["objects"])["BIRD"]["sounds"]) - 1

    objects_kt = emit_objects(db["objects"])  # populates statedefines
    motions_kt = emit_motions(db["motions"])  # populates ignore
    actions_kt = emit_actions(db["actions"])  # populates ignore

    parts = [
        DONOTEDIT,
        "@file:Suppress(\"unused\", \"ObjectPropertyName\", \"SpellCheckingInspection\")",
        "",
        "package %s" % PACKAGE,
        "",
        TYPE_DEFS,
        CONDBIT_DEFS,
        "// Table sizes",
        "const val NLOCATIONS = %d" % len(db["locations"]),
        "const val NOBJECTS = %d" % len(db["objects"]),
        "const val NHINTS = %d" % len(db["hints"]),
        "const val NCLASSES = %d" % len(db["classes"]),
        "const val NDEATHS = %d" % len(db["obituaries"]),
        "const val NTHRESHOLDS = %d" % len(db["turn_thresholds"]),
        "const val NMOTIONS = %d" % len(db["motions"]),
        "const val NACTIONS = %d" % len(db["actions"]),
        "const val NTRAVEL = %d" % len(travel),
        "const val NKEYS = %d" % len(tkey),
        "const val NDWARVES = %d" % len(db["dwarflocs"]),
        "const val BIRD_ENDSTATE = %d" % deathbird,
        "",
        "// Single-letter words the parser must ignore, from `oldstyle: false` entries",
        "const val IGNORE = %s" % kstr(ignore),
        "",
        emit_refs("Arbitrary message refs", db["arbitrary_messages"]),
        "",
        emit_refs("Location refs", db["locations"]),
        "",
        emit_refs("Object refs", db["objects"]),
        "",
        emit_refs("Motion refs", db["motions"]),
        "",
        emit_refs("Action refs", db["actions"]),
        "",
        "// Object state definitions",
        "\n".join(statedefines),
        "",
        "val dwarflocs: IntArray = intArrayOf(%s)" % ", ".join(db["dwarflocs"]),
        "",
        emit_arbitrary_messages(db["arbitrary_messages"]),
        "",
        emit_locations(db["locations"]),
        "",
        objects_kt,
        "",
        emit_obituaries(db["obituaries"]),
        "",
        emit_hints(db["hints"]),
        "",
        emit_classes(db["classes"]),
        "",
        emit_turn_thresholds(db["turn_thresholds"]),
        "",
        emit_conditions(db["locations"]),
        "",
        motions_kt,
        "",
        actions_kt,
        "",
        emit_travel(travel),
        "",
        emit_tkey(tkey),
        "",
    ]

    os.makedirs(os.path.dirname(KT_NAME), exist_ok=True)
    with open(KT_NAME, "w", encoding="utf-8") as kf:
        kf.write("\n".join(parts))

    print(
        "wrote %s: %d locations, %d objects, %d messages, %d travel ops"
        % (
            os.path.relpath(KT_NAME, ROOT),
            len(db["locations"]),
            len(db["objects"]),
            len(db["arbitrary_messages"]),
            len(travel),
        )
    )


if __name__ == "__main__":
    main()

# end
