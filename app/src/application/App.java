package application;

public class App {

    public static final String LIGNE = "***********************************************************************************************************";
    public static final String CMD_INCONNUE = "La commande n'a pas ete reconnue !";

    public static void main(String[] args) {
        try {
            System.out.println(LIGNE + "\nBienvenue!\n" + LIGNE);
            new Jeu();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }
}

