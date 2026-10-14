package fr.zenika.pokecombat.combat;

/** Niveaux 3 et 4 : bonus de type, puis calcul des dégâts d'une attaque. */
public class Degats {

    public static final String ATTAQUE_DE_STATUT = "Attaque de statut : aucun dégât.";
    static final double BONUS_MEME_TYPE = 1.5;

    private final Efficacite efficacite;

    public Degats(Efficacite efficacite) {
        this.efficacite = efficacite;
    }

    public ResultatAttaque calculer(Pokemon attaquant, Attaque attaque, Pokemon defenseur) {
        if (attaque.puissance() == null) {
            return new ResultatAttaque(0, ATTAQUE_DE_STATUT);
        }
        double multiplicateur = efficacite.multiplicateur(attaque.type(), defenseur);
        String message = Efficacite.message(multiplicateur);
        if (multiplicateur == 0) {
            return new ResultatAttaque(0, message);
        }
        double degats = attaque.puissance() * bonusDeType(attaquant, attaque) * multiplicateur;
        return new ResultatAttaque((int) Math.floor(degats), message);
    }

    /** Niveau 3 : ×1,5 si le type de l'attaque est l'un des types du Pokémon qui la lance. */
    static double bonusDeType(Pokemon attaquant, Attaque attaque) {
        return attaquant.types().contains(attaque.type()) ? BONUS_MEME_TYPE : 1;
    }
}
