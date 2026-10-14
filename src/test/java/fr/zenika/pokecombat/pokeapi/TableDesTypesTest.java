package fr.zenika.pokecombat.pokeapi;

import static fr.zenika.pokecombat.combat.Type.EAU;
import static fr.zenika.pokecombat.combat.Type.ELECTRIK;
import static fr.zenika.pokecombat.combat.Type.FEU;
import static fr.zenika.pokecombat.combat.Type.NORMAL;
import static fr.zenika.pokecombat.combat.Type.PLANTE;
import static fr.zenika.pokecombat.combat.Type.SPECTRE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/** Niveau 1 vérifié sur le vrai snapshot généré depuis PokeAPI. */
class TableDesTypesTest {

    private final TableDesTypes table = new TableDesTypes(new ObjectMapper());

    @Test
    void feuContrePlanteEstDoublementEfficace() {
        assertEquals(2, table.multiplicateur(FEU, PLANTE));
    }

    @Test
    void feuContreEauNeFaitQueLaMoitie() {
        assertEquals(0.5, table.multiplicateur(FEU, EAU));
    }

    @Test
    void normalContreSpectreNaAucunEffet() {
        assertEquals(0, table.multiplicateur(NORMAL, SPECTRE));
    }

    @Test
    void normalContreElectrikEstNormal() {
        assertEquals(1, table.multiplicateur(NORMAL, ELECTRIK));
    }
}
