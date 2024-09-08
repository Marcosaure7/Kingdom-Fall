package application;

import objets.Arme;
import personnages.Ennemi;

public class App {

    public static final String LIGNE = "***********************************************************************************************************";

    public static void main(String[] args) {

        System.out.println(LIGNE + "\nBienvenue!\n" + LIGNE);
        new Jeu();
    }


}

