#!/usr/bin/env python3
"""Résumé Markdown du rapport PIT, affiché dans la page du run GitHub Actions.

    python3 scripts/resume_pit.py >> "$GITHUB_STEP_SUMMARY"
"""
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

RAPPORT = Path("target/pit-reports/mutations.xml")
CAPTURES = {"KILLED", "TIMED_OUT", "MEMORY_ERROR", "RUN_ERROR"}


def main():
    if not RAPPORT.exists():
        print("## Mutation testing\n\nAucun rapport PIT : la compilation ou les tests ont échoué avant.")
        return 0
    mutants = list(ET.parse(RAPPORT).getroot())
    captures = [m for m in mutants if m.get("status") in CAPTURES]
    enfuis = [m for m in mutants if m.get("status") not in CAPTURES]
    score = round(100 * len(captures) / len(mutants)) if mutants else 100

    print("## Mutation testing : le Pokédex des mutants\n")
    print(f"**{len(captures)} mutants capturés sur {len(mutants)} ({score} %)**, seuil : 80 %.\n")
    if enfuis:
        print("| Mutant enfui | Ligne | Mutation |")
        print("|---|---|---|")
        for m in sorted(enfuis, key=lambda m: (m.findtext("sourceFile"), int(m.findtext("lineNumber")))):
            print(f"| {m.findtext('sourceFile')} `{m.findtext('mutatedMethod')}` | {m.findtext('lineNumber')} "
                  f"| {m.findtext('description')} ({m.get('status')}) |")
    else:
        print("Attrapez-les tous : aucun mutant ne s'est enfui.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
