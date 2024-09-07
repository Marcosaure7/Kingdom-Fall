package objets;

public class Arme extends Objet {

    int degats;
    EffetStatut effet;

    public Arme(String nom, int degats, String effetStatut) {
        super(Type_Objet.ARME, nom);
        this.degats = degats;
        setEffetStatutFromString(effetStatut);
    }

    public void setEffetStatutFromString(String effetStatut) {
        effet = EffetStatut.valueOf(effetStatut);
    }
}
