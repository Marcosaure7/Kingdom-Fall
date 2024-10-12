package personnages;

import objets.Exp;
import objets.Objet;

import java.util.ArrayList;

public class Ennemi extends Entite {

    private double poidsSpawn;
    private final Exp xpDrop;

    private final ArrayList<Objet> drops;

    public static final Ennemi LAMBDA = new Ennemi("lambda", 0, 0, 0, 0, 0, new ArrayList<>());


    public Ennemi(String nom, int ptsVie, int niveau, int att, double poidsSpawn, int xpDrop, ArrayList<Objet> drops) {
        super(nom, ptsVie, niveau, att);
        this.drops = drops;
        this.poidsSpawn = poidsSpawn;
        this.xpDrop = new Exp(xpDrop);
    }

    /**
     * Constructeur copie
     * @param autre Objet initial qu'on veut copier
     */
    public Ennemi (Ennemi autre)
    {
        super(autre);
        this.drops = autre.drops;
        this.poidsSpawn = autre.poidsSpawn;
        this.xpDrop = new Exp(autre.xpDrop.getValeur());
    }

    // public void setPoidsSpawn(int poidsSpawn) { this.poidsSpawn = poidsSpawn; }
    public double getPoidsSpawn() { return this.poidsSpawn; }
    public String getNom() { return this.nom; }
    public String toString(){
        return String.format("%s\n\nNiv:%d\tPV:%d\tAtt:%d\t", nom, niveau, ptsVie, attBase);
    }
    public Exp getXpDrop() { return this.xpDrop; }
    public ArrayList<Objet> getDrops ()
    {
        return this.drops;
    }

    public static void pondererPoidsSpawn(ArrayList<Ennemi> ennemis) {
        double totalDropRates = 0;
        for (Ennemi ennemi : ennemis) {
            totalDropRates += ennemi.poidsSpawn;
        }
        for (Ennemi ennemi : ennemis) {
            ennemi.poidsSpawn /= totalDropRates;
        }
    }

}
