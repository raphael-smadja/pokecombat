package fr.zenika.pokecombat.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.zenika.pokecombat.combat.DonneesPokemon;
import fr.zenika.pokecombat.combat.FaussesDonneesPokemon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Test d'intégration de l'API : l'appli complète, mais sans réseau grâce aux fausses données. */
@SpringBootTest(properties = "pokecombat.prechauffage=false")
@AutoConfigureMockMvc
class CombatResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void salamecheGagneContreBulbizarreEnDeuxTours() throws Exception {
        mockMvc.perform(post("/api/combats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"gauche": {"pokemon": "charmander", "attaque": "ember"},
                                 "droite": {"pokemon": "bulbasaur", "attaque": "vine-whip"}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vainqueur").value("GAUCHE"))
                .andExpect(jsonPath("$.tours").value(2))
                .andExpect(jsonPath("$.gauche.pvDeCombat").value(117))
                .andExpect(jsonPath("$.journal[0].message").value("C'est super efficace !"))
                .andExpect(jsonPath("$.journal[0].pvRestants").value(15));
    }

    @Test
    void refuseUnCombatSansAttaque() throws Exception {
        mockMvc.perform(post("/api/combats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"gauche": {"pokemon": "charmander"},
                                 "droite": {"pokemon": "bulbasaur", "attaque": "vine-whip"}}
                                """))
                .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class SansReseau {

        @Bean
        @Primary
        DonneesPokemon faussesDonneesPokemon() {
            return new FaussesDonneesPokemon();
        }
    }
}
