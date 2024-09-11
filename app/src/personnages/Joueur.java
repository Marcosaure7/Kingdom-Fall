package personnages;

import application.App;
import exceptions.InventairePleinException;
import objets.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Joueur extends Entite {

    // La Map contient comme clé un type d'objet et comme valeur la liste des objets de ce type dans l'inventaire.
    private final Map<Type_Objet, ArrayList<Objet>> inventaire;
    private Arme armeEquipee;
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
            this.xp = new Exp(this.xp.getValeur() - xpCap);
            xpCap = (int) (xpCap * 1.5);
            System.out.println(String.format("Prochain niveau : XP: %d/%d", this.xp.getValeur(), xpCap));
        }
    }

    public void ouvrirInventaire(Scanner sc) {
        System.out.println();
        for (Type_Objet type : inventaire.keySet()) {
            System.out.println(type);
        }

        // Quelle catégorie ouvrir?
        boolean actionReconnue = false;
        StringBuilder inventaireDuType = new StringBuilder();
        String type;
        Type_Objet typeObjet = null;
        do {
            type = sc.nextLine().toUpperCase();
            try {
                typeObjet = Type_Objet.valueOf(type);
                actionReconnue = true;
                for  (int i = 0; i < typeObjet.getEspaceInventaire(); i++) {
                    String objetStr;
                    if (i > inventaire.get(typeObjet).size() - 1)
                        objetStr = "(Vide)";
                    else
                        objetStr = inventaire.get(typeObjet).get(i).getNom();

                    inventaireDuType.append(objetStr).append("\n");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("La commande n'a pas été reconnue !");
            }
        }
        while (!actionReconnue);

        switch (typeObjet) {
            case ARMES:
                System.out.printf("%nArme équipée : %s%n%n", armeEquipee == null ? "Aucun(e)" : armeEquipee.getNom());
                break;
            case ARMURES:
                System.out.printf("%nArmure équipée : %s%n%n", "ARMURE_EQUIPEE");
                break;
        }
        System.out.println(inventaireDuType);
    }
}
