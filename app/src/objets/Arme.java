package objets;

import java.util.ArrayList;

public class Arme extends Objet {

    private int degats;
    private EffetStatut effet;
    private String description;

    public Arme(String nom, String description, double dropRate, int degats, String effetStatut) {
        super(Type_Objet.ARME, nom, dropRate);
        this.degats = degats;
        setEffetStatutFromString(effetStatut);
        this.description = genererDescription(description);
    }

    public static void pondererDropRates(ArrayList<Arme> armes) {
        double totalDropRates = 0;
        for (Arme arme : armes) {
            totalDropRates += arme.getDropRate();
        }
        for (Arme arme : armes) {
            arme.dropRate /= totalDropRates;
        }
    }

    private String genererDescription(String description) {
        return String.format("%s\n%s\nDégâts : %d\nEffet de statut : %s\nChances de drop : %.2f",
                getNom(), description, degats, effet, getDropRate()*100) + "%";
    }

    public void setEffetStatutFromString(String effetStatut) {
        effet = EffetStatut.valueOf(effetStatut.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("%s", getNom());
    }

    public String getDescription() {
        return description;
    }
}
