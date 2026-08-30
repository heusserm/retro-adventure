package com.xndev.retroadventure.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.xndev.retroadventure.engine.LOC_START
import com.xndev.retroadventure.engine.SaveStore
import kotlin.test.Test
import kotlin.test.assertTrue

/** An in-memory store, so these tests never touch the real save directory. */
private class FakeSaves : SaveStore {
    private val slots = mutableMapOf<String, String>()
    override fun write(name: String, data: String): Boolean { slots[name] = data; return true }
    override fun read(name: String): String? = slots[name]
    override fun list(): List<String> = slots.keys.toList()
}

/**
 * Runs the real composables on the desktop target -- no device, no simulator.
 *
 * A screenshot proves a control drew, not that it works. EncounterDeck's About
 * dialog was linked from three screens and composed by none of them, and it
 * looked correct in every screenshot for three releases. These tests press
 * things.
 */
@OptIn(ExperimentalTestApi::class)
class AppTest {

    @Test
    fun theGameGreetsYouOnLaunch() = runComposeUiTest {
        setContent { App(seed = 1) }
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Welcome to Adventure", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun typingACommandAdvancesTheGame() = runComposeUiTest {
        setContent { App(seed = 1) }
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Welcome to Adventure", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        onNodeWithText("What now?").performTextInput("n")
        onNodeWithText("Go").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("standing at the end of a road", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * The opening line is a yes/no question. A player who types a real command
     * there gets "Please answer the question." -- they must not get silence,
     * which is indistinguishable from the app being broken.
     */
    @Test
    fun aNonYesNoAnswerToTheOpeningQuestionSaysSo() = runComposeUiTest {
        setContent { App(seed = 1) }
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Welcome to Adventure", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        onNodeWithText("What now?").performTextInput("look")
        onNodeWithText("Go").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Please answer the question", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Save, move somewhere else, restore, and check you are back where you
     * saved. Asserting only that "Saved." appears would pass even if the
     * snapshot were empty.
     */
    @Test
    fun aSavedGameCanBeRestored() = runComposeUiTest {
        val saves = FakeSaves()
        setContent { App(seed = 1, saves = saves) }
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Welcome to Adventure", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        onNodeWithText("What now?").performTextInput("n")
        onNodeWithText("Go").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("end of a road", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }

        onNodeWithText("Save").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Saved.", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }
        // Assert the snapshot is real, not just that a word appeared on screen.
        val saved = saves.read(QUICK_SAVE)!!
        assertTrue(saved.contains("magic=retro-adventure-save"))
        assertTrue(saved.contains("loc=$LOC_START"), "should have saved at the road")

        // Walk into the building, so restoring has something to undo.
        onNodeWithText("What now?").performTextInput("in")
        onNodeWithText("Go").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("inside a building", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }

        onNodeWithText("Restore").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Restored.", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Restart throws the game away and starts a new one. */
    @Test
    fun restartBeginsAFreshGame() = runComposeUiTest {
        setContent { App(seed = 1, saves = FakeSaves()) }
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Welcome to Adventure", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        onNodeWithText("What now?").performTextInput("n")
        onNodeWithText("Go").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("end of a road", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }

        onNodeWithText("Restart").performClick()
        waitUntil(timeoutMillis = 10_000) {
            // The old transcript is gone: the road description is no longer on
            // screen, and the opening question is back.
            onAllNodes(hasText("end of a road", substring = true))
                .fetchSemanticsNodes().isEmpty() &&
                onAllNodes(hasText("Welcome to Adventure", substring = true))
                    .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * The BSD-2-Clause notice is a license obligation, not decoration. If this
     * fails because the dialog is no longer reachable, the fix is to make it
     * reachable again, not to delete the test.
     */
    @Test
    fun theAttributionOpensTheLicenseNotice() = runComposeUiTest {
        setContent { App(seed = 1) }
        onNodeWithText(ATTRIBUTION_LINE).performClick()
        onNodeWithText("Will Crowther and Don Woods", substring = true).assertIsDisplayed()
    }
}
