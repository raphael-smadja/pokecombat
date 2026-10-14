package fr.zenika.pokecombat.combat;

import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.BULBIZARRE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.CHARGE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.FANTOMINUS;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.FLAMMECHE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.FOUET_LIANES;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.LECHOUILLE;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.RATTATA;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.SALAMECHE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
