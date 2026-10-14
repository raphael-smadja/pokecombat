# Pokécombat

Simulateur de duel Pokémon qui sert de terrain de jeu au talk
**« Attrapez-les tous : chassez les bugs avec le mutation testing et un reviewer IA »**.

Des tests verts avec une couverture proche de 100 % peuvent laisser passer des bugs. PIT le prouve en injectant
des mutants dans le code ; un reviewer IA explique chaque mutant enfui et propose le test qui le capture.

> Chaque mutant est un Pokémon sauvage, chaque test est une Poké Ball.
> Un mutant tué par un test est capturé ; un mutant qui survit s'est enfui.

## Lancer l'appli

```bash
./mvnw spring-boot:run
```

Puis http://localhost:8080 : choisir deux Pokémon et une attaque chacun, cliquer sur « Combattre ! ».
PokeAPI fournit les données (mises en cache), notre code applique les règles.

## Les commandes du talk

| Commande | Ce qu'elle montre |
|---|---|
| `./mvnw test` | Tests verts, couverture JaCoCo dans `target/site/jacoco/index.html` |
| `./mvnw test-compile pitest:mutationCoverage` | Mutation testing, rapport dans `target/pit-reports/index.html` |
| `scripts/bug-du-public.sh liste` | Les bugs prêts à pousser en PR pendant l'étape « le public attaque » |
| `python3 scripts/generer_snapshots.py` | Régénère les snapshots PokeAPI (table des types, noms français) |

## Architecture

| Package | Contenu |
|---|---|
| `fr.zenika.pokecombat.combat` | Le métier, en Java pur : `Efficacite`, `Degats`, `Duel`, l'interface `DonneesPokemon`. Seul package muté par PIT. |
| `fr.zenika.pokecombat.pokeapi` | L'adaptateur PokeAPI : `PokeApiDonneesPokemon` (RestClient + `@Cacheable`), snapshot `table-des-types.json` |
| `fr.zenika.pokecombat.web.rest` | `CombatResource` (`POST /api/combats`), `PokemonResource` (`GET /api/pokemons?recherche=sala`) |
| tests `combat` | `FaussesDonneesPokemon` : les fiches des exemples de référence, aucun appel réseau |

Deux choix de conception :
- la table des types est figée dans un snapshot JSON versionné, généré une fois depuis PokeAPI : le métier reste
  déterministe et ne dépend pas du réseau ;
- le métier ne connaît PokeAPI qu'à travers `DonneesPokemon` : dans les tests, on teste nos règles, jamais l'API.

## Les règles

1. **Efficacité** : un type d'attaque contre un type de défense donne ×0, ×½, ×1 ou ×2.
2. **Double type** : on multiplie les deux multiplicateurs (0, ¼, ½, 1, 2, 4), et le message en dépend.
3. **Bonus de type** : ×1,5 si l'attaque est du type de l'attaquant (l'un de ses deux types suffit).
4. **Dégâts** : puissance × bonus × multiplicateur, arrondi à l'entier inférieur. Attaque de statut (puissance
   `null` dans PokeAPI) ou immunité : 0.
5. **Duel** : PV × 3 ; le plus rapide commence (égalité : la gauche) ; K.O. à 0 PV ; match nul après 50 tours.

## La Ligue (CI)

Chaque PR passe 4 badges dans GitHub Actions (`.github/workflows/ligue.yml`) :
**Build** → **Tests** → **Mutation** (PIT, rouge sous 80 %) → **Review IA** (claude-code-action et le skill
`.claude/skills/revue-pokecombat`). Une branch protection sur `main` les exige tous pour merger.

Par défaut, l'IA conseille et PIT bloque. Variable de repo `REVUE_BLOQUANTE=true` : la review devient bloquante.

## Les étapes (tags git)

| Tag | Contenu |
|---|---|
| `etape-0` | Le fichier JDL |
| `etape-1` | L'appli, PokeAPI branchée, les 5 niveaux, la suite piégée (98 % de lignes, 50 % de mutants capturés) |
| `etape-2` | La Ligue : Build, Tests, Mutation, et le skill de revue sur `main` |
| `etape-3` | Le job Review IA |
| `etape-4` | Les 3 Poké Balls du live : 86 % de mutants capturés, badge Mutation vert |
| `etape-5` | Toutes les Poké Balls : 100 % |
