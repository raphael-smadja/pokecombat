package fr.zenika.pokecombat.combat;

import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.BULBIZARRE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.CHARGE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.ECLAIR;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.FANTOMINUS;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.FLAMMECHE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.FOUET_LIANES;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.JET_PIERRES;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.LECHOUILLE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.LEVIATOR;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.PIKACHU;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.PISTOLET_A_O;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.RACAILLOU;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.RATTATA;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.SALAMECHE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class DuelTest {

    private final Duel duel = new Duel(new Degats(new Efficacite(new FaussesDonneesPokemon())));

    @Test
    void salamecheBatBulbizarre() {
        ResultatDuel resultat = duel.combattre(
                new Combattant(SALAMECHE, FLAMMECHE), new Combattant(BULBIZARRE, FOUET_LIANES));

        assertEquals(Vainqueur.GAUCHE, resultat.vainqueur());
    }

    @Test
    void combatSansEffetSeTermine() {
        ResultatDuel resultat = duel.combattre(
                new Combattant(RATTATA, CHARGE), new Combattant(FANTOMINUS, LECHOUILLE));

        assertNotNull(resultat);
    }

    // --- Poké Balls ---

    @Test
    void duelMiroirLaGaucheCommenceEtGagneEnQuatreTours() {
        ResultatDuel resultat = duel.combattre(
                new Combattant(SALAMECHE, FLAMMECHE), new Combattant(SALAMECHE, FLAMMECHE));

        assertEquals(Vainqueur.GAUCHE, resultat.vainqueur());
        assertEquals(4, resultat.tours());
    }

    @Test
    void salamecheBatBulbizarreEnDeuxTours() {
        ResultatDuel resultat = duel.combattre(
                new Combattant(SALAMECHE, FLAMMECHE), new Combattant(BULBIZARRE, FOUET_LIANES));

        assertEquals(Vainqueur.GAUCHE, resultat.vainqueur());
        assertEquals(2, resultat.tours());
        assertEquals(List.of(120, 33, 120), degats(resultat));
        assertEquals(List.of(15, 84, 0), pvRestants(resultat));
    }

    @Test
    void pikachuBatLeviatorEnDeuxTours() {
        ResultatDuel resultat = duel.combattre(
                new Combattant(PIKACHU, ECLAIR), new Combattant(LEVIATOR, PISTOLET_A_O));

        assertEquals(Vainqueur.GAUCHE, resultat.vainqueur());
        assertEquals(2, resultat.tours());
        assertEquals(List.of(45, 45, 0), pvRestants(resultat));
    }

    @Test
    void racaillouImmuniseBatPikachuEnDeuxTours() {
        ResultatDuel resultat = duel.combattre(
                new Combattant(PIKACHU, ECLAIR), new Combattant(RACAILLOU, JET_PIERRES));

        assertEquals(Vainqueur.DROITE, resultat.vainqueur());
        assertEquals(2, resultat.tours());
        assertEquals(List.of(0, 75, 0, 75), degats(resultat));
    }

    @Test
    void rattataEtFantominusFontMatchNulApresCinquanteTours() {
        ResultatDuel resultat = duel.combattre(
                new Combattant(RATTATA, CHARGE), new Combattant(FANTOMINUS, LECHOUILLE));

        assertEquals(Vainqueur.NUL, resultat.vainqueur());
        assertEquals(50, resultat.tours());
        assertEquals(100, resultat.journal().size());
    }

    private static List<Integer> degats(ResultatDuel resultat) {
        return resultat.journal().stream().map(EntreeJournal::degats).toList();
    }

    private static List<Integer> pvRestants(ResultatDuel resultat) {
        return resultat.journal().stream().map(EntreeJournal::pvRestants).toList();
    }
}
