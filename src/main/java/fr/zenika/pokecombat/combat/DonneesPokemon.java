package fr.zenika.pokecombat.combat;

/**
 * Tout ce que le métier sait de PokeAPI passe par cette interface.
 * En production : PokeApiDonneesPokemon. En test : FaussesDonneesPokemon, sans réseau.
 */
public interface DonneesPokemon {

    Pokemon pokemon(String nom);

    Attaque attaque(String nom);

    /** Niveau 1 : multiplicateur d'un type d'attaque contre un type de défense (0, ½, 1 ou 2). */
    double efficacite(Type typeAttaque, Type typeDefense);
}
