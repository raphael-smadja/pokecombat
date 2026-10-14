package fr.zenika.pokecombat.combat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class EfficaciteTest {

    @Test
    void chaqueMultiplicateurAUnMessage() {
        for (double multiplicateur : new double[] {0, 0.5, 1, 2}) {
            assertNotNull(Efficacite.message(multiplicateur));
        }
    }
}
