package objets;

public enum Type_Objet {

    ARMES(5, true),
    ARMURES(5, true),
    POTIONS(5, false),
    DIVERS(10, false);

    private final int espaceInventaire;
    private final boolean equipable;

    Type_Objet(int espaceInventaire, boolean equipable) {
        this.espaceInventaire = espaceInventaire;
        this.equipable = equipable;
    }

    public int getEspaceInventaire() {
        return espaceInventaire;
    }

    public boolean isEquipable() {
        return equipable;
    }
}
