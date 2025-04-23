package objets;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import personnages.Joueur;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Arme extends Objet implements Serializable
{
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
        return String.format("%s\n%s\nDégâts : %d\nEffet de statut : %s\nChances de drop : %.2f",
                getNom(), description, degats, effet, getDropRate()*100) + "%";
    }

    @Override
    public VBox formatComparedDescription(Joueur joueur)
    {
        VBox vBox = new VBox();
        String[] descriptionSplit = getDescription().split("\n");

        for (int i = 0; i < descriptionSplit.length; i++) {
            if (i != 2) {
                vBox.getChildren().add(new Label(descriptionSplit[i] + "\n"));
            }
            else {
                TextFlow textFlow = new TextFlow();
                textFlow.getChildren().add(new Text(descriptionSplit[i] + " "));
                textFlow.getChildren().add(getDifferenceDegats((Arme) joueur.getEquip(Type_Objet.ARMES)));
                vBox.getChildren().add(textFlow);
            }
        }
        return vBox;
    }

    private Text getDifferenceDegats(Arme autre) {
        return getComparisonText(degats, autre.degats);
    }

    public int getDegats() {
        return degats;
    }

    public EffetStatut getEffet () {return effet;}
}
