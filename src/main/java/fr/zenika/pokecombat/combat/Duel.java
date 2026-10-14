package fr.zenika.pokecombat.combat;

import java.util.ArrayList;
import java.util.List;

/** Niveau 5 : le duel tour par tour, jusqu'au K.O. ou au match nul. */
public class Duel {

    static final int MULTIPLICATEUR_PV = 3;
    static final int TOURS_MAXIMUM = 50;

    private final Degats degats;

    public Duel(Degats degats) {
        this.degats = degats;
    }

    /** PV de combat = PV de base × 3, pour que le combat dure quelques tours. */
    public static int pvDeCombat(Pokemon pokemon) {
        return pokemon.pvDeBase() * MULTIPLICATEUR_PV;
    }

    public ResultatDuel combattre(Combattant gauche, Combattant droite) {
        EnCombat premier = new EnCombat(gauche, Vainqueur.GAUCHE);
        EnCombat second = new EnCombat(droite, Vainqueur.DROITE);
        // Le plus rapide attaque en premier. En cas d'égalité, la gauche commence.
        if (droite.pokemon().vitesse() > gauche.pokemon().vitesse()) {
            EnCombat plusRapide = second;
            second = premier;
            premier = plusRapide;
        }

        List<EntreeJournal> journal = new ArrayList<>();
        int tour = 0;
        while (tour < TOURS_MAXIMUM) {
            tour++;
            attaquer(premier, second, tour, journal);
            if (second.estKo()) {
                return new ResultatDuel(premier.camp, tour, journal);
            }
            attaquer(second, premier, tour, journal);
            if (premier.estKo()) {
                return new ResultatDuel(second.camp, tour, journal);
            }
        }
        return new ResultatDuel(Vainqueur.NUL, tour, journal);
    }

    private void attaquer(EnCombat attaquant, EnCombat defenseur, int tour, List<EntreeJournal> journal) {
        Attaque attaque = attaquant.combattant.attaque();
        ResultatAttaque resultat = degats.calculer(attaquant.pokemon(), attaque, defenseur.pokemon());
        defenseur.subir(resultat.degats());
        journal.add(new EntreeJournal(tour, attaquant.pokemon().nom(), attaque.nom(), defenseur.pokemon().nom(),
                resultat.degats(), resultat.message(), defenseur.pv));
    }

    /** L'état d'un Pokémon pendant le duel : ses PV de combat évoluent à chaque attaque subie. */
    private static final class EnCombat {
        private final Combattant combattant;
        private final Vainqueur camp;
        private int pv;

        private EnCombat(Combattant combattant, Vainqueur camp) {
            this.combattant = combattant;
            this.camp = camp;
            this.pv = pvDeCombat(combattant.pokemon());
        }

        private Pokemon pokemon() {
            return combattant.pokemon();
        }

        /** Les PV ne descendent jamais sous 0. */
        private void subir(int degats) {
            pv = Math.max(0, pv - degats);
        }

        private boolean estKo() {
            return pv <= 0;
        }
    }
}
