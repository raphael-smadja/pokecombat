package fr.zenika.pokecombat.combat;

/** Un Pokémon engagé dans un duel, avec l'attaque qu'il utilisera à chaque tour. */
public record Combattant(Pokemon pokemon, Attaque attaque) {
}
