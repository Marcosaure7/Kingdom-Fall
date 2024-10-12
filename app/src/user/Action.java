package user;

public enum Action {

    NOT_DEFINED("N/A", "N/A"),
    OUVRIR_INVENTAIRE("inventaire", "inv"),
    INFO("info", "i"),
    ATTAQUER("attaquer", "att"),
    OUI("oui", "o"),
    NON("non", "n");

    public final String choix1;
    public final String choix2;

    Action(String choix1, String choix2)
    {
        this.choix1 = choix1;
        this.choix2 = choix2;
    }


    @Override
    public String toString()
    {
        return "(" + choix1 + ":" + choix2 + ") ";
    }

    public boolean correspondreStringAction (String cmd)
    {
        return cmd.equalsIgnoreCase(choix1) || cmd.equalsIgnoreCase(choix2);
    }
}
