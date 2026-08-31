package com.xndev.retroadventure.app

/**
 * How big the transcript should be, and how wide it should be allowed to get.
 *
 * A phone and a 13-inch iPad are not the same reading problem. At a fixed size
 * the text is right on a phone and tiny on a tablet; at full width a tablet
 * line runs to about 150 characters, which is roughly twice what anyone can
 * read comfortably. Typographers put the comfortable range at 45-75 characters,
 * and the game's own text was wrapped for 70, so a column near that is both
 * readable and what the author assumed.
 *
 * These are plain functions of the available width so they can be tested
 * without a screen.
 */

/** Point size for the transcript, given the available width in dp. */
fun transcriptFontSize(widthDp: Float): Int = when {
    widthDp < 600f -> 13   // phone
    widthDp < 900f -> 17   // small tablet, or a phone in landscape
    else -> 20             // 12.9" and 13" tablets
}

/**
 * The widest the transcript column should get, in dp. Beyond this the text is
 * centered with margins rather than stretched, which keeps the line length in
 * the readable range instead of following the glass.
 */
fun readingWidthDp(widthDp: Float): Float = minOf(widthDp, 760f)
