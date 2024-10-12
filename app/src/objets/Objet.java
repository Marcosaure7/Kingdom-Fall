package objets;

import java.util.ArrayList;

public abstract class Objet extends Drops {

    protected Type_Objet type;
    protected String nom;
    protected double dropRate;

    public Objet(Type_Objet type, String nom, double dropRate) {
        this.type = type;
        this.nom = nom;
        this.dropRate = dropRate;
    }

    public Objet (Objet autre)
    {
        this.type = autre.type;
        this.nom = autre.nom;
        this.dropRate = autre.dropRate;
    }

    public Type_Objet getType() {
        return type;
    }

    public String getNom() {
        return nom;
    }

    public double getDropRate() {
        return dropRate;
    }

    @Override
    public String toString() {
        return nom;
    }

    public abstract String getDescription();

    public static void pondererDropRates(ArrayList<Objet> objets) {
        double totalDropRates = 0;
        for (Objet objet : objets) {
            totalDropRates += objet.getDropRate();
        }
        for (Objet objet : objets) {
            objet.dropRate /= totalDropRates;
        }
    }
}
