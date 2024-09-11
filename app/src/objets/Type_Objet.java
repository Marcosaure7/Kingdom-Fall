package objets;

public enum Type_Objet {

    ARMES(5),
    ARMURES(5),
    POTIONS(5),
    DIVERS(100);

    private final int espaceInventaire;

    Type_Objet(int espaceInventaire) {
        this.espaceInventaire = espaceInventaire;
    }

    public int getEspaceInventaire() {
        return espaceInventaire;
    }
}
