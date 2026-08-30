package com.xndev.retroadventure.engine

/**
 * A single playthrough: the main loop from upstream's main.c.
 *
 * PORT STATUS: partial. The data tables, the RNG, the vocabulary, movement and
 * the output pipeline are ported and verified. `action()` -- upstream's
 * actions.c, 1677 lines -- is mostly not, and unported verbs answer with the
 * NOT_PORTED marker below rather than silently doing nothing. Run
 * TranscriptTest to see exactly which transcript diverges where.
 *
 * On the input model: upstream pulls lines from stdin from inside the loop, and
 * the loop asks questions mid-turn ("Would you like instructions?"), so it is
 * not a `String -> String` function per command. Rather than flatten that into a
 * state machine -- which would mean restructuring every yes/no site and is the
 * kind of change that quietly breaks transcript fidelity -- the loop keeps its
 * shape and pulls from an [InputSource]. The transcript harness passes a file;
 * a UI passes a blocking queue fed from the text field. See AGENTS.md.
 */

fun interface InputSource {
    /** Next line of input, or null at end of input. */
    fun readLine(): String?
}

const val PROMPT = "> "

/** Emitted where upstream would run a verb this port has not reached yet. */
const val NOT_PORTED = "[not ported yet]"

/** How many forced moves in a row without input before we call it a loop. */
const val FORCED_MOVE_LIMIT = 200

