#!/usr/bin/env python3
"""
Report cyclomatic complexity, coverage and CRAP per method.

CRAP (Change Risk Anti-Patterns) is Alberg and Savoia's metric:

    CRAP(m) = comp(m)^2 * (1 - cov(m))^3 + comp(m)

It says the two things that make a method dangerous to change -- being
complicated and being untested -- are worse together than either alone. A
simple method scores low however badly covered it is; a hairy one scores low
only if it is well covered. The conventional danger line is 30.

That framing suits this project. The engine is a port, so almost every method
is a transliteration of C that has to behave identically; the risk is not that
the code is ugly, it is that a branch nothing exercises is quietly wrong. CRAP
finds exactly those.

Usage:  ./gradlew koverXmlReport && python3 scripts/crap.py [--threshold 30]

Reads Kover's JaCoCo-format XML. Note Kover does not emit JaCoCo's COMPLEXITY
counter, so complexity is derived from the branch counts the same way JaCoCo
derives it: one plus half the branches, which is the number of independent
paths through the method.
"""

import argparse
import glob
import os
import sys
import xml.etree.ElementTree as ET

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def counters(node):
    out = {}
    for c in node.findall("counter"):
        out[c.get("type")] = (int(c.get("missed")), int(c.get("covered")))
    return out


def method_stats(report_path):
    """Yield (class, method, complexity, coverage, crap) for every method."""
    tree = ET.parse(report_path)
    for package in tree.getroot().iter("package"):
        for cls in package.iter("class"):
            clsname = cls.get("name", "").replace("/", ".")
            for method in cls.findall("method"):
                c = counters(method)
                bmissed, bcovered = c.get("BRANCH", (0, 0))
                branches = bmissed + bcovered
                complexity = 1 + branches // 2

                lmissed, lcovered = c.get("LINE", (0, 0))
                lines = lmissed + lcovered
                if lines == 0:
                    continue
                cov = lcovered / lines

                crap = complexity**2 * (1 - cov) ** 3 + complexity
                yield clsname, method.get("name"), complexity, cov, crap, lines


def totals(report_path):
    tree = ET.parse(report_path)
    c = counters(tree.getroot())
    lmissed, lcovered = c.get("LINE", (0, 0))
    bmissed, bcovered = c.get("BRANCH", (0, 0))
    return lmissed, lcovered, bmissed, bcovered


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--threshold", type=float, default=30.0)
    ap.add_argument("--top", type=int, default=15)
    args = ap.parse_args()

    reports = sorted(glob.glob(os.path.join(ROOT, "*", "build", "reports", "kover", "report.xml")))
    if not reports:
        sys.exit("no Kover reports; run ./gradlew koverXmlReport first")

    all_methods = []
    print("=" * 78)
    for path in reports:
        module = os.path.relpath(path, ROOT).split(os.sep)[0]
        lmissed, lcovered, bmissed, bcovered = totals(path)
        lines = lmissed + lcovered
        branches = bmissed + bcovered
        lcov = 100 * lcovered / lines if lines else 0.0
        bcov = 100 * bcovered / branches if branches else 0.0
        print(
            f"{module:10s} line {lcov:5.1f}% ({lcovered}/{lines})"
            f"   branch {bcov:5.1f}% ({bcovered}/{branches})"
        )
        all_methods.extend(list(method_stats(path)))
    print("=" * 78)

    over = [m for m in all_methods if m[4] > args.threshold]
    all_methods.sort(key=lambda m: m[4], reverse=True)

    print(
        f"{len(all_methods)} methods, {len(over)} above the CRAP threshold "
        f"of {args.threshold:.0f}"
    )
    print()
    print(f"{'CRAP':>7}  {'cx':>3}  {'cov':>6}  {'lines':>5}  method")
    for clsname, name, cx, cov, crap, lines in all_methods[: args.top]:
        short = clsname.rsplit(".", 1)[-1]
        print(f"{crap:7.1f}  {cx:3d}  {100 * cov:5.1f}%  {lines:5d}  {short}.{name}")

    print()
    print(
        "CRAP over 30 means complicated AND under-tested. On a port, that is "
        "where a\nmistranslated branch hides -- the transcripts cannot catch "
        "what they never run."
    )


if __name__ == "__main__":
    main()
