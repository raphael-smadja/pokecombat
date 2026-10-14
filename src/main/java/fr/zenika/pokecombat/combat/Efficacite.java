package fr.zenika.pokecombat.combat;

import java.util.List;

/** Niveaux 1 et 2 : efficacité d'un type d'attaque contre le ou les types du défenseur. */
public class Efficacite {

    public static final String AUCUN_EFFET = "Ça n'a aucun effet.";
    public static final String SUPER_EFFICACE = "C'est super efficace !";
    public static final String PAS_TRES_EFFICACE = "Ce n'est pas très efficace…";
    public static final String ATTAQUE_NORMALE = "Attaque normale.";

    private final DonneesPokemon donnees;

    public Efficacite(DonneesPokemon donnees) {
        this.donnees = donnees;
    }

    /** Contre un Pokémon à deux types, on multiplie les deux multiplicateurs : 0, ¼, ½, 1, 2 ou 4. */
    public double multiplicateur(Type typeAttaque, Pokemon defenseur) {
        List<Type> types = defenseur.types();
        double multiplicateur = donnees.efficacite(typeAttaque, types.get(0));
        if (types.size() > 1) {
            multiplicateur = multiplicateur * donnees.efficacite(typeAttaque, types.get(1));
        }
        return multiplicateur;
    }

    /** Le message affiché dépend du multiplicateur total. */
    public static String message(double multiplicateur) {
        if (multiplicateur == 0) {
            return AUCUN_EFFET;
        }
        if (multiplicateur > 1) {
            return SUPER_EFFICACE;
        }
        if (multiplicateur < 1) {
            return PAS_TRES_EFFICACE;
        }
        return ATTAQUE_NORMALE;
    }
}
