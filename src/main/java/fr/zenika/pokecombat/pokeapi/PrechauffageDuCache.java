package fr.zenika.pokecombat.pokeapi;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Au démarrage, charge en cache les Pokémon et attaques de la démo : la salle ne dépend plus du réseau ensuite. */
@Component
@ConditionalOnProperty(name = "pokecombat.prechauffage", havingValue = "true")
public class PrechauffageDuCache implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(PrechauffageDuCache.class);
    private static final List<String> POKEMONS = List.of(
            "charmander", "bulbasaur", "pikachu", "gyarados", "geodude", "rattata", "gastly", "charizard");
    private static final List<String> ATTAQUES = List.of(
            "ember", "vine-whip", "thunder-shock", "water-gun", "rock-throw", "tackle", "scratch", "lick",
            "wing-attack", "tail-whip");

    private final PokeApiClient client;

    public PrechauffageDuCache(PokeApiClient client) {
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        Thread.ofVirtual().name("prechauffage-pokeapi").start(() -> {
            try {
                POKEMONS.forEach(client::pokemon);
                ATTAQUES.forEach(client::attaque);
                LOG.info("Cache PokeAPI préchauffé : {} Pokémon, {} attaques", POKEMONS.size(), ATTAQUES.size());
            } catch (RuntimeException e) {
                LOG.warn("Préchauffage du cache PokeAPI impossible : {}", e.getMessage());
            }
        });
    }
}
