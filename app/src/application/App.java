package application;

import objets.Arme;
import personnages.Ennemi;

public class App {

    public static final String LIGNE = "***********************************************************************************************************";

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

