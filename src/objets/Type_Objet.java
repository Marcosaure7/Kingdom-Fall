package objets;

public enum Type_Objet {

    ARME(5),
    ARMURE(5),
    POTION(5),
    DIVERS(100);

    private final int espace_inventaire;

    Type_Objet(int espace_inventaire) {
        this.espace_inventaire = espace_inventaire;
    }

    public int getEspace_inventaire() {
        return espace_inventaire;
    }
}
