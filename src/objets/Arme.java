package objets;

public class Arme extends Objet {

    int degats;
    EffetStatut effet;

    public Arme(String nom, int degats, EffetStatut effet) {
        super(Type_Objet.ARME, nom);
        this.degats = degats;
        this.effet = effet;
    }
}
