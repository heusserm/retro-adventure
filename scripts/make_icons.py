#!/usr/bin/env python3
"""
Generate the app icon for iOS and Android.

The icon is drawn rather than drawn *on*: no source artwork to lose, and
re-running this after a design tweak regenerates every size consistently. That
matters more than it sounds -- EncounterDeck's icon set is the kind of thing
that rots into "which PNG is the real one".

Design: a lit brass lantern in the dark. The lantern is the first useful object
in the game and the thing that makes the cave playable at all, so it reads as
Adventure to anyone who has played it, and as "something in a dark place" to
anyone who has not. It is built from four or five solid shapes because an icon
is often 40 pixels wide, where anything finer turns to mush.

iOS icons must have no alpha channel -- App Store Connect rejects transparency
-- so the iOS image is composited onto an opaque background. Android's adaptive
icon wants the opposite: a transparent foreground layer, kept inside the safe
zone, over a separate background color.

Usage:  python3 scripts/make_icons.py
"""

import os
from PIL import Image, ImageDraw, ImageFilter

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
IOS_ICON = os.path.join(ROOT, "iosApp/iosApp/Assets.xcassets/AppIcon.appiconset")
ANDROID_RES = os.path.join(ROOT, "app/src/androidMain/res")

S = 1024  # master size; everything is scaled from this

BACKDROP = (18, 18, 26)
BACKDROP_EDGE = (10, 10, 16)
GLOW = (255, 176, 62)
BRASS = (214, 158, 68)
BRASS_DARK = (150, 104, 40)
FLAME = (255, 236, 178)


def backdrop() -> Image.Image:
    """Dark ground with a soft vignette, so the glow has something to sit in."""
    img = Image.new("RGB", (S, S), BACKDROP)
    d = ImageDraw.Draw(img)
    steps = 90
    for i in range(steps):
        t = i / steps
        r = int(S * 0.78 * (1 - t)) + int(S * 0.10)
        color = tuple(
            int(BACKDROP_EDGE[c] + (BACKDROP[c] - BACKDROP_EDGE[c]) * (1 - t)) for c in range(3)
        )
        d.ellipse([S / 2 - r, S / 2 - r, S / 2 + r, S / 2 + r], fill=color)
    return img


def glow_layer() -> Image.Image:
    """The lamplight. Drawn big and blurred, then composited additively."""
    g = Image.new("RGB", (S, S), (0, 0, 0))
    d = ImageDraw.Draw(g)
    for i in range(40):
        t = i / 40
        r = int(S * 0.42 * (1 - t) + S * 0.06)
        v = int(150 * (t ** 1.6))
        d.ellipse(
            [S / 2 - r, S * 0.52 - r, S / 2 + r, S * 0.52 + r],
            fill=(int(GLOW[0] * v / 255), int(GLOW[1] * v / 255), int(GLOW[2] * v / 255)),
        )
    return g.filter(ImageFilter.GaussianBlur(S * 0.05))


def lantern(draw: ImageDraw.ImageDraw) -> None:
    """Four solid shapes: handle, cap, body, base. Nothing finer than ~3% of width."""
    cx, cy = S / 2, S * 0.54
    w = S * 0.30          # body half-width at the base
    body_top = cy - S * 0.16
    body_bot = cy + S * 0.17

    # Handle: an arc thick enough to survive downscaling.
    draw.arc(
        [cx - w * 0.72, body_top - S * 0.20, cx + w * 0.72, body_top + S * 0.06],
        start=200, end=340, fill=BRASS, width=int(S * 0.035),
    )

    # Cap.
    draw.polygon(
        [(cx - w * 0.86, body_top), (cx + w * 0.86, body_top),
         (cx + w * 0.52, body_top - S * 0.075), (cx - w * 0.52, body_top - S * 0.075)],
        fill=BRASS,
    )

    # Glass housing, lit from inside.
    draw.polygon(
        [(cx - w * 0.66, body_top), (cx + w * 0.66, body_top),
         (cx + w * 0.80, body_bot), (cx - w * 0.80, body_bot)],
        fill=FLAME,
    )
    # Brass uprights framing the glass.
    up = int(S * 0.030)
    draw.line([(cx - w * 0.66, body_top), (cx - w * 0.80, body_bot)], fill=BRASS, width=up)
    draw.line([(cx + w * 0.66, body_top), (cx + w * 0.80, body_bot)], fill=BRASS, width=up)

    # Base.
    draw.polygon(
        [(cx - w * 0.92, body_bot), (cx + w * 0.92, body_bot),
         (cx + w * 0.74, body_bot + S * 0.075), (cx - w * 0.74, body_bot + S * 0.075)],
        fill=BRASS_DARK,
    )