class Adventure(
    private val input: InputSource,
    val out: Output = Output(),
    private val prompt: Boolean = true,
) {
    val game = GameState()

    private var word1: Word = EMPTY_WORD
    private var word2: Word = EMPTY_WORD

    // --- output helpers, from misc.c ---

    private fun vspeak(msg: String?, blank: Boolean, args: List<Any> = emptyList()) {
        val rendered = renderMessage(msg, args, inside = game.inside(game.loc)) ?: return
        if (rendered.isEmpty()) return
        if (blank) out.line()
        out.line(rendered)
    }

    private fun speak(msg: String?, vararg args: Any) = vspeak(msg, true, args.toList())

    private fun rspeak(i: Int, vararg args: Any) =
        vspeak(arbitraryMessages[i], true, args.toList())

    /** Upstream `sspeak()`: unconditional newline, no blank-line suppression. */
    private fun sspeak(msg: Int, vararg args: Any) {
        out.line()
        out.line(renderMessage(arbitraryMessages[msg], args.toList(), inside = game.inside(game.loc)) ?: "")
    }

    private fun pspeak(obj: Int, mode: SpeakType, blank: Boolean, skip: Int, vararg args: Any) {
        val msg = when (mode) {
            SpeakType.TOUCH -> objects[obj].inventory
            SpeakType.LOOK -> objects[obj].descriptions.getOrNull(skip)
            SpeakType.HEAR -> objects[obj].sounds.getOrNull(skip)
            SpeakType.STUDY -> objects[obj].texts.getOrNull(skip)
            SpeakType.CHANGE -> objects[obj].changes.getOrNull(skip)
        }
        vspeak(msg, blank, args.toList())
    }

    // --- input, from misc.c get_input() ---

    private fun getInput(): String? {
        val inputPrompt = if (prompt) PROMPT else ""
        out.line() // upstream prints one blank line per input, before the comment loop
        while (true) {
            val line = input.readLine() ?: return null
            if (line.startsWith("#")) continue // comments in test scripts
            inputsRead++
            val stripped = line.trimEnd('\n', '\r')
            out.raw(inputPrompt)
            out.line(stripped)
            return stripped
        }
    }

    /** Upstream `yes_or_no()`. */
    private fun yesOrNo(question: String?, yesResponse: String?, noResponse: String?): Boolean {
        while (true) {
            speak(question)
            val reply = getInput() ?: return false
            if (reply.isEmpty()) {
                rspeak(PLEASE_ANSWER)
                continue
            }
            val firstword = reply.trim().split(Regex("[ \t]+"))[0].lowercase()
            when {
                "yes".startsWith(firstword) || firstword.startsWith("y") -> {
                    speak(yesResponse); return true
                }
                "no".startsWith(firstword) || firstword.startsWith("n") -> {
                    speak(noResponse); return false
                }
                else -> rspeak(PLEASE_ANSWER)
            }
        }
    }

    // --- the loop, from main.c ---

    /** Upstream `describe_location()`. */
    private fun describeLocation() {
        var msg = locations[game.loc].small
        if (game.locs[game.loc].abbrev % game.abbnum == 0 || msg == null) {
            msg = locations[game.loc].big
        }
        if (!game.forced(game.loc) && game.isDarkHere()) {
            msg = arbitraryMessages[PITCH_DARK]
        }
        if (game.toting(BEAR)) rspeak(TAME_BEAR)
        speak(msg)
        if (game.loc == LOC_Y2 && game.rng.pct(25) && !game.closng) rspeak(SAYS_PLUGH)
    }

    /** Upstream `listobjects()`. */
    private fun listObjects() {
        if (game.isDarkHere()) return
        game.locs[game.loc].abbrev++
        var i = game.locs[game.loc].atloc
        while (i != 0) {
            var obj = i
            if (obj >= NOBJECTS) obj -= NOBJECTS
            if (!(obj == STEPS && game.toting(NUGGET))) {
                if (game.objectIsStashed(obj) || game.objectIsNotFound(obj)) {
                    if (!game.closed) {
                        game.objectSetFound(obj)
                        if (obj == RUG) game.objectState[RUG].prop = RUG_DRAGON
                        if (obj == CHAIN) game.objectState[CHAIN].prop = CHAINING_BEAR
                        if (obj == EGGS) game.seenbigwords = true
                        game.tally--
                    }
                }
                if (!(game.closed && (game.objectIsStashed(obj) || game.objectIsNotFound(obj)))) {
                    var kk = game.objectState[obj].prop
                    if (obj == STEPS) {
                        kk = if (game.loc == game.objectState[STEPS].fixed) STEPS_UP else STEPS_DOWN
                    }
                    pspeak(obj, SpeakType.LOOK, true, kk)
                }
            }
            i = game.link[i]
        }
    }

    /** Upstream `traveleq()`. */
    private fun traveleq(a: Int, b: Int): Boolean =
        travel[a].condType == travel[b].condType &&
            travel[a].condArg1 == travel[b].condArg1 &&
            travel[a].condArg2 == travel[b].condArg2 &&
            travel[a].destType == travel[b].destType &&
            travel[a].destVal == travel[b].destVal

    /**
     * Upstream `playermove()`. The dest_special branch dispatches into
     * main.c's special-travel code, which is not ported; it announces
     * NOT_PORTED and leaves the player put.
     */
    private fun playermove(motionIn: Int) {
        var motion = motionIn
        var te = tkey[game.loc]
        game.newloc = game.loc
        if (te == 0) return

        when {
            motion == NUL -> return

            motion == BACK -> {
                motion = game.oldloc
                if (game.forced(motion)) motion = game.oldlc2
                game.oldlc2 = game.oldloc
                game.oldloc = game.loc
                if (game.cndbit(game.loc, COND_NOBACK)) { rspeak(TWIST_TURN); return }
                if (motion == game.loc) { rspeak(FORGOT_PATH); return }

                var teTmp = 0
                while (true) {
                    val destType = travel[te].destType
                    val scratchloc = travel[te].destVal
                    if (destType != DestType.GOTO || scratchloc != motion) {
                        if (destType == DestType.GOTO &&
                            game.forced(scratchloc) &&
                            travel[tkey[scratchloc]].destVal == motion
                        ) {
                            teTmp = te
                        }
                        if (!travel[te].stop) { te++; continue }
                        te = teTmp
                        if (te == 0) { rspeak(NOT_CONNECTED); return }
                    }
                    motion = travel[te].motion
                    te = tkey[game.loc]
                    break
                }
            }

            motion == LOOK -> {
                if (game.detail < 3) rspeak(NO_MORE_DETAIL)
                game.detail++
                game.wzdark = false
                game.locs[game.loc].abbrev = 0
                return
            }

            motion == CAVE -> {
                rspeak(if (game.outside(game.loc) && game.loc != LOC_GRATE) FOLLOW_STREAM else NEED_DETAIL)
                return
            }

            else -> {
                game.oldlc2 = game.oldloc
                game.oldloc = game.loc
            }
        }

        // Find a travel entry for this motion.
        while (true) {
            if (travel[te].motion == HERE || travel[te].motion == motion) break
            if (travel[te].stop) {
                when (motion) {
                    EAST, WEST, SOUTH, NORTH, NE, NW, SW, SE, UP, DOWN -> rspeak(BAD_DIRECTION)
                    FORWARD, LEFT, RIGHT -> rspeak(UNSURE_FACING)
                    OUTSIDE, INSIDE -> rspeak(NO_INOUT_HERE)
                    XYZZY, PLUGH -> rspeak(NOTHING_HAPPENS)
                    CRAWL -> rspeak(WHICH_WAY)
                    else -> rspeak(CANT_APPLY)
                }
                return
            }
            te++
        }

        // Check the conditionals on this destination and any that follow it.
        while (true) {
            val condType = travel[te].condType
            val condArg1 = travel[te].condArg1
            val condArg2 = travel[te].condArg2
            val ok = when {
                condType == CondType.GOTO || condType == CondType.PCT ->
                    condArg1 == 0 || game.rng.pct(condArg1)
                condType == CondType.CARRY || condType == CondType.WITH ->
                    game.toting(condArg1) || (condType == CondType.WITH && game.at(condArg1))
                else -> game.objectState[condArg1].prop != condArg2
            }
            if (ok) break
            // Conditional failed: skip to the next non-matching destination.
            var teTmp = te
            do {
                if (travel[teTmp].stop) return // upstream BUGs here; a port should not crash a phone
                teTmp++
            } while (traveleq(te, teTmp))
            te = teTmp
        }

        game.newloc = travel[te].destVal
        when (travel[te].destType) {
            DestType.GOTO -> return
            DestType.SPEAK -> {
                rspeak(game.newloc)
                game.newloc = game.loc
                return
            }
            DestType.SPECIAL -> {
                out.line(); out.line("$NOT_PORTED special travel to ${game.newloc}")
                game.newloc = game.loc
                return
            }
        }
    }

    /** Upstream `do_move()`, minus dwarf movement and death, which are not ported. */
    private fun doMove(): Boolean {
        if (game.outside(game.newloc) && game.newloc != 0 && game.closng) {
            rspeak(EXIT_CLOSED)
            game.newloc = game.loc
            if (!game.panic) game.clock2 = PANICTIME
            game.panic = true
        }
        game.loc = game.newloc
        return true
    }

    /**
     * Upstream's `phase_codes_t`. These are not cosmetic: they decide whether
     * the next turn re-describes the location (TOP), goes straight back to the
     * prompt (CLEAROBJ), or reprocesses the second word without reading new
     * input (WORD2). Getting them wrong inserts a room description after every
     * verb, which a transcript diff catches and a play-through does not.
     */
    private enum class Phase { CLEAROBJ, TOP, EXECUTED, TERMINATE, WORD2, UNKNOWN, CHECKHINT }

    /** Upstream's `enum speechpart`. */
    private enum class Part { UNKNOWN, INTRANSITIVE, TRANSITIVE }

    /**
     * Counts inputs consumed, so the loop can tell that it is spinning.
     *
     * A forced location moves the player without asking for input. If the move
     * out of one lands back on itself -- which it can while special travel is
     * unported -- the loop describes the room forever, consuming nothing. The
     * real game cannot do this; a half-ported one can, and it hangs the test
     * suite instead of failing it. The guard below turns that into a named
     * error naming the location, which is a bug report rather than a timeout.
     */
    private var inputsRead = 0

    private var part = Part.UNKNOWN
    private var verb = 0
    private var obj = NO_OBJECT

    private fun clearCommand() {
        word1 = EMPTY_WORD
        word2 = EMPTY_WORD
        part = Part.UNKNOWN
        verb = 0
        obj = NO_OBJECT
    }

    /** Upstream `state_change()`: set the state and announce the change. */
    private fun stateChange(o: Int, state: Int) {
        game.objectState[o].prop = state
        pspeak(o, SpeakType.CHANGE, true, state)
    }

    /**
     * Upstream `action()`.
     *
     * The three-way `part` split is the load-bearing structure, not decoration.
     * "take lamp" arrives as an intransitive CARRY with a second word, which
     * returns WORD2; the loop then re-enters with "lamp" as word one, `verb`
     * still holding CARRY, and this function promotes the pair to transitive.
     * That is how every two-word command in the game is resolved, so verbs are
     * cheap to add once it is right.
     */
    private fun action(): Phase {
        if (actions[verb].noAction) {
            speak(actions[verb].message)
            return Phase.CLEAROBJ
        }

        if (part == Part.UNKNOWN) {
            // Analyse an object word: is the thing here, do we have a verb yet?
            // Water and oil are funny -- they are never actually dropped at a
            // location, but may be here in the bottle or as a feature of the
            // room.
            when {
                game.here(obj) -> {}
                obj == DWARF && game.atdwrf(game.loc) > 0 -> {}
                !game.closed && ((game.liquid() == obj && game.here(BOTTLE)) ||
                    obj == game.liqloc(game.loc)) -> {}
                obj == OIL && game.here(URN) && game.objectState[URN].prop != URN_EMPTY ->
                    obj = URN
                obj == PLANT && game.at(PLANT2) &&
                    game.objectState[PLANT2].prop != PLANT_THIRSTY -> obj = PLANT2
                obj == KNIFE && game.knfloc == game.loc -> {
                    game.knfloc = -1
                    rspeak(KNIVES_VANISH)
                    return Phase.CLEAROBJ
                }
                obj == ROD && game.here(ROD2) -> obj = ROD2
                (verb == FIND || verb == INVENTORY) &&
                    (word2.id == WORD_EMPTY || word2.id == WORD_NOT_FOUND) -> {}
                else -> {
                    sspeak(NO_SEE, word1.raw)
                    return Phase.CLEAROBJ
                }
            }
            if (verb != 0) part = Part.TRANSITIVE
        }

        when (part) {
            Part.INTRANSITIVE -> {
                if (!word2.isEmpty && verb != SAY) return Phase.WORD2
                if (verb == SAY) {
                    // KEYS is not special; anything that is not NO_OBJECT or
                    // INTRANSITIVE will do. This stops an unknown word being
                    // read as an intransitive verb.
                    obj = if (!word2.isEmpty) KEYS else NO_OBJECT
                }
                if (obj == NO_OBJECT || obj == INTRANSITIVE) {
                    return when (verb) {
                        CARRY -> vcarry(INTRANSITIVE)
                        NOTHING -> { rspeak(OK_MAN); Phase.CLEAROBJ }
                        UNLOCK, LOCK -> lock(INTRANSITIVE)
                        LIGHT -> light(INTRANSITIVE)
                        EXTINGUISH -> extinguish(INTRANSITIVE)
                        GO -> { speak(actions[verb].message); Phase.CLEAROBJ }
                        QUIT -> Phase.TERMINATE
                        INVENTORY -> inven()
                        SEED, WASTE -> { rspeak(NUMERIC_REQUIRED); Phase.TOP }
                        DROP, SAY, WAVE, TAME, RUB, THROW, FIND, FEED, BREAK, WAKE ->
                            Phase.UNKNOWN
                        else -> notPorted()
                    }
                }
                // An object turned up after all; fall through as transitive.
                return transitive()
            }
            Part.TRANSITIVE -> return transitive()
            Part.UNKNOWN -> {
                // Unknown verb, couldn't deduce an object -- might need a hint.
                sspeak(WHAT_DO, word1.raw)
                return Phase.CHECKHINT
            }
        }
    }

    private fun transitive(): Phase = when (verb) {
        CARRY -> vcarry(obj)
        DROP -> discard(obj)
        LIGHT -> light(obj)
        EXTINGUISH -> extinguish(obj)
        LOCK, UNLOCK -> lock(obj)
        WAVE -> wave(obj)
        SEED -> {
            // Speaks the *action's* own message, not an arbitrary message --
            // actions carry a message field of their own and rspeak() would
            // index the wrong table entirely.
            val n = word2.raw.toIntOrNull() ?: 0
            speak(actions[SEED].message, n)
            game.rng.setSeed(n)
            game.turns--
            Phase.TOP
        }
        else -> notPorted()
    }

    /**
     * Name the *verb*, not `word1.raw`. By the time a transitive verb gets here
     * the word shift has already put the object in word one, so reporting the
     * raw word blames "rod" for an unported "wave".
     */
    private fun notPorted(): Phase {
        val name = actions.getOrNull(verb)?.words?.firstOrNull() ?: word1.raw
        out.line()
        out.line("$NOT_PORTED verb $name")
        return Phase.CLEAROBJ
    }

    /** Upstream `light()`. Applicable only to the lamp and the urn. */
    private fun light(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            var selects = 0
            if (game.here(LAMP) && game.objectState[LAMP].prop == LAMP_DARK && game.limit >= 0) {
                o = LAMP; selects++
            }
            if (game.here(URN) && game.objectState[URN].prop == URN_DARK) {
                o = URN; selects++
            }
            if (selects != 1) return Phase.UNKNOWN
        }
        when (o) {
            URN -> stateChange(URN, if (game.objectState[URN].prop == URN_EMPTY) URN_EMPTY else URN_LIT)
            LAMP -> {
                if (game.limit < 0) {
                    rspeak(LAMP_OUT)
                } else {
                    stateChange(LAMP, LAMP_BRIGHT)
                    // Lighting the lamp in a room you entered in the dark means
                    // you finally get to see it, so go round to describe it.
                    if (game.wzdark) return Phase.TOP
                }
            }
            else -> speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /** Upstream `extinguish()`. Lamp, urn, and dragon/volcano (nice try). */
    private fun extinguish(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            if (game.here(LAMP) && game.objectState[LAMP].prop == LAMP_BRIGHT) o = LAMP
            if (game.here(URN) && game.objectState[URN].prop == URN_LIT) o = URN
            if (o == INTRANSITIVE) return Phase.UNKNOWN
        }
        when (o) {
            URN -> if (game.objectState[URN].prop != URN_EMPTY) {
                stateChange(URN, URN_DARK)
            } else {
                pspeak(URN, SpeakType.CHANGE, true, URN_DARK)
            }
            LAMP -> {
                stateChange(LAMP, LAMP_DARK)
                rspeak(if (game.isDarkHere()) PITCH_DARK else NO_MESSAGE)
            }
            DRAGON, VOLCANO -> rspeak(BEYOND_POWER)
            else -> speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /** Upstream `lock()`, which serves both "open" and "close". */
    private fun lock(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            if (game.here(CLAM)) o = CLAM
            if (game.here(OYSTER)) o = OYSTER
            if (game.at(DOOR)) o = DOOR
            if (game.at(GRATE)) o = GRATE
            if (game.here(CHAIN)) o = CHAIN
            if (o == INTRANSITIVE) {
                rspeak(NOTHING_LOCKED)
                return Phase.CLEAROBJ
            }
        }
        when (o) {
            CHAIN -> if (game.here(KEYS)) return notPorted() else rspeak(NO_KEYS)
            GRATE -> if (game.here(KEYS)) {
                if (game.closng) {
                    rspeak(EXIT_CLOSED)
                    if (!game.panic) game.clock2 = PANICTIME
                    game.panic = true
                } else {
                    stateChange(GRATE, if (verb == LOCK) GRATE_CLOSED else GRATE_OPEN)
                }
            } else {
                rspeak(NO_KEYS)
            }
            CLAM -> when {
                verb == LOCK -> rspeak(HUH_MAN)
                game.toting(CLAM) -> rspeak(DROP_CLAM)
                !game.toting(TRIDENT) -> rspeak(CLAM_OPENER)
                else -> {
                    game.destroy(CLAM)
                    game.drop(OYSTER, game.loc)
                    game.drop(PEARL, LOC_CULDESAC)
                    rspeak(PEARL_FALLS)
                }
            }
            OYSTER -> when {
                verb == LOCK -> rspeak(HUH_MAN)
                game.toting(OYSTER) -> rspeak(DROP_OYSTER)
                !game.toting(TRIDENT) -> rspeak(OYSTER_OPENER)
                else -> rspeak(OYSTER_OPENS)
            }
            DOOR -> rspeak(if (game.objectState[DOOR].prop == DOOR_UNRUSTED) OK_MAN else RUSTY_DOOR)
            CAGE -> rspeak(NO_LOCK)
            KEYS -> rspeak(CANNOT_UNLOCK)
            else -> speak(actions[verb].message)
        }
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `vcarry()`. The liquids are the interesting part: water and oil
     * are never on any location's object list, so they must be mapped onto the
     * bottle before `carry()` sees them.
     */
    private fun vcarry(objIn: Int): Phase {
        var o = objIn
        if (o == INTRANSITIVE) {
            // Carry with no object given is OK only if exactly one thing is here.
            if (game.locs[game.loc].atloc == NO_OBJECT ||
                game.link[game.locs[game.loc].atloc] != 0 ||
                game.atdwrf(game.loc) > 0
            ) {
                return Phase.UNKNOWN
            }
            o = game.locs[game.loc].atloc
        }

        if (game.toting(o)) { speak(actions[verb].message); return Phase.CLEAROBJ }

        if (o == MESSAG) {
            rspeak(REMOVE_MESSAGE)
            game.destroy(MESSAG)
            return Phase.CLEAROBJ
        }

        if (game.objectState[o].fixed != IS_FREE) {
            when (o) {
                PLANT -> rspeak(
                    if (game.objectState[PLANT].prop == PLANT_THIRSTY || game.objectIsStashed(PLANT))
                        DEEP_ROOTS else YOU_JOKING
                )
                BEAR -> rspeak(
                    if (game.objectState[BEAR].prop == SITTING_BEAR) BEAR_CHAINED else YOU_JOKING
                )
                CHAIN -> rspeak(
                    if (game.objectState[BEAR].prop != UNTAMED_BEAR) STILL_LOCKED else YOU_JOKING
                )
                RUG -> rspeak(
                    if (game.objectState[RUG].prop == RUG_HOVER) RUG_HOVERS else YOU_JOKING
                )
                URN -> rspeak(URN_NOBUDGE)
                CAVITY -> rspeak(DOUGHNUT_HOLES)
                BLOOD -> rspeak(FEW_DROPS)
                SIGN -> rspeak(HAND_PASSTHROUGH)
                else -> rspeak(YOU_JOKING)
            }
            return Phase.CLEAROBJ
        }

        if (o == WATER || o == OIL) {
            if (!game.here(BOTTLE) || game.liquid() != o) {
                if (!game.toting(BOTTLE)) { rspeak(NO_CONTAINER); return Phase.CLEAROBJ }
                if (game.objectState[BOTTLE].prop == EMPTY_BOTTLE) {
                    return notPorted() // upstream calls fill(verb, BOTTLE)
                }
                rspeak(BOTTLE_FULL)
                return Phase.CLEAROBJ
            }
            o = BOTTLE
        }

        if (game.holdng >= INVLIMIT) { rspeak(CARRY_LIMIT); return Phase.CLEAROBJ }

        if (o == BIRD && game.objectState[BIRD].prop != BIRD_CAGED &&
            !game.objectIsStashed(BIRD)
        ) {
            if (game.objectState[BIRD].prop == BIRD_FOREST_UNCAGED) {
                game.destroy(BIRD)
                rspeak(BIRD_CRAP)
                return Phase.CLEAROBJ
            }
            if (!game.toting(CAGE)) { rspeak(CANNOT_CARRY); return Phase.CLEAROBJ }
            if (game.toting(ROD)) { rspeak(BIRD_EVADES); return Phase.CLEAROBJ }
            game.objectState[BIRD].prop = BIRD_CAGED
        }
        if ((o == BIRD || o == CAGE) && game.objectStateEquals(BIRD, BIRD_CAGED)) {
            // This expression maps BIRD to CAGE and CAGE to BIRD.
            game.carry(BIRD + CAGE - o, game.loc)
        }

        game.carry(o, game.loc)

        if (o == BOTTLE && game.liquid() != NO_OBJECT) {
            game.objectState[game.liquid()].place = CARRIED
        }

        if (game.gstone(o) && game.objectState[o].prop != STATE_FOUND) {
            game.objectSetFound(o)
            game.objectState[CAVITY].prop = CAVITY_EMPTY
        }
        rspeak(OK_MAN)
        return Phase.CLEAROBJ
    }

    /** A cut-down `discard()`. */
    private fun discard(objIn: Int): Phase {
        if (objIn == INTRANSITIVE || !game.toting(objIn)) {
            speak(actions[verb].message)
            return Phase.CLEAROBJ
        }
        game.drop(objIn, game.loc)
        rspeak(OK_MAN)
        return Phase.CLEAROBJ
    }

    /** Upstream `wave()`. No effect unless waving the rod at the fissure or the bird. */
    private fun wave(o: Int): Phase {
        if (o != ROD || !game.toting(o) ||
            (!game.here(BIRD) && (game.closng || !game.at(FISSURE)))
        ) {
            speak(
                if (!game.toting(o) && (o != ROD || !game.toting(ROD2)))
                    arbitraryMessages[ARENT_CARRYING]
                else
                    actions[verb].message
            )
            return Phase.CLEAROBJ
        }

        if (game.objectState[BIRD].prop == BIRD_UNCAGED &&
            game.loc == game.objectState[STEPS].place &&
            game.objectIsNotFound(JADE)
        ) {
            game.drop(JADE, game.loc)
            game.objectSetFound(JADE)
            game.tally--
            rspeak(NECKLACE_FLY)
            return Phase.CLEAROBJ
        }

        if (game.closed) {
            rspeak(if (game.objectState[BIRD].prop == BIRD_CAGED) CAGE_FLY else FREE_FLY)
            return notPorted() // upstream returns GO_DWARFWAKE
        }
        if (game.closng || !game.at(FISSURE)) {
            rspeak(if (game.objectState[BIRD].prop == BIRD_CAGED) CAGE_FLY else FREE_FLY)
            return Phase.CLEAROBJ
        }
        if (game.here(BIRD)) {
            rspeak(if (game.objectState[BIRD].prop == BIRD_CAGED) CAGE_FLY else FREE_FLY)
        }
        stateChange(
            FISSURE,
            if (game.objectState[FISSURE].prop == BRIDGED) UNBRIDGED else BRIDGED
        )
        return Phase.CLEAROBJ
    }

    /** Upstream `inven()`. */
    private fun inven(): Phase {
        var empty = true
        for (i in 1 until NOBJECTS) {
            if (i == BEAR || !game.toting(i)) continue
            if (empty) { rspeak(NOW_HOLDING); empty = false }
            pspeak(i, SpeakType.TOUCH, false, -1)
        }
        if (game.toting(BEAR)) { rspeak(TAME_BEAR); empty = false }
        if (empty) rspeak(NO_CARRY)
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `do_command()`. The loop nesting is upstream's and it matters:
     * the outer loop describes the location and lists what is here, the middle
     * one takes commands until one actually moves the player, and the inner one
     * reprocesses a shifted second word without reading new input. Collapsing
     * any of them re-describes the room at the wrong time.
     */
    private fun doCommand(): Boolean {
        var spinGuard = inputsRead
        var spins = 0
        outer@ while (true) {
            if (inputsRead == spinGuard) {
                if (++spins > FORCED_MOVE_LIMIT) {
                    throw IllegalStateException(
                        "stuck in a forced-location loop at location ${game.loc}" +
                            " after ${game.turns} turns; likely unported special travel"
                    )
                }
            } else {
                spinGuard = inputsRead
                spins = 0
            }
            describeLocation()
            if (game.forced(game.loc)) {
                playermove(HERE)
                return true
            }
            listObjects()
            clearCommand()

            input@ while (true) {
                // Whether the room was dark on entry. light() consults this to
                // decide whether turning the lamp on should re-describe the
                // room you have just revealed -- without it, the description
                // never appears and 88 transcripts diverge at the same line.
                game.wzdark = game.isDarkHere()

                // If the knife is not here it permanently disappears.
                if (game.knfloc > LOC_NOWHERE && game.knfloc != game.loc) {
                    game.knfloc = LOC_NOWHERE
                }

                val line = getInput() ?: return false

                // Every input, check the "foobar" flag: if positive make it
                // negative, if negative he skipped a word so reset it.
                game.foobar = if (game.foobar > WORD_EMPTY) -game.foobar else WORD_EMPTY
                game.turns++

                val (w1, w2) = Vocabulary.tokenize(line, game.zzword)
                word1 = w1
                word2 = w2

                while (true) { // reprocess after a word shift, no new input
                    if (word1.isEmpty) continue@input

                    if (word1.id == WORD_NOT_FOUND && word1.type == WordType.NONE) {
                        sspeak(DONT_KNOW, word1.raw)
                        clearCommand()
                        continue@input
                    }

                    // Nudge the player toward the shorter forms, as upstream does.
                    if (word1.raw.equals("west", ignoreCase = true)) {
                        if (++game.iwest == 10) rspeak(W_IS_WEST)
                    }
                    if (word1.raw.equals("go", ignoreCase = true) && !word2.isEmpty) {
                        if (++game.igo == 10) rspeak(GO_UNNEEDED)
                    }

                    when (word1.type) {
                        WordType.MOTION -> {
                            playermove(word1.id)
                            return true
                        }
                        WordType.NUMERIC -> {
                            sspeak(DONT_KNOW, word1.raw)
                            clearCommand()
                            continue@input
                        }
                        WordType.OBJECT -> {
                            part = Part.UNKNOWN
                            obj = word1.id
                        }
                        WordType.ACTION -> {
                            part = if (word2.type == WordType.NUMERIC) Part.TRANSITIVE
                                   else Part.INTRANSITIVE
                            verb = word1.id
                        }
                        WordType.NONE -> continue@input
                    }

                    when (action()) {
                        Phase.TERMINATE -> return false
                        Phase.EXECUTED -> return true
                        Phase.TOP -> continue@outer
                        Phase.WORD2 -> {
                            // Shift the second word up and analyse it, keeping
                            // `verb` -- this is how "take lamp" resolves.
                            word1 = word2
                            word2 = EMPTY_WORD
                            continue
                        }
                        Phase.UNKNOWN -> {
                            // Random intransitive verbs land here. Clear the
                            // object just in case.
                            val raw = word1.raw.replaceFirstChar { it.uppercaseChar() }
                            sspeak(DO_WHAT, raw)
                            obj = NO_OBJECT
                            continue@input
                        }
                        Phase.CHECKHINT -> continue@input
                        Phase.CLEAROBJ -> {
                            clearCommand()
                            continue@input
                        }
                    }
                }
            }
        }
    }

    /** Upstream `main()`: initialise, greet, then loop until end of input. */
    fun run(seedval: Int) {
        game.initialise(seedval)
        game.novice = yesOrNo(
            arbitraryMessages[WELCOME_YOU],
            arbitraryMessages[CAVE_NEARBY],
            arbitraryMessages[NO_MESSAGE],
        )
        if (game.novice) game.limit = NOVICELIMIT

        while (true) {
            if (!doMove()) continue
            if (!doCommand()) break
        }
    }
}
