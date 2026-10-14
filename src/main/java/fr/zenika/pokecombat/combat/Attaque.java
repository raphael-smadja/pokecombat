package fr.zenika.pokecombat.combat;

/**
 * Une attaque : son type et sa puissance.
 * La puissance est {@code null} pour les attaques de statut (Mimi-Queue, Rugissement…), comme dans PokeAPI.
 */
public record Attaque(String nom, Type type, Integer puissance) {
}
