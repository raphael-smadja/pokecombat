package fr.zenika.pokecombat.combat;

import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.BULBIZARRE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.CHARGE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.CRU_AILES;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.DRACAUFEU;
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

    @Test
    void fouetLianesSurDracaufeuNestPasTresEfficace() {
        ResultatAttaque resultat = degats.calculer(BULBIZARRE, FOUET_LIANES, DRACAUFEU);

        assertEquals(16, resultat.degats());
        assertEquals("Ce n'est pas très efficace…", resultat.message());
    }

    @Test
    void eclairSurRacaillouNaAucunEffet() {
        ResultatAttaque resultat = degats.calculer(PIKACHU, ECLAIR, RACAILLOU);

        assertEquals(0, resultat.degats());
        assertEquals("Ça n'a aucun effet.", resultat.message());
    }

    @Test
    void chargeDeSalamecheNaPasDeBonusDeType() {
        ResultatAttaque resultat = degats.calculer(SALAMECHE, CHARGE, BULBIZARRE);

        assertEquals(40, resultat.degats());
        assertEquals("Attaque normale.", resultat.message());
    }

    @Test
    void cruAilesDeDracaufeuALeBonusGraceASonSecondType() {
        ResultatAttaque resultat = degats.calculer(DRACAUFEU, CRU_AILES, BULBIZARRE);

        assertEquals(180, resultat.degats());
        assertEquals("C'est super efficace !", resultat.message());
    }

    @Test
    void mimiQueueEstUneAttaqueDeStatutSansDegats() {
        ResultatAttaque resultat = degats.calculer(RATTATA, MIMI_QUEUE, PIKACHU);

        assertEquals(0, resultat.degats());
        assertEquals("Attaque de statut : aucun dégât.", resultat.message());
    }
}
