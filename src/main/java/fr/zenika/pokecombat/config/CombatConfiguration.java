package fr.zenika.pokecombat.config;

import fr.zenika.pokecombat.combat.Degats;
import fr.zenika.pokecombat.combat.DonneesPokemon;
import fr.zenika.pokecombat.combat.Duel;
import fr.zenika.pokecombat.combat.Efficacite;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/** Le package combat est du Java pur : c'est ici qu'on le branche sur Spring. */
@Configuration
@EnableCaching
public class CombatConfiguration {

    @Bean
    Efficacite efficacite(DonneesPokemon donnees) {
        return new Efficacite(donnees);
    }

    @Bean
    Degats degats(Efficacite efficacite) {
        return new Degats(efficacite);
    }

    @Bean
    Duel duel(Degats degats) {
        return new Duel(degats);
    }

    @Bean
    RestClient pokeApiRestClient(RestClient.Builder builder, @Value("${pokecombat.pokeapi.url}") String url) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        return builder.baseUrl(url).requestFactory(requestFactory).build();
    }
}
