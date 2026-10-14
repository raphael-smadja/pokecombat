package fr.zenika.pokecombat.web.rest;

import fr.zenika.pokecombat.combat.Attaque;
import fr.zenika.pokecombat.combat.Combattant;
import fr.zenika.pokecombat.combat.DonneesPokemon;
import fr.zenika.pokecombat.combat.Duel;
import fr.zenika.pokecombat.combat.EntreeJournal;
import fr.zenika.pokecombat.combat.Pokemon;
import fr.zenika.pokecombat.combat.ResultatDuel;
import fr.zenika.pokecombat.combat.Type;
import fr.zenika.pokecombat.combat.Vainqueur;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/combats")
public class CombatResource {

    private final DonneesPokemon donnees;
    private final Duel duel;

    public CombatResource(DonneesPokemon donnees, Duel duel) {
        this.donnees = donnees;
        this.duel = duel;
    }

    /** POST /api/combats {"gauche": {"pokemon": "charmander", "attaque": "ember"}, "droite": {...}} */
    @PostMapping
    public ReponseCombat combattre(@RequestBody DemandeCombat demande) {
        Combattant gauche = combattant(demande.gauche());
        Combattant droite = combattant(demande.droite());
        if (gauche.pokemon().nom().equals(droite.pokemon().nom())) {
            // Duel miroir : on distingue les deux Pokémon dans le journal
            gauche = renommer(gauche, " de gauche");
            droite = renommer(droite, " de droite");
        }
        ResultatDuel resultat = duel.combattre(gauche, droite);
        return new ReponseCombat(resultat.vainqueur(), resultat.tours(), resultat.journal(),
                FicheCombattant.de(gauche), FicheCombattant.de(droite));
    }

    private Combattant combattant(Choix choix) {
        if (choix == null || choix.pokemon() == null || choix.attaque() == null) {
            throw new IllegalArgumentException("Il faut un Pokémon et une attaque de chaque côté.");
        }
        return new Combattant(donnees.pokemon(choix.pokemon()), donnees.attaque(choix.attaque()));
    }

    private static Combattant renommer(Combattant combattant, String suffixe) {
        Pokemon pokemon = combattant.pokemon();
        return new Combattant(new Pokemon(pokemon.nom() + suffixe, pokemon.types(), pokemon.pvDeBase(), pokemon.vitesse()),
                combattant.attaque());
    }

    public record Choix(String pokemon, String attaque) {
    }

    public record DemandeCombat(Choix gauche, Choix droite) {
    }

    public record ReponseCombat(Vainqueur vainqueur, int tours, List<EntreeJournal> journal,
                                FicheCombattant gauche, FicheCombattant droite) {
    }

    public record FicheCombattant(String nom, List<Type> types, int pvDeCombat, int vitesse,
                                  String attaque, Type typeAttaque, Integer puissance) {

        static FicheCombattant de(Combattant combattant) {
            Pokemon pokemon = combattant.pokemon();
            Attaque attaque = combattant.attaque();
            return new FicheCombattant(pokemon.nom(), pokemon.types(), Duel.pvDeCombat(pokemon), pokemon.vitesse(),
                    attaque.nom(), attaque.type(), attaque.puissance());
        }
    }
}
