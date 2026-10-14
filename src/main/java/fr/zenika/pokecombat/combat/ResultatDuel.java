package fr.zenika.pokecombat.combat;

import java.util.List;

public record ResultatDuel(Vainqueur vainqueur, int tours, List<EntreeJournal> journal) {
}
