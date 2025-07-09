package objets;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import personnages.Joueur;
import java.util.ArrayList;

public abstract class Objet extends Drops
{

    protected Type_Objet type;
    protected String nom;
    protected double dropRate;
    protected String dropRateString = "";

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
        this.dropRateString = autre.dropRateString;
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
    public abstract VBox formatComparedDescription(Joueur joueur);

    public static ArrayList<Objet> pondererDropRates(ArrayList<Objet> objets, String ennemi) {
        double totalDropRates = 0;
        for (Objet objet : objets) {
            totalDropRates += objet.getDropRate();
        }
        for (Objet objet : objets) {
            objet.dropRate /= totalDropRates;
            objet.dropRateString = String.format("%.2f%s (%s)", objet.dropRate * 100, "%", ennemi);
        }

        return objets;
    }

    public boolean equals(Objet objet)
    {
        return this.nom.equals(objet.nom);
    }

    /**
     *
     * @param newStat the new stat to be compared with the old one
     * @param oldStat the old stat (most likely item equipped)
     * @return Text formatted in result of the two stats compared
     */
    protected Text getComparisonText(int newStat, int oldStat)
    {
        int diff = newStat - oldStat;
        if (diff == 0)
            return new Text(" -");

        Text retour = new Text();
        if (diff > 0) {
            retour.setText(" ⏶ " + diff);
            retour.setFill(Color.GREEN);
        }
        else {
            diff = -diff;
            retour.setText(" ⏷ " + diff);
            retour.setFill(Color.RED);
        }

        return retour;
    }
}
