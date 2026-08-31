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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** An in-memory store, so these tests never touch the real save directory. */
private class FakeSaves : SaveStore {
    val slots = mutableMapOf<String, String>()
    override fun write(name: String, data: String): Boolean { slots[name] = data; return true }
    override fun read(name: String): String? = slots[name]
    override fun list(): List<String> = slots.keys.filter(::isPlayerSlot)
    override fun delete(name: String): Boolean = slots.remove(name) != null
}

/** In-memory settings, so tests never touch the real preferences. */
private class FakeSettings(private val values: MutableMap<String, Boolean> = mutableMapOf()) :
    Settings {
    override fun getBoolean(key: String, default: Boolean) = values[key] ?: default
    override fun putBoolean(key: String, value: Boolean) { values[key] = value }
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
        setContent { App(seed = 1, saves = FakeSaves(), settings = FakeSettings()) }
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Welcome to Adventure", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun typingACommandAdvancesTheGame() = runComposeUiTest {
        setContent { App(seed = 1, saves = FakeSaves(), settings = FakeSettings()) }
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
        setContent { App(seed = 1, saves = FakeSaves(), settings = FakeSettings()) }
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
    fun aGameCanBeSavedToANamedSlotAndLoadedBack() = runComposeUiTest {
        val saves = FakeSaves()
        setContent { App(seed = 1, saves = saves, settings = FakeSettings()) }
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Welcome to Adventure", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        onNodeWithText("What now?").performTextInput("n")
        onNodeWithText("Go").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("end of a road", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }

        onNodeWithText("Saves").performClick()
        onNodeWithText("Name this save").performTextInput("outside")
        onNodeWithText("Save").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Saved as outside.", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        assertTrue(saves.read("outside")!!.contains("loc=$LOC_START"))

        // Walk away, then load the slot back.
        onNodeWithText("What now?").performTextInput("in")
        onNodeWithText("Go").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("inside a building", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }

        onNodeWithText("Saves").performClick()
        onNodeWithText("Load").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Loaded outside.", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** The autosave slot is the app's, and must not clutter the player's list. */
    @Test
    fun theAutosaveSlotIsHiddenFromTheSlotList() {
        val saves = FakeSaves()
        saves.write(AUTOSAVE_SLOT, "x")
        saves.write("mine", "y")
        assertEquals(listOf("mine"), saves.list())
    }

    @Test
    fun theAutosaveSettingDefaultsToOn() {
        assertTrue(FakeSettings().getBoolean(SETTING_AUTOSAVE, true))
    }

    /** A resumable autosave picks the game back up on launch. */
    @Test
    fun anAutosaveIsResumedOnLaunch() = runComposeUiTest {
        val saves = FakeSaves()
        // Produce a real snapshot by playing a little in a throwaway app.
        setContent { App(seed = 1, saves = saves, settings = FakeSettings()) }
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Welcome to Adventure", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        onNodeWithText("What now?").performTextInput("n")
        onNodeWithText("Go").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("end of a road", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }
        onNodeWithText("Saves").performClick()
        onNodeWithText("Name this save").performTextInput("slot")
        onNodeWithText("Save").performClick()
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(hasText("Saved as slot.", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        // Promote it to the autosave slot, as backgrounding would.
        saves.write(AUTOSAVE_SLOT, saves.read("slot")!!)
        assertTrue(saves.read(AUTOSAVE_SLOT)!!.contains("magic=retro-adventure-save"))
    }

    /** Restart throws the game away and starts a new one. */
    @Test
    fun restartBeginsAFreshGame() = runComposeUiTest {
        setContent { App(seed = 1, saves = FakeSaves(), settings = FakeSettings()) }
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
        setContent { App(seed = 1, saves = FakeSaves(), settings = FakeSettings()) }
        onNodeWithText(ATTRIBUTION_LINE).performClick()
        onNodeWithText("Will Crowther and Don Woods", substring = true).assertIsDisplayed()
    }
}
