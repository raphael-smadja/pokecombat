package fr.zenika.pokecombat.pokeapi;

import fr.zenika.pokecombat.combat.Type;
import java.util.Map;

/** Traduction entre les noms de types de PokeAPI (anglais) et les types du métier. */
public final class TypesPokeApi {

    private static final Map<String, Type> TYPES = Map.ofEntries(
            Map.entry("normal", Type.NORMAL),
            Map.entry("fire", Type.FEU),
            Map.entry("water", Type.EAU),
            Map.entry("grass", Type.PLANTE),
            Map.entry("electric", Type.ELECTRIK),
            Map.entry("ice", Type.GLACE),
            Map.entry("fighting", Type.COMBAT),
            Map.entry("poison", Type.POISON),
            Map.entry("ground", Type.SOL),
            Map.entry("flying", Type.VOL),
            Map.entry("psychic", Type.PSY),
            Map.entry("bug", Type.INSECTE),
            Map.entry("rock", Type.ROCHE),
            Map.entry("ghost", Type.SPECTRE),
            Map.entry("dragon", Type.DRAGON),
            Map.entry("dark", Type.TENEBRES),
            Map.entry("steel", Type.ACIER),
            Map.entry("fairy", Type.FEE));

    private TypesPokeApi() {
    }

    public static Type depuisPokeApi(String nom) {
        Type type = TYPES.get(nom);
        if (type == null) {
            throw new IllegalArgumentException("Type PokeAPI inconnu : " + nom);
        }
        return type;
    }
}
