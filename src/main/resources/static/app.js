// Pokécombat : l'interface ne fait qu'afficher. Toutes les règles sont côté serveur, dans le package combat.

const TYPES = {
    NORMAL: ["Normal", "#9fa19f"], FEU: ["Feu", "#e62829"], EAU: ["Eau", "#2980ef"],
    PLANTE: ["Plante", "#3fa129"], ELECTRIK: ["Électrik", "#fac000"], GLACE: ["Glace", "#3dcef3"],
    COMBAT: ["Combat", "#ff8000"], POISON: ["Poison", "#9141cb"], SOL: ["Sol", "#915121"],
    VOL: ["Vol", "#81b9ef"], PSY: ["Psy", "#ef4179"], INSECTE: ["Insecte", "#91a119"],
    ROCHE: ["Roche", "#afa981"], SPECTRE: ["Spectre", "#704170"], DRAGON: ["Dragon", "#5060e1"],
    TENEBRES: ["Ténèbres", "#624d4e"], ACIER: ["Acier", "#60a1b8"], FEE: ["Fée", "#ef70ef"],
};
const TYPES_POKEAPI = {
    normal: "NORMAL", fire: "FEU", water: "EAU", grass: "PLANTE", electric: "ELECTRIK", ice: "GLACE",
    fighting: "COMBAT", poison: "POISON", ground: "SOL", flying: "VOL", psychic: "PSY", bug: "INSECTE",
    rock: "ROCHE", ghost: "SPECTRE", dragon: "DRAGON", dark: "TENEBRES", steel: "ACIER", fairy: "FEE",
};
const SCENARIOS = {
    nominal: [["charmander", "ember"], ["bulbasaur", "vine-whip"]],
    miroir: [["charmander", "ember"], ["charmander", "ember"]],
    quadruple: [["pikachu", "thunder-shock"], ["gyarados", "water-gun"]],
    immunite: [["pikachu", "thunder-shock"], ["geodude", "rock-throw"]],
    nul: [["rattata", "tackle"], ["gastly", "lick"]],
};
const SPRITE = id => `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png`;
const pause = ms => new Promise(r => setTimeout(r, ms));

let pokedex = [];
const cotes = {};

async function json(url, options) {
    const reponse = await fetch(url, options);
    if (!reponse.ok) {
        const probleme = await reponse.json().catch(() => ({}));
        throw new Error(probleme.detail || `Erreur ${reponse.status}`);
    }
    return reponse.json();
}

function badge(type) {
    const [libelle, couleur] = TYPES[type] || [type, "#888"];
    const span = document.createElement("span");
    span.className = "type";
    span.style.background = couleur;
    span.textContent = libelle;
    return span;
}

function creerCote(id) {
    const racine = document.getElementById(id);
    const cote = {
        racine,
        entree: racine.querySelector(".choix-pokemon"),
        attaque: racine.querySelector(".choix-attaque"),
        sprite: racine.querySelector(".sprite"),
        types: racine.querySelector(".types"),
        jauge: racine.querySelector(".jauge"),
        pvTexte: racine.querySelector(".pv-texte"),
        pokemon: null,
    };
    cote.sprite.addEventListener("error", () => cote.sprite.setAttribute("src", ""));
    cote.entree.addEventListener("change", () => choisirPokemon(cote, cote.entree.value));
    cote.attaque.addEventListener("change", majBouton);
    return cote;
}

async function choisirPokemon(cote, saisie, attaqueVoulue) {
    const texte = saisie.trim().toLowerCase();
    const entree = pokedex.find(p => p.nomFr.toLowerCase() === texte || p.nom === texte);
    if (!entree) return;
    cote.pokemon = entree;
    cote.entree.value = entree.nomFr;
    cote.sprite.className = "sprite";
    cote.sprite.src = SPRITE(entree.id);
    cote.sprite.alt = entree.nomFr;
    cote.types.replaceChildren(...entree.types.map(t => badge(TYPES_POKEAPI[t])));
    afficherPv(cote, null, null);

    cote.attaque.disabled = true;
    cote.attaque.replaceChildren(new Option("Chargement…", ""));
    try {
        const attaques = await json(`/api/pokemons/${entree.nom}/attaques`);
        cote.attaque.replaceChildren(...attaques.map(a => new Option(a.nomFr, a.nom)));
        cote.attaque.value = attaqueVoulue && attaques.some(a => a.nom === attaqueVoulue) ? attaqueVoulue : attaques[0]?.nom;
        cote.attaque.disabled = false;
    } catch (e) {
        cote.attaque.replaceChildren(new Option(e.message, ""));
    }
    majBouton();
}

