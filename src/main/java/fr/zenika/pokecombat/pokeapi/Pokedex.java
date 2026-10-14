package fr.zenika.pokecombat.pokeapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/** Les noms français (snapshot PokeAPI) : recherche « sala » → Salamèche, et affichage des attaques. */
@Component
public class Pokedex {

    private final List<EntreePokedex> pokemons;
    private final Map<String, String> attaquesFr;

    public Pokedex(ObjectMapper objectMapper) {
        this.pokemons = lire(objectMapper, "pokeapi/pokemons.json", new TypeReference<>() {});
        this.attaquesFr = lire(objectMapper, "pokeapi/attaques-fr.json", new TypeReference<>() {});
    }

    public List<EntreePokedex> rechercher(String recherche) {
        String texte = normaliser(recherche == null ? "" : recherche);
        return pokemons.stream()
                .filter(p -> normaliser(p.nomFr()).contains(texte) || p.nom().contains(texte))
                .toList();
    }

    /** Accepte le nom PokeAPI (« charmander ») ou le nom français (« Salamèche », « salameche »). */
    public Optional<EntreePokedex> trouver(String nom) {
        String texte = normaliser(nom);
        return pokemons.stream()
                .filter(p -> p.nom().equals(texte) || normaliser(p.nomFr()).equals(texte))
                .findFirst();
    }

    public String nomFrDeLAttaque(String nomPokeApi) {
        return attaquesFr.getOrDefault(nomPokeApi, nomPokeApi);
    }

    static String normaliser(String texte) {
        return Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private static <T> T lire(ObjectMapper objectMapper, String chemin, TypeReference<T> type) {
        try (InputStream json = new ClassPathResource(chemin).getInputStream()) {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new UncheckedIOException("Snapshot illisible : " + chemin, e);
        }
    }

    public record EntreePokedex(int id, String nom, String nomFr, List<String> types) {
    }
}
