package com.xndev.retroadventure.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

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
