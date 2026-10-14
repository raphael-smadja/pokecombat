package fr.zenika.pokecombat.pokeapi;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Appels HTTP à PokeAPI, mis en cache : chaque Pokémon et chaque attaque n'est demandé qu'une fois. */
@Component
public class PokeApiClient {

    private final RestClient restClient;

    public PokeApiClient(RestClient pokeApiRestClient) {
        this.restClient = pokeApiRestClient;
    }

    @Cacheable("pokeapi-pokemons")
    public PokemonDto pokemon(String nom) {
        return restClient.get().uri("/pokemon/{nom}", nom).retrieve().body(PokemonDto.class);
    }

    @Cacheable("pokeapi-attaques")
    public MoveDto attaque(String nom) {
        return restClient.get().uri("/move/{nom}", nom).retrieve().body(MoveDto.class);
    }

    public record Reference(String name) {
    }

    public record PokemonDto(int id, String name, List<TypeSlot> types, List<StatDto> stats, List<MoveSlot> moves) {

        public record TypeSlot(int slot, Reference type) {
        }

        public record StatDto(int base_stat, Reference stat) {
        }

        public record MoveSlot(Reference move) {
        }
    }

    /** {@code power} vaut null pour les attaques de statut : le piège réel de l'API. */
    public record MoveDto(String name, Integer power, Reference type, List<NomTraduit> names) {

        public record NomTraduit(String name, Reference language) {
        }
    }
}
