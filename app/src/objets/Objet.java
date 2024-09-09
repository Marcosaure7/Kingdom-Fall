package objets;

public abstract class Objet extends Drops {

    protected Type_Objet type;
    protected String nom;
    protected double dropRate;

    public Objet(Type_Objet type, String nom, double dropRate) {
        this.type = type;
        this.nom = nom;
        this.dropRate = dropRate;
    }

    public Type_Objet getType() {
        return type;
    }

    public String getNom() {
        return nom;
    }

    public double getDropRate() {
        return dropRate;
    }

    @Override
    public abstract String toString();

    public abstract String getDescription();
}
