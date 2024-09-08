package personnages;

import exceptions.InventairePleinException;
import objets.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Joueur extends Entite {

    // La Map contient comme clé un type d'objet et comme valeur la liste des objets de ce type dans l'inventaire.
    private Map<Type_Objet, ArrayList<Objet>> inventaire;

    public Joueur(int ptsVie, int niveau, int attBase) {
        super(ptsVie, niveau, attBase);
        inventaire = new HashMap<>();
        for (Type_Objet o: Type_Objet.values()) {
            inventaire.put(o, new ArrayList<>());
        }
    }

    public void ramasserObjet(@NotNull Objet objet) throws InventairePleinException {
        // Check si inventaire disponible
        if (inventaire.get(objet.getType()).size() < objet.getType().getEspaceInventaire()) {
            // Il y a de la place, on ajoute l'objet :
            inventaire.get(objet.getType()).add(objet);
        }
        else {
            throw new InventairePleinException("Inventaire plein!");
        }
    }

    public void pincer_bouche() {
        ptsVie -= 50;
    }
}
