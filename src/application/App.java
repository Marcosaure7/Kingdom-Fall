package application;

import objets.Arme;
import objets.EffetStatut;
import personnages.*;

import java.util.ArrayList;

public class App {

    private static final String LIGNE = "***********************************************************************************************************";
    private static ArrayList<Arme> banqueArmes = genererBanqueArmes();
    private static ArrayList<Ennemi> banqueEnnemis = genererEnnemis();
    private static Joueur joueur = new Joueur(0);

    public static void main(String[] args) {

        System.out.println(LIGNE + "\nBienvenue!\n" + LIGNE);

        Ennemi ennemiActuel = spawnerProchainEnnemi();
        System.out.println("Attention, votre premier ennemi!");

    }

    private static ArrayList<Arme> genererBanqueArmes() {
        ArrayList<Arme> armeArrayList = new ArrayList<>();
        armeArrayList.add(new Arme("Gourdin", 1, null));
        armeArrayList.add(new Arme("Hache", 0, EffetStatut.SAIGNEMENT));
        return armeArrayList;
    }

    private static ArrayList<Ennemi> genererEnnemis() {
        ArrayList<Ennemi> ennemiArrayList = new ArrayList<>();
        ennemiArrayList.add(new Ennemi("Squelette", joueur.getNiveau(), 5));
        ennemiArrayList.add(new Ennemi("Ogre", joueur.getNiveau(), 2));
        return ennemiArrayList;
    }

    private static Ennemi spawnerProchainEnnemi() {

        return
    }
}

