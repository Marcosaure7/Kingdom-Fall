package application;

import personnages.Ennemi;

public class App {

    private static final String LIGNE = "***********************************************************************************************************";

    public static void main(String[] args) {

        System.out.println(LIGNE + "\nBienvenue!\n" + LIGNE);
        Jeu jeuCourant = new Jeu();
        Ennemi ennemi = jeuCourant.genererEnnemi(1);
        System.out.flush();
        System.out.println(ennemi.getNom());
        System.out.println(ennemi.getPtsVie());
    }


}

