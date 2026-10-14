package fr.zenika.pokecombat.pokeapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.zenika.pokecombat.pokeapi.Pokedex.EntreePokedex;
import java.util.List;
import org.junit.jupiter.api.Test;

class PokedexTest {

    private final Pokedex pokedex = new Pokedex(new ObjectMapper());

    @Test
    void rechercheSansAccentsTrouveSalameche() {
        List<EntreePokedex> resultats = pokedex.rechercher("sala");

        assertEquals(List.of("Salamèche"), resultats.stream().map(EntreePokedex::nomFr).toList());
    }

    @Test
    void trouveUnPokemonParSonNomFrancaisOuSonNomPokeApi() {
        assertEquals("gyarados", pokedex.trouver("Léviator").orElseThrow().nom());
        assertEquals("Léviator", pokedex.trouver("gyarados").orElseThrow().nomFr());
    }

    @Test
    void traduitLesAttaques() {
        assertEquals("Mimi-Queue", pokedex.nomFrDeLAttaque("tail-whip"));
    }
}
