package objets;

public abstract class Divers extends Objet {

    private final String description;

    public Divers (String nom, String description, double dropRate)
    {
        super (Type_Objet.DIVERS, nom, dropRate);
        this.description = description;
    }

    public Divers (Divers autre)
    {
        super (autre);
        this.description = autre.description;
    }

    @Override
    public String getDescription() {
        return String.format("%s%n%s%nChances de drop : %.2f%n", nom, description, dropRate);
    }

    public void utiliser () {}
}
