package fr.zenika.pokecombat.web.rest;

import fr.zenika.pokecombat.pokeapi.PokeApiDonneesPokemon;
import fr.zenika.pokecombat.pokeapi.PokeApiDonneesPokemon.AttaqueDisponible;
import fr.zenika.pokecombat.pokeapi.Pokedex;
import fr.zenika.pokecombat.pokeapi.Pokedex.EntreePokedex;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pokemons")
public class PokemonResource {

    private final Pokedex pokedex;
    private final PokeApiDonneesPokemon pokeApi;

    public PokemonResource(Pokedex pokedex, PokeApiDonneesPokemon pokeApi) {
        this.pokedex = pokedex;
        this.pokeApi = pokeApi;
    }

    /** GET /api/pokemons?recherche=sala */
    @GetMapping
    public List<EntreePokedex> rechercher(@RequestParam(defaultValue = "") String recherche) {
        return pokedex.rechercher(recherche);
    }

    /** GET /api/pokemons/charmander/attaques */
    @GetMapping("/{nom}/attaques")
    public List<AttaqueDisponible> attaques(@PathVariable String nom) {
        return pokeApi.attaquesDe(nom);
    }
}
