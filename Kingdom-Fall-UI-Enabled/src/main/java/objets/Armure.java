package objets;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import personnages.Joueur;

import java.io.Serializable;

public class Armure extends Objet implements Serializable
{

    private String description;
    private final int capaciteArmure;
    private int ptsArmure;

    public Armure(String nom, String description, int capaciteArmure, double dropRate) {
        super(Type_Objet.ARMURES, nom, dropRate);
        this.capaciteArmure = capaciteArmure;
        if (!description.equals("null")) {
            this.description = description;
        }

        ptsArmure = capaciteArmure;
    }

    public Armure (Armure autre)
    {
        super(autre);
        this.description = autre.description;
        this.capaciteArmure = autre.capaciteArmure;
        this.ptsArmure = autre.ptsArmure;
    }

    public String getDescription() {
        return String.format("%s\n%s\nCapacité : %d\nArmure : %d\nChances de drop : %.2f",
                getNom(), description, capaciteArmure, ptsArmure, getDropRate()*100) + "%";
    }

    public VBox formatComparedDescription(Joueur joueur) {
        VBox vbox = new VBox();
        String[] descriptionSplit = getDescription().split("\n");

        for (int i = 0; i < descriptionSplit.length; i++) {
            if (i != 2 && i != 3 || joueur.getEquip(Type_Objet.ARMURES) == null)
                vbox.getChildren().add(new Text(descriptionSplit[i] + " "));
            else
            {
                TextFlow textFlow = new TextFlow();
                textFlow.getChildren().add(new Text(descriptionSplit[i]));

                if (i == 2)
                    textFlow.getChildren().add(getDifferenceCapaciteArmure((Armure) joueur.getEquip(Type_Objet.ARMURES)));
                else
                    textFlow.getChildren().add(getDifferencePtsArmure((Armure) joueur.getEquip(Type_Objet.ARMURES)));

                vbox.getChildren().add(textFlow);
            }
        }

        return vbox;
    }

    public int getPtsArmure() {
        return ptsArmure;
    }

    public int getCapaciteArmure() {
        return capaciteArmure;
    }

    /**
     * Fait subir l'attaque à l'armure seulement.
     * @param attaque L'attaque reçue à l'entité.
     * @return Les dégâts non absorbés par l'armure.
     */
    public int mangerAttaque(int attaque)
    {
        if (attaque <= ptsArmure)
        {
            ptsArmure -= attaque;
            attaque = 0;
        }
        else
        {
            attaque -= ptsArmure;
            ptsArmure = 0;
        }

        return attaque;
    }

    public void resetArmure()
    {
        ptsArmure = capaciteArmure;
    }

    private Text getDifferenceCapaciteArmure(Armure autre) {
        return getComparisonText(capaciteArmure, autre.capaciteArmure);
    }

    private Text getDifferencePtsArmure(Armure autre) {
        return getComparisonText(ptsArmure, autre.ptsArmure);
    }
}
