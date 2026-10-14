package fr.zenika.pokecombat.combat;

import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.BULBIZARRE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.ECLAIR;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.FLAMMECHE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.FOUET_LIANES;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.LEVIATOR;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.MIMI_QUEUE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.PIKACHU;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.RACAILLOU;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.RATTATA;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.SALAMECHE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DegatsTest {

    private final Degats degats = new Degats(new Efficacite(new FaussesDonneesPokemon()));

    @Test
    void flammecheSurBulbizarreFait120() {
        ResultatAttaque resultat = degats.calculer(SALAMECHE, FLAMMECHE, BULBIZARRE);

        assertEquals(120, resultat.degats());
    }

    @Test
    void attaqueDeStatutNeLevePasDException() {
        assertDoesNotThrow(() -> degats.calculer(RATTATA, MIMI_QUEUE, PIKACHU));
    }

    @Test
    void immuniteDonneDesDegatsPositifs() {
        ResultatAttaque resultat = degats.calculer(PIKACHU, ECLAIR, RACAILLOU);

        assertTrue(resultat.degats() >= 0);
    }

    // --- Poké Balls ---

    @Test
    void fouetLianesSurLeviatorEstUneAttaqueNormale() {
        ResultatAttaque resultat = degats.calculer(BULBIZARRE, FOUET_LIANES, LEVIATOR);

        assertEquals(67, resultat.degats());
        assertEquals("Attaque normale.", resultat.message());
    }

    @Test
    void eclairSurLeviatorQuadrupleLesDegats() {
        ResultatAttaque resultat = degats.calculer(PIKACHU, ECLAIR, LEVIATOR);

        assertEquals(240, resultat.degats());
        assertEquals("C'est super efficace !", resultat.message());
    }
}
