package objets;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import personnages.Joueur;

import java.io.Serializable;

public class Armure extends Objet implements Serializable
{

    private String description;
    private final int ptsArmure;

    public Armure(String nom, String description, int ptsArmure, double dropRate) {
        super(Type_Objet.ARMURES, nom, dropRate);
        this.ptsArmure = ptsArmure;
        if (!description.equals("null")) {
            this.description = description;
        }
    }

    public Armure (Armure autre)
    {
        super(autre);
        this.description = autre.description;
        this.ptsArmure = autre.ptsArmure;
    }

    public String getDescription() {
        return String.format("%s\n%s\nArmure : %d\nChances de drop : %.2f",
                getNom(), description, ptsArmure, getDropRate()*100) + "%";
    }

    public VBox formatComparedDescription(Joueur joueur) {
        VBox vbox = new VBox();
        String[] descriptionSplit = getDescription().split("\n");

        for (int i = 0; i < descriptionSplit.length; i++) {
            if (i != 2 || joueur.getEquip(Type_Objet.ARMURES) == null)
                vbox.getChildren().add(new Text(descriptionSplit[i] + " "));
            else
            {
                TextFlow textFlow = new TextFlow();
                textFlow.getChildren().add(new Text(descriptionSplit[i]));
                textFlow.getChildren().add(getDifferenceArmure((Armure) joueur.getEquip(Type_Objet.ARMURES)));
                vbox.getChildren().add(textFlow);
            }
        }

        return vbox;
    }

    private Text getDifferenceArmure(Armure autre) {
        return getComparisonText(ptsArmure, autre.ptsArmure);
    }
}
