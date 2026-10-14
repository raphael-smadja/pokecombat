package fr.zenika.pokecombat.combat;

import static fr.zenika.pokecombat.combat.Type.ACIER;
import static fr.zenika.pokecombat.combat.Type.COMBAT;
import static fr.zenika.pokecombat.combat.Type.DRAGON;
import static fr.zenika.pokecombat.combat.Type.EAU;
import static fr.zenika.pokecombat.combat.Type.ELECTRIK;
import static fr.zenika.pokecombat.combat.Type.FEU;
import static fr.zenika.pokecombat.combat.Type.GLACE;
import static fr.zenika.pokecombat.combat.Type.INSECTE;
import static fr.zenika.pokecombat.combat.Type.NORMAL;
import static fr.zenika.pokecombat.combat.Type.PLANTE;
import static fr.zenika.pokecombat.combat.Type.POISON;
import static fr.zenika.pokecombat.combat.Type.PSY;
import static fr.zenika.pokecombat.combat.Type.ROCHE;
import static fr.zenika.pokecombat.combat.Type.SOL;
import static fr.zenika.pokecombat.combat.Type.SPECTRE;
import static fr.zenika.pokecombat.combat.Type.TENEBRES;
import static fr.zenika.pokecombat.combat.Type.VOL;

import java.util.List;
import java.util.Map;

/**
 * Fausses données figées : les fiches des exemples de référence.
 * On teste nos règles, jamais PokeAPI : aucun appel réseau dans les tests.
 */
public class FaussesDonneesPokemon implements DonneesPokemon {

    public static final Pokemon SALAMECHE = new Pokemon("Salamèche", List.of(FEU), 39, 65);
    public static final Pokemon BULBIZARRE = new Pokemon("Bulbizarre", List.of(PLANTE, POISON), 45, 45);
    public static final Pokemon PIKACHU = new Pokemon("Pikachu", List.of(ELECTRIK), 35, 90);
    public static final Pokemon LEVIATOR = new Pokemon("Léviator", List.of(EAU, VOL), 95, 81);
    public static final Pokemon RACAILLOU = new Pokemon("Racaillou", List.of(ROCHE, SOL), 40, 20);
    public static final Pokemon RATTATA = new Pokemon("Rattata", List.of(NORMAL), 30, 72);
    public static final Pokemon FANTOMINUS = new Pokemon("Fantominus", List.of(SPECTRE, POISON), 30, 80);
    public static final Pokemon DRACAUFEU = new Pokemon("Dracaufeu", List.of(FEU, VOL), 78, 100);

    public static final Attaque FLAMMECHE = new Attaque("Flammèche", FEU, 40);
    public static final Attaque FOUET_LIANES = new Attaque("Fouet Lianes", PLANTE, 45);
    public static final Attaque ECLAIR = new Attaque("Éclair", ELECTRIK, 40);
    public static final Attaque PISTOLET_A_O = new Attaque("Pistolet à O", EAU, 40);
    public static final Attaque JET_PIERRES = new Attaque("Jet-Pierres", ROCHE, 50);
    public static final Attaque CHARGE = new Attaque("Charge", NORMAL, 40);
    public static final Attaque LECHOUILLE = new Attaque("Léchouille", SPECTRE, 30);
    public static final Attaque CRU_AILES = new Attaque("Cru-Ailes", VOL, 60);
    public static final Attaque MIMI_QUEUE = new Attaque("Mimi-Queue", NORMAL, null);

    private static final Map<String, Pokemon> POKEMONS = Map.of(
            "charmander", SALAMECHE,
            "bulbasaur", BULBIZARRE,
            "pikachu", PIKACHU,
            "gyarados", LEVIATOR,
            "geodude", RACAILLOU,
            "rattata", RATTATA,
            "gastly", FANTOMINUS,
            "charizard", DRACAUFEU);

    private static final Map<String, Attaque> ATTAQUES = Map.of(
            "ember", FLAMMECHE,
            "vine-whip", FOUET_LIANES,
            "thunder-shock", ECLAIR,
            "water-gun", PISTOLET_A_O,
            "rock-throw", JET_PIERRES,
            "tackle", CHARGE,
            "lick", LECHOUILLE,
            "wing-attack", CRU_AILES,
            "tail-whip", MIMI_QUEUE);

    /** Extrait de la table des types (valeurs réelles du snapshot) : les lignes des attaques utilisées. */
    private static final Map<Type, Map<Type, Double>> TABLE_DES_TYPES = Map.of(
            FEU, Map.of(INSECTE, 2.0, DRAGON, 0.5, FEU, 0.5, PLANTE, 2.0, GLACE, 2.0, ROCHE, 0.5, ACIER, 2.0,
                    EAU, 0.5),
            PLANTE, Map.of(INSECTE, 0.5, DRAGON, 0.5, FEU, 0.5, VOL, 0.5, PLANTE, 0.5, SOL, 2.0, POISON, 0.5,
                    ROCHE, 2.0, ACIER, 0.5, EAU, 2.0),
            ELECTRIK, Map.of(DRAGON, 0.5, ELECTRIK, 0.5, VOL, 2.0, PLANTE, 0.5, SOL, 0.0, EAU, 2.0),
            EAU, Map.of(DRAGON, 0.5, FEU, 2.0, PLANTE, 0.5, SOL, 2.0, ROCHE, 2.0, EAU, 0.5),
            ROCHE, Map.of(INSECTE, 2.0, COMBAT, 0.5, FEU, 2.0, VOL, 2.0, SOL, 0.5, GLACE, 2.0, ACIER, 0.5),
            NORMAL, Map.of(SPECTRE, 0.0, ROCHE, 0.5, ACIER, 0.5),
            SPECTRE, Map.of(TENEBRES, 0.5, SPECTRE, 2.0, NORMAL, 0.0, PSY, 2.0),
            VOL, Map.of(INSECTE, 2.0, ELECTRIK, 0.5, COMBAT, 2.0, PLANTE, 2.0, ROCHE, 0.5, ACIER, 0.5));

    @Override
    public Pokemon pokemon(String nom) {
        return trouver(POKEMONS, nom);
    }

    @Override
    public Attaque attaque(String nom) {
        return trouver(ATTAQUES, nom);
    }

    @Override
    public double efficacite(Type typeAttaque, Type typeDefense) {
        return TABLE_DES_TYPES.getOrDefault(typeAttaque, Map.of()).getOrDefault(typeDefense, 1.0);
    }

    private static <T> T trouver(Map<String, T> fiches, String nom) {
        T fiche = fiches.get(nom);
        if (fiche == null) {
            throw new IllegalArgumentException("Absent des fausses données : " + nom);
        }
        return fiche;
    }
}
