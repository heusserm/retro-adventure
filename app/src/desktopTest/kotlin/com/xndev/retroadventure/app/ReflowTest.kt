package com.xndev.retroadventure.app

import kotlin.test.Test
import kotlin.test.assertEquals

class ReflowTest {

    @Test
    fun aWrappedParagraphBecomesOneLine() {
        val wrapped = "You are standing at the end of a road before a small brick building.\n" +
            "Around you is a forest.  A small stream flows out of the building and\n" +
            "down a gully.\n"
        assertEquals(
            "You are standing at the end of a road before a small brick building. " +
                "Around you is a forest.  A small stream flows out of the building and " +
                "down a gully.\n",
            reflow(wrapped),
        )
    }

    @Test
    fun blankLinesStillSeparateParagraphs() {
        val text = "\n> look\n\nYou are inside a building, a well house for a large spring.\n\n" +
            "There are some keys on the ground here.\n"
        assertEquals(
            "\n> look\n\nYou are inside a building, a well house for a large spring.\n\n" +
                "There are some keys on the ground here.\n",
            reflow(text),
        )
    }

    @Test
    fun echoedCommandsKeepTheirOwnLine() {
        assertEquals("> take lamp\nOK\n", reflow("> take lamp\nOK\n"))
    }

    @Test
    fun indentedFlourishesAreLeftAlone() {
        // The instructions end with a tab-indented divider; joining it onto the
        // previous sentence would read as a bug.
        val text = "features of the current program were added by Don Woods.\n\t\t\t      - - -\n"
        assertEquals(
            "features of the current program were added by Don Woods.\n\t\t\t      - - -\n",
            reflow(text),
        )
    }

    @Test
    fun theEmptyTranscriptSurvives() {
        assertEquals("", reflow(""))
    }
}
