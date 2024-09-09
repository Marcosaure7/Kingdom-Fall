package personnages;

import exceptions.InventairePleinException;
import objets.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Joueur extends Entite {

    // La Map contient comme clé un type d'objet et comme valeur la liste des objets de ce type dans l'inventaire.
    private Map<Type_Objet, ArrayList<Objet>> inventaire;
    private Exp xp;
    private int xpCap;

    public Joueur(int ptsVie, int niveau, int attBase) {
        super(ptsVie, niveau, attBase);
        inventaire = new HashMap<>();
        for (Type_Objet o: Type_Objet.values()) {
            inventaire.put(o, new ArrayList<>());
        }
        xp = new Exp(0);
        xpCap = 10;
    }

    public void ramasserObjet(@NotNull Objet objet) throws InventairePleinException {
        // Check si inventaire disponible
        if (inventaire.get(objet.getType()).size() < objet.getType().getEspaceInventaire()) {
            // Il y a de la place, on ajoute l'objet :
            inventaire.get(objet.getType()).add(objet);
        }
        else {
            throw new InventairePleinException("Inventaire plein!");
        }
    }

    public void pincer_bouche() {
        ptsVie -= 50;
    }

    public String toString() {
        return String.format("Joueur\n\nNiv:%d\tPV:%d\tAtt:%d", niveau, ptsVie, attBase);
    }

    public void gainXp (Exp xp) {
        this.xp.gainExp(xp);
        System.out.print("XP: " + this.xp.getValeur() + "/" + this.xpCap);
        if (this.xp.getValeur() >= xpCap) {
            System.out.println(String.format("\tVous montez de niveau !\tNiv:%d -> %d", niveau, ++niveau));
            this.xp = new Exp((int) (this.xp.getValeur() - xpCap));
            xpCap = (int) (xpCap * 1.5);
            System.out.println(String.format("Prochain niveau : XP: %d/%d", this.xp.getValeur(), xpCap));
        }
    }

    public void ajouterALInventaire(Objet objet) throws InventairePleinException {
        if (inventaire.get(objet.getType()).size() < objet.getType().getEspaceInventaire()) {
            inventaire.get(objet.getType()).add(objet);
        }
        else {
            throw new InventairePleinException("Inventaire plein!");
        }
    }
}
