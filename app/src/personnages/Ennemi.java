package personnages;

import objets.Exp;

public class Ennemi extends Entite {

    private int poidsSpawn;
    private String nom;
    private Exp xpDrop;

    public Ennemi(String nom, int ptsVie, int niveau, int att, int poidsSpawn, int xpDrop) {
        super(ptsVie, niveau, att);
        this.nom = nom;
        this.poidsSpawn = poidsSpawn;
        this.xpDrop = new Exp(xpDrop);
    }

    // public void setPoidsSpawn(int poidsSpawn) { this.poidsSpawn = poidsSpawn; }
    public int getPoidsSpawn() { return this.poidsSpawn; }
    public String getNom() { return this.nom; }
    public String toString(){
        return String.format("%s\n\nNiv:%d\tPV:%d\tAtt:%d\t", nom, niveau, ptsVie, attBase);
    }
    public Exp getXpDrop() { return this.xpDrop; }


}
