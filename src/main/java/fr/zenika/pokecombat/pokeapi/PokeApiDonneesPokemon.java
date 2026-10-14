package fr.zenika.pokecombat.pokeapi;

import fr.zenika.pokecombat.combat.Attaque;
import fr.zenika.pokecombat.combat.DonneesPokemon;
import fr.zenika.pokecombat.combat.Pokemon;
import fr.zenika.pokecombat.combat.Type;
import fr.zenika.pokecombat.pokeapi.PokeApiClient.MoveDto;
import fr.zenika.pokecombat.pokeapi.PokeApiClient.PokemonDto;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/** L'adaptateur : traduit les réponses PokeAPI en fiches du métier. PokeAPI fournit les données, le métier applique les règles. */
@Component
public class PokeApiDonneesPokemon implements DonneesPokemon {

    private final PokeApiClient client;
    private final TableDesTypes tableDesTypes;
    private final Pokedex pokedex;

    public PokeApiDonneesPokemon(PokeApiClient client, TableDesTypes tableDesTypes, Pokedex pokedex) {
        this.client = client;
        this.tableDesTypes = tableDesTypes;
        this.pokedex = pokedex;
    }

    @Override
    public Pokemon pokemon(String nom) {
        PokemonDto fiche = client.pokemon(nomPokeApi(nom));
        List<Type> types = fiche.types().stream()
                .sorted(Comparator.comparingInt(PokemonDto.TypeSlot::slot))
                .map(slot -> TypesPokeApi.depuisPokeApi(slot.type().name()))
                .toList();
        String nomFr = pokedex.trouver(fiche.name()).map(Pokedex.EntreePokedex::nomFr).orElse(fiche.name());
        return new Pokemon(nomFr, types, stat(fiche, "hp"), stat(fiche, "speed"));
    }

    @Override
    public Attaque attaque(String nom) {
        MoveDto fiche = client.attaque(nom.toLowerCase(Locale.ROOT).trim());
        String nomFr = fiche.names().stream()
                .filter(n -> n.language().name().equals("fr"))
                .map(MoveDto.NomTraduit::name)
                .findFirst()
                .orElse(fiche.name());
        return new Attaque(nomFr, TypesPokeApi.depuisPokeApi(fiche.type().name()), fiche.power());
    }

    @Override
    public double efficacite(Type typeAttaque, Type typeDefense) {
        return tableDesTypes.multiplicateur(typeAttaque, typeDefense);
    }

    /** Les attaques qu'un Pokémon peut apprendre, pour l'interface (le métier n'en a pas besoin). */
    public List<AttaqueDisponible> attaquesDe(String nom) {
        return client.pokemon(nomPokeApi(nom)).moves().stream()
                .map(slot -> slot.move().name())
                .distinct()
                .map(attaque -> new AttaqueDisponible(attaque, pokedex.nomFrDeLAttaque(attaque)))
                .sorted(Comparator.comparing(AttaqueDisponible::nomFr))
                .toList();
    }

    private String nomPokeApi(String nom) {
        return pokedex.trouver(nom).map(Pokedex.EntreePokedex::nom).orElse(nom.toLowerCase(Locale.ROOT).trim());
    }

    private static int stat(PokemonDto fiche, String nom) {
        return fiche.stats().stream()
                .filter(s -> s.stat().name().equals(nom))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Stat absente : " + nom))
                .base_stat();
    }

    public record AttaqueDisponible(String nom, String nomFr) {
    }
}