function majBouton() {
    const pret = Object.values(cotes).every(c => c.pokemon && c.attaque.value);
    document.getElementById("combattre").disabled = !pret;
}

function afficherPv(cote, pv, pvMax) {
    if (pv === null) {
        cote.jauge.style.width = "100%";
        cote.jauge.style.background = "var(--vert)";
        cote.pvTexte.textContent = "";
        return;
    }
    const ratio = pv / pvMax;
    cote.jauge.style.width = `${Math.max(0, ratio * 100)}%`;
    cote.jauge.style.background = ratio > .5 ? "var(--vert)" : ratio > .2 ? "var(--orange)" : "var(--rouge)";
    cote.pvTexte.textContent = `${pv} / ${pvMax} PV`;
}

function classeDuMessage(message) {
    if (message.startsWith("C'est super")) return "super";
    if (message.startsWith("Ce n'est pas")) return "faible";
    if (message.startsWith("Ça n'a") || message.startsWith("Attaque de statut")) return "nul";
    return "";
}

async function combattre() {
    const bouton = document.getElementById("combattre");
    const journal = document.getElementById("journal");
    const vainqueur = document.getElementById("vainqueur");
    bouton.disabled = true;
    journal.replaceChildren();
    vainqueur.textContent = "";

    let resultat;
    try {
        resultat = await json("/api/combats", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                gauche: { pokemon: cotes.gauche.pokemon.nom, attaque: cotes.gauche.attaque.value },
                droite: { pokemon: cotes.droite.pokemon.nom, attaque: cotes.droite.attaque.value },
            }),
        });
    } catch (e) {
        vainqueur.textContent = e.message;
        majBouton();
        return;
    }

    const fiches = { gauche: resultat.gauche, droite: resultat.droite };
    for (const c of ["gauche", "droite"]) {
        cotes[c].sprite.className = "sprite";
        afficherPv(cotes[c], fiches[c].pvDeCombat, fiches[c].pvDeCombat);
    }
    const delai = resultat.journal.length > 20 ? 40 : 450;

    for (const entree of resultat.journal) {
        await pause(delai);
        const defenseur = entree.defenseur === fiches.gauche.nom ? "gauche" : "droite";
        const cote = cotes[defenseur];
        afficherPv(cote, entree.pvRestants, fiches[defenseur].pvDeCombat);
        cote.sprite.classList.remove("touche");
        void cote.sprite.offsetWidth;
        cote.sprite.classList.add("touche");
        if (entree.pvRestants === 0) cote.sprite.classList.add("ko");

        const li = document.createElement("li");
        li.className = classeDuMessage(entree.message);
        li.innerHTML = `<span class="tour">T${entree.tour}</span>`;
        li.append(`${entree.attaquant} lance ${entree.attaque} : ${entree.degats} dégât${entree.degats > 1 ? "s" : ""}. `);
        const message = document.createElement("span");
        message.className = "message";
        message.textContent = entree.message;
        li.append(message, ` ${entree.defenseur} : ${entree.pvRestants} PV.`);
        journal.append(li);
        li.scrollIntoView({ block: "nearest" });
    }

    const fin = document.createElement("li");
    fin.className = "fin";
    fin.textContent = resultat.vainqueur === "NUL"
        ? `Match nul après ${resultat.tours} tours.`
        : `${fiches[resultat.vainqueur.toLowerCase()].nom} gagne en ${resultat.tours} tour${resultat.tours > 1 ? "s" : ""} !`;
    journal.append(fin);
    fin.scrollIntoView({ block: "nearest" });
    vainqueur.textContent = fin.textContent;
    majBouton();
}

async function lancerScenario(nom) {
    const [[pg, ag], [pd, ad]] = SCENARIOS[nom];
    await Promise.all([choisirPokemon(cotes.gauche, pg, ag), choisirPokemon(cotes.droite, pd, ad)]);
}

async function demarrer() {
    cotes.gauche = creerCote("gauche");
    cotes.droite = creerCote("droite");
    pokedex = await json("/api/pokemons");
    document.getElementById("liste-pokemons").replaceChildren(...pokedex.map(p => new Option(p.nomFr)));
    document.getElementById("combattre").addEventListener("click", combattre);
    document.querySelectorAll("[data-scenario]").forEach(b =>
        b.addEventListener("click", () => lancerScenario(b.dataset.scenario)));
    await lancerScenario("nominal");
}

demarrer();
