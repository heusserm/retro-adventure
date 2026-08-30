package com.xndev.retroadventure.engine

/**
 * The output side of upstream's misc.c: vspeak/speak/rspeak/pspeak/sspeak.
 *
 * Everything the game says goes through here so the transcript harness can
 * capture it. Upstream writes straight to stdout; a phone has no stdout, so the
 * engine accumulates lines instead and the front end renders them. The blank
 * lines matter -- upstream emits one before nearly every message and the .chk
 * files record them, so a port that "tidies up" the spacing fails every
 * transcript while looking perfectly correct on screen.
 */
class Output {
    private val sb = StringBuilder()

    fun raw(text: String) {
        sb.append(text)
    }

    fun line(text: String = "") {
        sb.append(text).append('\n')
    }

    fun take(): String {
        val s = sb.toString()
        sb.clear()
        return s
    }

    override fun toString(): String = sb.toString()
}

/** Upstream's `enum speaktype`. */
enum class SpeakType { TOUCH, LOOK, HEAR, STUDY, CHANGE }

/**
 * Upstream `vspeak()`.
 *
 * Handles the format specifiers upstream invented: %d (integer), %s (string),
 * %S (an "s" iff the *previous* %d was not 1), %V (version). It also carries
 * upstream's "floor" -> "ground" swap for when the player is outdoors, which is
 * a text substitution on the message body rather than an argument -- easy to
 * miss, and it shows up in transcripts that drop things outside.
 */
fun renderMessage(msg: String?, args: List<Any> = emptyList(), inside: Boolean = true, version: String = VERSION): String? {
    if (msg == null || msg.isEmpty()) return null

    val out = StringBuilder()
    var argIndex = 0
    var pluralize = false
    var i = 0
    while (i < msg.length) {
        val c = msg[i]
        if (c != '%') {
            // Least obtrusive way to deal with artifacts "on the floor" being
            // dropped outside of both cave and building.
            if (!inside && msg.startsWith("floor", i)) {
                val after = if (i + 5 < msg.length) msg[i + 5] else ' '
                if (after == ' ' || after == '.') {
                    out.append("ground")
                    i += 5
                    continue
                }
            }
            out.append(c)
            i++
            continue
        }
        i++
        if (i >= msg.length) break
        when (msg[i]) {
            'd' -> {
                val arg = (args.getOrNull(argIndex++) as? Int) ?: 0
                out.append(arg)
                pluralize = arg != 1
            }
            's' -> out.append(args.getOrNull(argIndex++)?.toString() ?: "")
            'S' -> if (pluralize) out.append('s')
            'V' -> out.append(version)
            else -> out.append(msg[i])
        }
        i++
    }
    return out.toString()
}

const val VERSION = "1.22"
