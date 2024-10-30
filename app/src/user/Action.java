package user;

public enum Action {

    NOT_DEFINED(),
    OUVRIR_INVENTAIRE("inventaire", "inv"),
    OUVRIR_MENU("menu", "m"),
    INFO("info", "i"),
    ATTAQUER("attaquer", "att"),
    OUI("oui", "o"),
    NON("non", "n"),
    QUITTER("quitter", "q");

    public final String[] choix;

    Action(String... choix)
    {
        this.choix = choix;
    }


    @Override
    public String toString()
    {
        StringBuilder retour = new StringBuilder("(");
        for (String s: choix) retour.append(s).append(":");

        retour.delete(retour.length() - 1, retour.length()); // Enlève les derniers ", " pour closer avec le dernier élément
        retour.append(")");

        return retour.toString();
    }

    public boolean correspondreStringAction (String cmd)
    {
        for (String s: choix)
        {
            if (cmd.equalsIgnoreCase(s))
                return true;
        }
        return false;
    }
}
