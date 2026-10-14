#!/usr/bin/env bash
# Étape 8 du talk : pousse un bug proposé par la salle dans une PR, et regarde qui l'attrape.
#
#   scripts/bug-du-public.sh liste          # les bugs préparés
#   scripts/bug-du-public.sh arrondi        # injecte un bug préparé, pousse, ouvre la PR
#   scripts/bug-du-public.sh libre "titre"  # pousse tes modifications en cours (bug inventé par la salle)
set -euo pipefail

cd "$(git rev-parse --show-toplevel)"
COMBAT=src/main/java/fr/zenika/pokecombat/combat

remplacer() { # fichier, avant, après
    python3 - "$1" "$2" "$3" <<'EOF'
import sys
fichier, avant, apres = sys.argv[1:]
texte = open(fichier, encoding="utf-8").read()
if avant not in texte:
    sys.exit(f"Introuvable dans {fichier} : {avant}")
open(fichier, "w", encoding="utf-8").write(texte.replace(avant, apres, 1))
EOF
}

injecter() {
    case "$1" in
        arrondi)       remplacer $COMBAT/Degats.java "Math.floor(degats)" "Math.round(degats)" ;;
        premier-type)  remplacer $COMBAT/Degats.java "attaquant.types().contains(attaque.type())" "attaquant.types().get(0) == attaque.type()" ;;
        tours-49)      remplacer $COMBAT/Duel.java "TOURS_MAXIMUM = 50" "TOURS_MAXIMUM = 49" ;;
        vitesse-egale) remplacer $COMBAT/Duel.java "droite.pokemon().vitesse() > gauche.pokemon().vitesse()" "droite.pokemon().vitesse() >= gauche.pokemon().vitesse()" ;;
        pv-x2)         remplacer $COMBAT/Duel.java "MULTIPLICATEUR_PV = 3" "MULTIPLICATEUR_PV = 2" ;;
        pv-negatifs)   remplacer $COMBAT/Duel.java "pv = Math.max(0, pv - degats);" "pv = pv - degats;" ;;
        *) echo "Bug inconnu : $1 (voir : $0 liste)"; exit 1 ;;
    esac
}

case "${1:-liste}" in
    liste)
        cat <<'EOF'
arrondi        Degats : arrondi au plus proche au lieu de l'entier inférieur
premier-type   Degats : le bonus de type ne regarde que le premier type de l'attaquant
tours-49       Duel   : match nul après 49 tours au lieu de 50
vitesse-egale  Duel   : en cas d'égalité de vitesse, la droite commence
pv-x2          Duel   : PV de combat = PV de base × 2 au lieu de × 3
pv-negatifs    Duel   : les PV peuvent descendre sous 0
libre "titre"  pousse tes propres modifications
EOF
        exit 0 ;;
    libre)
        titre="${2:?Donne un titre : $0 libre \"titre du bug\"}"
        slug="libre-$(date +%H%M%S)" ;;
    *)
        titre="Bug du public : $1"
        slug="$1" ;;
esac

if [ "$1" != libre ] && ! git diff --quiet; then
    echo "Des modifications sont en cours : commit ou stash d'abord (sinon elles partiraient dans la PR)."
    exit 1
fi

git fetch -q origin main
branche="bug/$slug"
if [ "$1" = libre ]; then
    git stash push -q -m bug-du-public
    git switch -q -c "$branche" origin/main
    git stash pop -q
else
    git switch -q -c "$branche" origin/main
    injecter "$1"
fi

git commit -qam "$titre"
git push -q -u origin "$branche"
gh pr create --base main --head "$branche" --title "$titre" \
    --body "Bug proposé par la salle. Qui l'attrape : les tests, PIT, la review IA… ou personne ?"
git switch -q -
