package objets;

import application.Donjon;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import personnages.Boss;
import personnages.Joueur;

import java.io.Serializable;

public class ObjetInvoqueBoss extends Divers implements Serializable
{

    private final Boss bossInvoque;
    private final Donjon donjon;

    public ObjetInvoqueBoss (String nom, String description, double dropRate, Boss bossInvoque, Donjon donjon) {
        super(nom, description, dropRate);
        this.bossInvoque = bossInvoque;
        this.donjon = donjon;
    }

    public ObjetInvoqueBoss (ObjetInvoqueBoss source)
    {
        super(source);
        bossInvoque = source.bossInvoque;
        donjon = source.donjon;
    }

    @Override
    public void utiliser ()
    {
        donjon.bossEnFileDattente(bossInvoque);
    }

    @Override
    public VBox formatComparedDescription(Joueur joueur) {
        VBox vbox = new VBox();
        String[] descriptionSplit = super.getDescription().split("\n");

        for (String s : descriptionSplit) {
            vbox.getChildren().add(new Text(s));
        }

        return vbox;
    }
}