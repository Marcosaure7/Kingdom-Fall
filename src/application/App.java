package application;

import objets.Arme;
import personnages.*;

import java.util.ArrayList;

public class App {

    private static final String LIGNE = "***********************************************************************************************************";
    private ArrayList<Arme> banqueArmes = genererBanqueArmes();

    public static void main(String[] args) {

        Joueur joueur = new Joueur(0);
        System.out.println(LIGNE + "\nBienvenue!\n" + LIGNE);



    }

    private ArrayList<Arme> genererBanqueArmes() {
        Arme gourdin = new Arme();
        return banqueArmes;
    }
}
