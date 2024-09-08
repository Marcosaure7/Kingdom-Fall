package objets;

public abstract class Objet {

    Type_Objet type;
    String nom;

    public Objet(Type_Objet type, String nom) {
        this.type = type;
        this.nom = nom;
    }

    public Type_Objet getType() {
        return type;
    }

    public String getNom() {
        return nom;
    }
}
