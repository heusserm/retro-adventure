package com.xndev.retroadventure.app

/**
 * Undo the game's hard line wrapping for display.
 *
 * The game text was written for a 70-column terminal and carries its line
 * breaks baked in, mid-sentence. On a phone that wraps twice -- once by the
 * author, once by the layout -- and comes out as a ragged column that is
 * genuinely hard to read.
 *
 * This is display-only, and deliberately so. The engine's output is compared
 * byte for byte against upstream's recorded transcripts, so it cannot be
 * touched; reflowing here means the tests keep their oracle and the player
 * still gets readable paragraphs.
 *
 * What is preserved, because it is formatting rather than wrapping:
 *
 *  - Blank lines, which separate paragraphs and are load-bearing in the game's
 *    pacing.
 *  - Echoed commands, the "> take lamp" lines, which are their own turn.
 *  - Indented lines. The game uses leading tabs for its few centered flourishes
 *    such as the "- - -" divider in the instructions, and joining those onto the
 *    previous sentence would look like a bug.
 */
fun reflow(text: String): String {
    val out = StringBuilder()
    val paragraph = StringBuilder()

    fun flush() {
        if (paragraph.isNotEmpty()) {
            out.append(paragraph).append('\n')
            paragraph.clear()
        }
    }

    // Text ending in a newline splits to a trailing empty element. Emitting it
    // as a blank line appends a spurious one to every render, and since output
    // is appended turn by turn those accumulate down the transcript.
    val lines = text.split("\n").let {
        if (it.isNotEmpty() && it.last().isEmpty()) it.dropLast(1) else it
    }

    for (raw in lines) {
        val line = raw.trimEnd('\r')
        when {
            line.isBlank() -> {
                flush()
                out.append('\n')
            }
            // An echoed command, or a deliberately indented flourish.
            line.startsWith("> ") || line.startsWith(" ") || line.startsWith("\t") -> {
                flush()
                out.append(line).append('\n')
            }
            else -> {
                if (paragraph.isNotEmpty()) paragraph.append(' ')
                paragraph.append(line.trim())
            }
        }
    }
    flush()
    return out.toString()
}
