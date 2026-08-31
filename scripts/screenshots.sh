#!/bin/bash
# Capture App Store screenshots from a booted simulator.
#
# !! THIS TAKES OVER THE MOUSE AND KEYBOARD. Start it and leave the machine
# alone until it finishes -- roughly two minutes per device. There is no way to
# type into the Simulator without driving the real input devices, so anything
# you click or type lands in the middle of the run and corrupts it. A stray
# keystroke turned "take rod" into "onacetake rod" once, which looks exactly
# like a timing bug and is not one.
#
# Screenshots come from `simctl io`, never `screencapture`: simctl grabs the
# device framebuffer at its true pixel size, which is what App Store Connect
# checks, and it cannot accidentally capture the rest of the desktop.
#
# One launch per screenshot. That looks wasteful and is not: typing, blurring to
# hide the keyboard, and re-focusing inside a single run means several focus
# events, and keystrokes that land during a focus animation go missing. One
# focus event per run makes it deterministic.
#
# Every shot blurs the field first, so no keyboard -- and on iPad no QuickType
# suggestion bar -- is ever in frame.
#
# Usage: screenshots.sh <udid> <dev_w> <dev_h> <outdir> <prefix> [ix iy sx sy ax ay]
#
# The six optional fractions locate the input field, the Saves button and the
# attribution line. They differ between phone and tablet because the layout
# reflows, and they must be MEASURED off a capture, not guessed: the mapping
# from window to device screen carries a small offset that big targets absorb
# and small ones do not. A tap a few points below the attribution silently does
# nothing, and you get a screenshot of the game with no dialog on it.
set -euo pipefail

UDID="$1"; DEV_W="$2"; DEV_H="$3"; OUTDIR="$4"; PREFIX="$5"
F_INPUT_X="${6:-0.42}";  F_INPUT_Y="${7:-0.820}"
F_SAVES_X="${8:-0.10}";  F_SAVES_Y="${9:-0.868}"
F_ABOUT_X="${10:-0.46}"; F_ABOUT_Y="${11:-0.9215}"
F_BLUR_X=0.5; F_BLUR_Y=0.35     # empty transcript; tapping it dismisses the keyboard

BUNDLE=com.xndev.retroAdventure
mkdir -p "$OUTDIR"

osascript -e 'tell application "Simulator" to activate' >/dev/null
sleep 1

# Hide the Dock for the duration, and put it back on the way out.
#
# The Simulator window does not fit above the Dock at any of its zoom levels, so
# the Dock sits on top of the bottom of the device and a tap aimed at a control
# down there lands on the Dock instead, silently doing nothing. That is what made
# the Saves and About buttons look unclickable while the text field worked fine
# -- a whole afternoon of "the tap coordinates must be wrong".
DOCK_WAS=$(osascript -e 'tell application "System Events" to get autohide of dock preferences')
restore_dock() {
  osascript -e "tell application \"System Events\" to set autohide of dock preferences to $DOCK_WAS" >/dev/null || true
}
trap restore_dock EXIT
osascript -e 'tell application "System Events" to set autohide of dock preferences to true' >/dev/null
sleep 1

# Normalize the zoom so the measured fractions mean the same thing every run.
osascript -e 'tell application "System Events" to keystroke "4" using command down' >/dev/null
sleep 1.5
osascript -e 'tell application "System Events" to tell process "Simulator" to set position of window 1 to {498, 40}' >/dev/null || true
sleep 1

