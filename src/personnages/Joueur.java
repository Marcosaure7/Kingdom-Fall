package personnages;

import objets.*;
import java.util.HashMap;
import java.util.Map;

public class Joueur extends Entite {

    private Map<Type_Objet, Objet[]> inventaire;

    public Joueur(int niveau) {
        super(niveau);
        inventaire = new HashMap<>();
        for (Type_Objet o: Type_Objet.values()) {
            inventaire.put(o, new Objet[o.getEspace_inventaire()]);
        }
    }

    public void pincer_bouche() {
        ptsVie -= 50;
    }
}
