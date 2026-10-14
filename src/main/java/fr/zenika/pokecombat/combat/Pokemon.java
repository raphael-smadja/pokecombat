package fr.zenika.pokecombat.combat;

import java.util.List;

/** Fiche d'un Pokémon : un ou deux types, PV de base et vitesse. */
public record Pokemon(String nom, List<Type> types, int pvDeBase, int vitesse) {
}
