#!/usr/bin/env python3
"""Génère les snapshots PokeAPI versionnés dans src/main/resources/pokeapi.

À lancer une seule fois (ou pour rafraîchir les données) :

    python3 scripts/generer_snapshots.py

- table-des-types.json : les efficacités entre types (/api/v2/type/{nom}, damage_relations).
  Le métier ne dépend jamais du réseau pour ces règles : elles sont figées ici.
- pokemons.json        : les 151 Pokémon de la 1re génération (nom API, nom français, types).
  Sert à la recherche « sala » -> Salamèche dans l'interface.
- attaques-fr.json     : nom API -> nom français de chaque attaque, pour l'affichage.
"""
import json
import sys
import urllib.request
from concurrent.futures import ThreadPoolExecutor
from datetime import date
from pathlib import Path

API = "https://pokeapi.co/api/v2"
SORTIE = Path(__file__).resolve().parent.parent / "src" / "main" / "resources" / "pokeapi"
TYPES = [
    "normal", "fire", "water", "grass", "electric", "ice", "fighting", "poison", "ground",
    "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy",
]
NB_POKEMONS = 151


def lire(url):
    requete = urllib.request.Request(url, headers={"User-Agent": "pokecombat-snapshot"})
    with urllib.request.urlopen(requete, timeout=30) as reponse:
        return json.load(reponse)


def nom_francais(noms, defaut):
    return next((n["name"] for n in noms if n["language"]["name"] == "fr"), defaut)


def table_des_types():
    efficacites = {}
    for nom in TYPES:
        relations = lire(f"{API}/type/{nom}")["damage_relations"]
        ligne = {}
        for cle, valeur in (("no_damage_to", 0), ("half_damage_to", 0.5), ("double_damage_to", 2)):
            for cible in relations[cle]:
                if cible["name"] in TYPES:
                    ligne[cible["name"]] = valeur
        efficacites[nom] = dict(sorted(ligne.items()))
    return {
        "source": f"{API}/type/{{nom}} (damage_relations)",
        "generee_le": date.today().isoformat(),
        "commentaire": "Multiplicateur d'un type d'attaque (clé) contre un type de défense. Absent = x1.",
        "efficacites": efficacites,
    }


def pokemon(numero):
    fiche = lire(f"{API}/pokemon/{numero}")
    espece = lire(f"{API}/pokemon-species/{numero}")
    return {
        "id": numero,
        "nom": fiche["name"],
        "nomFr": nom_francais(espece["names"], fiche["name"]),
        "types": [t["type"]["name"] for t in sorted(fiche["types"], key=lambda t: t["slot"])],
    }


def attaque(url):
    fiche = lire(url)
    return fiche["name"], nom_francais(fiche["names"], fiche["name"])


def main():
    SORTIE.mkdir(parents=True, exist_ok=True)

    print("Table des types…")
    ecrire("table-des-types.json", table_des_types())

    print(f"{NB_POKEMONS} Pokémon…")
    with ThreadPoolExecutor(max_workers=8) as pool:
        pokemons = list(pool.map(pokemon, range(1, NB_POKEMONS + 1)))
    ecrire("pokemons.json", pokemons)

    print("Noms français des attaques…")
    liste = lire(f"{API}/move?limit=2000")["results"]
    with ThreadPoolExecutor(max_workers=8) as pool:
        noms = dict(pool.map(attaque, [a["url"] for a in liste]))
    ecrire("attaques-fr.json", dict(sorted(noms.items())))


def ecrire(fichier, contenu):
    chemin = SORTIE / fichier
    chemin.write_text(json.dumps(contenu, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"  -> {chemin.relative_to(SORTIE.parent.parent.parent.parent)}")


if __name__ == "__main__":
    sys.exit(main())
