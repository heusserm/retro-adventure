package com.xndev.retroadventure.engine

/**
 * Word lookup, ported from the get_*_vocab_id / get_vocab_metadata block of
 * upstream's misc.c.
 *
 * Two things here are load-bearing and look like sloppiness if you don't know
 * the history:
 *
 *  - Words compare on their first TOKLEN (5) characters only, case-insensitively.
 *    That is why the YAML spells the lamp's second word "lante" and downstream
 *    "downs". "lanternfish" is a lantern to this parser, and matching on the
 *    whole word instead would silently break dozens of accepted commands.
 *  - Lookup order is motion, then object, then action, first match wins. Several
 *    words live in more than one table ("water" is an object and part of an
 *    action phrase); the order is what disambiguates them.
 *  - IGNORE only suppresses single-letter words in *oldstyle* mode. Upstream's
 *    condition is `len > 1 || not in ignore || !oldstyle`, and dropping that
 *    last term rejects "z" (wait), "l" (look), "i" (inventory) and "x" in
 *    modern mode -- which reads as a data problem and is not one.
 */

const val TOKLEN = 5
const val WORD_EMPTY = 0
const val WORD_NOT_FOUND = -1

enum class WordType { NONE, MOTION, OBJECT, ACTION, NUMERIC }

data class Word(val raw: String, val id: Int, val type: WordType) {
    val isEmpty: Boolean get() = raw.isEmpty()
}

val EMPTY_WORD = Word("", WORD_EMPTY, WordType.NONE)

private fun tokenMatches(word: String, entry: String): Boolean =
    word.take(TOKLEN).lowercase() == entry.take(TOKLEN).lowercase()

object Vocabulary {
    /** Upstream's `settings.oldstyle`: emulate the 1977 UI, including its warts. */
    var oldstyle: Boolean = false

    private fun accepted(word: String): Boolean =
        word.length > 1 || !IGNORE.contains(word[0].uppercaseChar()) || !oldstyle

    fun motionId(word: String): Int {
        for (i in motions.indices) {
            for (w in motions[i].words) {
                if (tokenMatches(word, w) && accepted(word)) return i
            }
        }
        return WORD_NOT_FOUND
    }

    fun objectId(word: String): Int {
        for (i in objects.indices) {
            for (w in objects[i].words) {
                if (tokenMatches(word, w)) return i
            }
        }
        return WORD_NOT_FOUND
    }

    fun actionId(word: String): Int {
        for (i in actions.indices) {
            for (w in actions[i].words) {
                if (tokenMatches(word, w) && accepted(word)) return i
            }
        }
        return WORD_NOT_FOUND
    }

    private fun isValidInt(s: String): Boolean {
        val body = if (s.startsWith("-")) s.substring(1) else s
        return body.isNotEmpty() && body.all { it.isDigit() }
    }

    /** Upstream `get_vocab_metadata()`. `zzword` is the bird's magic word. */
    fun classify(word: String, zzword: String): Word {
        if (word.isEmpty()) return EMPTY_WORD

        var ref = motionId(word)
        if (ref != WORD_NOT_FOUND) return Word(word, ref, WordType.MOTION)

        ref = objectId(word)
        if (ref != WORD_NOT_FOUND) return Word(word, ref, WordType.OBJECT)

        ref = actionId(word)
        if (ref != WORD_NOT_FOUND && ref != PART) return Word(word, ref, WordType.ACTION)

        // The reservoir magic word, which changes with the seed.
        if (word.equals(zzword, ignoreCase = true)) return Word(word, PART, WordType.ACTION)

        if (isValidInt(word)) return Word(word, WORD_EMPTY, WordType.NUMERIC)

        return Word(word, WORD_NOT_FOUND, WordType.NONE)
    }

    /**
     * Upstream `tokenize()`: take the first two whitespace-separated tokens and
     * classify them. Anything past the second word is discarded, exactly as
     * upstream's `sscanf(raw, "%s%s", ...)` does.
     */
    fun tokenize(raw: String, zzword: String): Pair<Word, Word> {
        val parts = raw.trim().split(Regex("[ \t]+")).filter { it.isNotEmpty() }
        val first = classify(parts.getOrElse(0) { "" }, zzword)
        val second = classify(parts.getOrElse(1) { "" }, zzword)
        return first to second
    }
}
