package com.xndev.retroadventure.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TypographyTest {

    @Test
    fun aPhoneKeepsTheCompactSize() {
        assertEquals(13, transcriptFontSize(390f))   // iPhone 15 Pro
        assertEquals(13, transcriptFontSize(414f))   // 6.5" phone
    }

    @Test
    fun aTabletGetsALargerSize() {
        assertEquals(20, transcriptFontSize(1024f))  // 12.9" iPad
        assertEquals(20, transcriptFontSize(1032f))  // 13" iPad
        assertTrue(transcriptFontSize(1024f) > transcriptFontSize(414f))
    }

    @Test
    fun theColumnStopsGrowingBeforeItBecomesUnreadable() {
        // A phone uses everything it has; a tablet does not.
        assertEquals(414f, readingWidthDp(414f))
        assertEquals(760f, readingWidthDp(1032f))
    }
}
