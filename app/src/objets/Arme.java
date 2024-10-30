package objets;

import java.util.ArrayList;

public class Arme extends Objet {

    private int degats;
    private EffetStatut effet;
    private String description = "";

    public Arme(String nom, String description, double dropRate, int degats, String effetStatut) {
        super(Type_Objet.ARMES, nom, dropRate);
        this.degats = degats;
        setEffetStatutFromString(effetStatut);
        if (!description.equals("null")) {
            this.description = description;
        }
    }


    public Arme (Arme autre)
    {
        super(autre);
        this.degats = autre.degats;
        this.effet = autre.effet;
        this.description = autre.description;
    }

    public void setEffetStatutFromString(String effetStatut) {
        effet = EffetStatut.valueOf(effetStatut.toUpperCase());
    }

    public String getDescription() {
        return String.format("%s\n%s\nDegats : %d\nEffet de statut : %s\nChances de drop : %.2f",
                getNom(), description, degats, effet, getDropRate()*100) + "%";
    }

    public int getDegats() {
        return degats;
    }

    public EffetStatut getEffet () {return effet;}
}
