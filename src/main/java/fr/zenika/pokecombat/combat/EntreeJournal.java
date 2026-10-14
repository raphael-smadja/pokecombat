package fr.zenika.pokecombat.combat;

/** Une ligne du journal de combat : qui attaque, combien de dégâts, le message, les PV restants. */
public record EntreeJournal(
        int tour,
        String attaquant,
        String attaque,
        String defenseur,
        int degats,
        String message,
        int pvRestants) {
}
