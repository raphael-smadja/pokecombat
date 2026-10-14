package fr.zenika.pokecombat.pokeapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.zenika.pokecombat.combat.Type;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * La table des efficacités, figée dans un snapshot JSON généré une fois depuis PokeAPI
 * (scripts/generer_snapshots.py). Les règles ne dépendent pas du réseau et restent déterministes.
 */
@Component
public class TableDesTypes {

    static final String SNAPSHOT = "pokeapi/table-des-types.json";

    private final Map<Type, Map<Type, Double>> efficacites = new EnumMap<>(Type.class);

    public TableDesTypes(ObjectMapper objectMapper) {
        try (InputStream json = new ClassPathResource(SNAPSHOT).getInputStream()) {
            Snapshot snapshot = objectMapper.readValue(json, Snapshot.class);
            snapshot.efficacites().forEach((attaque, defenses) -> {
                Map<Type, Double> ligne = new EnumMap<>(Type.class);
                defenses.forEach((defense, valeur) -> ligne.put(TypesPokeApi.depuisPokeApi(defense), valeur));
                efficacites.put(TypesPokeApi.depuisPokeApi(attaque), ligne);
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Snapshot illisible : " + SNAPSHOT, e);
        }
    }

    /** Absent de la table = efficacité normale (×1). */
    public double multiplicateur(Type typeAttaque, Type typeDefense) {
        return efficacites.getOrDefault(typeAttaque, Map.of()).getOrDefault(typeDefense, 1.0);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Snapshot(Map<String, Map<String, Double>> efficacites) {
    }
}
