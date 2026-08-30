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
     * Upstream's `phase_codes_t`, reduced to the codes the ported paths return.
     * These are not cosmetic: they decide whether the next turn re-describes the
     * location (TOP) or goes straight back to the prompt (CLEAROBJ). Getting
     * that wrong inserts a room description after every verb, which is what a
     * transcript diff catches and a play-through does not.
     */
    private enum class Phase { CLEAROBJ, TOP, EXECUTED, TERMINATE }

    /**
     * A stand-in for upstream's `action()`. Only the verbs needed to walk
     * around and handle objects are here; everything else says so out loud.
     */
    private fun action(): Phase {
        val verb = word1.id
        when (verb) {
            SEED -> {
                // Note this speaks the *action's* own message, not an
                // arbitrary message -- actions carry a message field of their
                // own and rspeak() would index the wrong table entirely.
                val n = word2.raw.toIntOrNull() ?: 0
                speak(actions[SEED].message, n)
                game.rng.setSeed(n)
                game.turns--
                return Phase.TOP
            }
            CARRY -> return doTake()
            DROP -> return doDrop()
            INVENTORY -> return doInventory()
            QUIT -> return Phase.TERMINATE
        }
        out.line(); out.line("$NOT_PORTED verb ${word1.raw}")
        return Phase.CLEAROBJ
    }

    private fun doTake(): Phase {
        val obj = if (word2.type == WordType.OBJECT) word2.id else INTRANSITIVE
        if (obj == INTRANSITIVE) { rspeak(NO_CARRY); return Phase.CLEAROBJ }
        if (game.toting(obj)) { rspeak(ALREADY_CARRYING); return Phase.CLEAROBJ }
        if (!game.here(obj)) { rspeak(NO_CARRY); return Phase.CLEAROBJ }
        if (game.objectState[obj].fixed != IS_FREE) { rspeak(YOU_JOKING); return Phase.CLEAROBJ }
        if (game.holdng >= INVLIMIT) { rspeak(CARRY_LIMIT); return Phase.CLEAROBJ }
        game.carry(obj, game.loc)
        rspeak(OK_MAN)
        return Phase.CLEAROBJ
    }

    private fun doDrop(): Phase {
        val obj = if (word2.type == WordType.OBJECT) word2.id else INTRANSITIVE
        if (obj == INTRANSITIVE || !game.toting(obj)) { rspeak(ARENT_CARRYING); return Phase.CLEAROBJ }
        game.drop(obj, game.loc)
        rspeak(OK_MAN)
        return Phase.CLEAROBJ
    }

    private fun doInventory(): Phase {
        var spk = NO_CARRY
        for (i in 1 until NOBJECTS) {
            if (i == BEAR || !game.toting(i)) continue
            if (spk == NO_CARRY) rspeak(NOW_HOLDING)
            pspeak(i, SpeakType.TOUCH, false, -1)
            spk = 0
        }
        if (game.toting(BEAR)) { rspeak(TAME_BEAR); spk = 0 }
        if (spk == NO_CARRY) rspeak(spk)
        return Phase.CLEAROBJ
    }

    /**
     * Upstream `do_command()`. The two nested loops are upstream's and they
     * matter: the outer one describes the location and lists what is here, the
     * inner one takes commands until one of them actually moves the player.
     * Collapsing them into a single loop re-describes the room after every
     * verb.
     */
    private fun doCommand(): Boolean {
        while (true) { // command.state != EXECUTED
            describeLocation()
            if (game.forced(game.loc)) {
                playermove(HERE)
                return true
            }
            listObjects()

            var backToTop = false
            while (!backToTop) { // command.state <= GIVEN
                val line = getInput() ?: return false

                // Every input, check the "foobar" flag: if positive make it
                // negative, if negative he skipped a word so reset it.
                game.foobar = if (game.foobar > WORD_EMPTY) -game.foobar else WORD_EMPTY
                game.turns++

                val (w1, w2) = Vocabulary.tokenize(line, game.zzword)
                word1 = w1
                word2 = w2

                if (word1.isEmpty) continue

                if (word1.id == WORD_NOT_FOUND && word1.type == WordType.NONE) {
                    sspeak(DONT_KNOW, word1.raw)
                    continue
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
                        continue
                    }
                    WordType.OBJECT -> {
                        // "<object> <verb>" is a legal irregular form that
                        // upstream's preprocess_command() rewrites. Not ported.
                        out.line(); out.line("$NOT_PORTED bare object ${word1.raw}")
                        continue
                    }
                    WordType.ACTION -> when (action()) {
                        Phase.TERMINATE -> return false
                        Phase.EXECUTED -> return true
                        Phase.TOP -> backToTop = true
                        Phase.CLEAROBJ -> {}
                    }
                    WordType.NONE -> continue
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