GEOM=$(osascript -e 'tell application "System Events" to tell process "Simulator" to get {position, size} of window 1' | tr -d ' ')
RECT=$(python3 -c '
import sys
x, y, w, h = [int(v) for v in sys.argv[1].split(",")]
dev_w, dev_h = int(sys.argv[2]), int(sys.argv[3])
TITLE = 28                      # Simulator title bar
sh = h - TITLE
sw = sh * dev_w / dev_h         # the screen keeps the device aspect ratio
print(int(x + (w - sw) / 2), int(y + TITLE), int(sw), int(sh))
' "$GEOM" "$DEV_W" "$DEV_H")
read -r SX SY SW SH <<< "$RECT"
echo "screen rect: $SX $SY $SW $SH"

# Every tap carries CAL_DY, a vertical correction measured at the start of the
# run. The mapping from Simulator window to device screen is off by a handful of
# points -- enough that a text field still gets hit and a one-line button does
# not -- and the amount changes whenever the window is resized or re-zoomed.
# Guessing it by hand wasted hours; measuring it takes ten taps.
CAL_DY=0

tap() {
  cliclick "c:$(python3 -c "print(int($SX + $1 * $SW))"),$(python3 -c "print(int($SY + $2 * $SH + $CAL_DY))")"
  sleep 0.7
}

field_is_focused() {  # a focused OutlinedTextField draws a purple border
  xcrun simctl io "$UDID" screenshot /tmp/ra_cal.png >/dev/null 2>&1
  python3 -c "
from PIL import Image
im = Image.open('/tmp/ra_cal.png').convert('RGB'); w, h = im.size
lo, hi = int(h * ($F_INPUT_Y - 0.03)), int(h * ($F_INPUT_Y + 0.03))
n = 0
for y in range(lo, hi, 2):
    for x in range(0, w, 4):
        r, g, b = im.getpixel((x, y))
        if b > 140 and r < 130 and g < 110:
            n += 1
print(1 if n > 400 else 0)"
}

calibrate() {
  xcrun simctl terminate "$UDID" "$BUNDLE" 2>/dev/null || true
  sleep 1
  xcrun simctl launch "$UDID" "$BUNDLE" >/dev/null
  sleep 6
  for D in 0 -8 -16 8 -24 16 -32 24 -40 32 -48 40; do
    CAL_DY=$D
    tap $F_INPUT_X $F_INPUT_Y
    sleep 1
    if [ "$(field_is_focused)" = "1" ]; then
      echo "calibrated: dy=$D"
      return 0
    fi
  done
  CAL_DY=0
  echo "WARNING: could not focus the input field; shots will be wrong"
  return 1
}

type_cmd() {
  osascript -e "tell application \"System Events\" to keystroke \"$1\"" >/dev/null
  sleep 0.7
  osascript -e 'tell application "System Events" to key code 36' >/dev/null
  sleep 2.0
}

fresh_game() {
  xcrun simctl terminate "$UDID" "$BUNDLE" 2>/dev/null || true
  sleep 1
  xcrun simctl launch "$UDID" "$BUNDLE" >/dev/null
  sleep 4
  tap $F_INPUT_X $F_INPUT_Y
  sleep 1
}

grab() {
  tap $F_BLUR_X $F_BLUR_Y
  sleep 1.2
  xcrun simctl io "$UDID" screenshot "$OUTDIR/${PREFIX}_$1.png" >/dev/null 2>&1
  echo "  captured $1"
}

# A clean, consistent status bar. Apple's own marketing time.
xcrun simctl status_bar "$UDID" override \
  --time "9:41" --batteryState charged --batteryLevel 100 \
  --cellularBars 4 --wifiBars 3

calibrate

fresh_game; type_cmd "n"; grab "1-road"

fresh_game; type_cmd "n"; type_cmd "in"; grab "2-building"

fresh_game
type_cmd "n"; type_cmd "in"; type_cmd "take lamp"; type_cmd "xyzzy"; type_cmd "on"
grab "3-cave"

fresh_game
type_cmd "n"; type_cmd "in"; type_cmd "take lamp"; type_cmd "xyzzy"; type_cmd "on"
type_cmd "take rod"; type_cmd "inventory"
grab "4-inventory"

fresh_game
type_cmd "n"; type_cmd "in"; type_cmd "take lamp"
tap $F_BLUR_X $F_BLUR_Y; sleep 1.2
tap $F_SAVES_X $F_SAVES_Y; sleep 2
xcrun simctl io "$UDID" screenshot "$OUTDIR/${PREFIX}_5-saves.png" >/dev/null 2>&1
echo "  captured 5-saves"

fresh_game
type_cmd "n"; type_cmd "in"
tap $F_BLUR_X $F_BLUR_Y; sleep 1.2
tap $F_ABOUT_X $F_ABOUT_Y; sleep 2
xcrun simctl io "$UDID" screenshot "$OUTDIR/${PREFIX}_6-about.png" >/dev/null 2>&1
echo "  captured 6-about"

echo "done: $OUTDIR"