def master(transparent: bool) -> Image.Image:
    if transparent:
        img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)
        lantern(d)
        return img

    img = backdrop()
    img = Image.blend(img, Image.new("RGB", (S, S), (0, 0, 0)), 0.0)
    glow = glow_layer()
    img = Image.eval(img, lambda v: v)  # keep mode RGB
    img = Image.blend(img, glow, 0.0)
    # Additive composite of the glow, clipped.
    px_i, px_g = img.load(), glow.load()
    for y in range(0, S):
        for x in range(0, S):
            r, g, b = px_i[x, y]
            gr, gg, gb = px_g[x, y]
            px_i[x, y] = (min(255, r + gr), min(255, g + gg), min(255, b + gb))
    d = ImageDraw.Draw(img)
    lantern(d)
    return img


def write_ios(icon: Image.Image) -> None:
    os.makedirs(IOS_ICON, exist_ok=True)
    icon.convert("RGB").save(os.path.join(IOS_ICON, "Icon-1024.png"))
    with open(os.path.join(IOS_ICON, "Contents.json"), "w", encoding="utf-8") as f:
        f.write(
            '{\n  "images" : [\n    {\n      "filename" : "Icon-1024.png",\n'
            '      "idiom" : "universal",\n      "platform" : "ios",\n'
            '      "size" : "1024x1024"\n    }\n  ],\n'
            '  "info" : { "author" : "xcode", "version" : 1 }\n}\n'
        )
    print("wrote iOS icon (1024, opaque)")


ANDROID_DENSITIES = {
    "mipmap-mdpi": 48, "mipmap-hdpi": 72, "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144, "mipmap-xxxhdpi": 192,
}


def write_android(icon: Image.Image, foreground: Image.Image) -> None:
    for folder, size in ANDROID_DENSITIES.items():
        d = os.path.join(ANDROID_RES, folder)
        os.makedirs(d, exist_ok=True)
        icon.convert("RGB").resize((size, size), Image.LANCZOS).save(
            os.path.join(d, "ic_launcher.png")
        )
        # Adaptive foreground: the lantern at 66% inside a transparent square,
        # because Android crops the outer third to whatever shape the launcher
        # feels like using.
        fg = Image.new("RGBA", (S, S), (0, 0, 0, 0))
        scaled = foreground.resize((int(S * 0.66), int(S * 0.66)), Image.LANCZOS)
        fg.paste(scaled, (int(S * 0.17), int(S * 0.17)), scaled)
        fg.resize((size * 2, size * 2), Image.LANCZOS).save(
            os.path.join(d, "ic_launcher_foreground.png")
        )

    anydpi = os.path.join(ANDROID_RES, "mipmap-anydpi-v26")
    os.makedirs(anydpi, exist_ok=True)
    for name in ("ic_launcher", "ic_launcher_round"):
        with open(os.path.join(anydpi, f"{name}.xml"), "w", encoding="utf-8") as f:
            f.write(
                '<?xml version="1.0" encoding="utf-8"?>\n'
                '<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">\n'
                '    <background android:drawable="@color/icon_background" />\n'
                '    <foreground android:drawable="@mipmap/ic_launcher_foreground" />\n'
                "</adaptive-icon>\n"
            )
    values = os.path.join(ANDROID_RES, "values")
    os.makedirs(values, exist_ok=True)
    colors = os.path.join(values, "colors.xml")
    existing = open(colors, encoding="utf-8").read() if os.path.exists(colors) else None
    if existing and "icon_background" not in existing:
        existing = existing.replace(
            "</resources>", '    <color name="icon_background">#12121A</color>\n</resources>'
        )
        open(colors, "w", encoding="utf-8").write(existing)
    print("wrote Android icons (%s densities + adaptive)" % len(ANDROID_DENSITIES))


if __name__ == "__main__":
    opaque = master(transparent=False)
    fg = master(transparent=True)
    write_ios(opaque)
    write_android(opaque, fg)
