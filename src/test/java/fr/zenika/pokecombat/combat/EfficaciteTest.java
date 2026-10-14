package fr.zenika.pokecombat.combat;

import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.DRACAUFEU;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.LEVIATOR;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.RACAILLOU;
import static fr.zenika.pokecombat.combat.FaussesDonneesPokemon.SALAMECHE;
import static fr.zenika.pokecombat.combat.Type.ELECTRIK;
import static fr.zenika.pokecombat.combat.Type.PLANTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EfficaciteTest {

    private final Efficacite efficacite = new Efficacite(new FaussesDonneesPokemon());

    @Test
    void chaqueMultiplicateurAUnMessage() {
        for (double multiplicateur : new double[] {0, 0.5, 1, 2}) {
            assertNotNull(Efficacite.message(multiplicateur));
        }
    }

    // --- Poké Balls ---

    @Test
    void contreUnSeulTypeOnLitLaTable() {
        assertEquals(0.5, efficacite.multiplicateur(PLANTE, SALAMECHE));
    }

    @Test
    void contreUnDoubleTypeOnMultiplieLesDeuxEfficacites() {
        assertEquals(4, efficacite.multiplicateur(ELECTRIK, LEVIATOR));
        assertEquals(1, efficacite.multiplicateur(PLANTE, LEVIATOR));
        assertEquals(0.25, efficacite.multiplicateur(PLANTE, DRACAUFEU));
        assertEquals(0, efficacite.multiplicateur(ELECTRIK, RACAILLOU));
    }

    @ParameterizedTest(name = "×{0} → {1}")
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            0    | Ça n'a aucun effet.
            0.25 | Ce n'est pas très efficace…
            0.5  | Ce n'est pas très efficace…
            1    | Attaque normale.
            2    | C'est super efficace !
            4    | C'est super efficace !
            """)
    void leMessageDependDuMultiplicateurTotal(double multiplicateur, String message) {
        assertEquals(message, Efficacite.message(multiplicateur));
    }
}
