package objets;

public class Armure extends Objet {

    private String description;
    private final int ptsArmure;

    public Armure(String nom, String description, int ptsArmure, double dropRate) {
        super(Type_Objet.ARMURES, nom, dropRate);
        this.ptsArmure = ptsArmure;
        if (!description.equals("null")) {
            this.description = description;
        }
    }

    public Armure (Armure autre)
    {
        super(autre);
        this.description = autre.description;
        this.ptsArmure = autre.ptsArmure;
    }

    public String getDescription() {
        return String.format("%s\n%s\nArmure : %d\nChances de drop : %.2f",
                getNom(), description, ptsArmure, getDropRate()*100) + "%";
    }
}
