package objets;

public enum Type_Objet {

    ARME(5),
    ARMURE(5),
    POTION(5),
    DIVERS(100);

    private final int espaceInventaire;

    Type_Objet(int espaceInventaire) {
        this.espaceInventaire = espaceInventaire;
    }

    public int getEspaceInventaire() {
        return espaceInventaire;
    }
}
