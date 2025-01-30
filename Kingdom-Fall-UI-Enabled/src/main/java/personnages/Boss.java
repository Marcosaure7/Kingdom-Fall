package personnages;

import objets.Objet;
import java.util.ArrayList;

public class Boss extends Ennemi {

    private final int ptsArmure;
    private final int donjonDebloque;
    private final String description;

    public Boss (String nom, String description, int niveau, int ptsVie, int att, int xpDrop, int ptsArmure, ArrayList<Objet> drops)
    {
        super(nom, ptsVie, niveau, att, 0, xpDrop, drops);
        this.ptsArmure = ptsArmure;
        donjonDebloque = niveau + 1;
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public int getDonjonDebloque() {
        return donjonDebloque;
    }

}
