package objets;

public class Potion extends Objet {
    private final int soin;
    private final String description;

    public Potion (String nom, int soin, double drop_rate) {
        super(Type_Objet.POTIONS, nom, drop_rate);
        this.description = String.format("Soigne de %d PV", soin);
        this.soin = soin;
    }

    public Potion (Potion autre)
    {
        super(autre);
        this.description = autre.description;
        this.soin = autre.soin;
    }

    public int getSoin() {
        return soin;
    }

    public String getDescription() {
        return description;
    }

}
