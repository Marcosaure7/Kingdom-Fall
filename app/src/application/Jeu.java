package application;

import objets.Arme;
import personnages.Ennemi;
import personnages.Joueur;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Jeu {

    private DatabaseManager dbm;
    private Joueur joueur;
    private int donjon = 0;
    private ArrayList<Ennemi> ennemis;
    private ArrayList<Arme> armes;
    private Scanner sc;

    public Jeu() throws InterruptedException {
        dbm = new DatabaseManager();
        joueur = new Joueur(100, 0, 1);
        int nbTours = 0;
        loadNouveauDonjon(0);

        sc = new Scanner(System.in);
        premierTour();
        /* Boucle de jeu
         * Version très prématurée
         */
        while (donjon == 1) {
            nbTours++;
            System.out.printf("\n%sTour %d\n", App.LIGNE, nbTours);
            new Tour(joueur, nbTours % 2 == 1);
        }
    }

    private void premierTour() throws InterruptedException {
        System.out.println("Voici le tour d'introduction à Kingdom Fall !");
        System.out.println("Appuyer sur Entrée pour passer au prochain dialogue...");
        sc.nextLine();
        System.out.println("Un ennemi va apparaître sous peu pour vous montrer les bases.\n" + App.LIGNE);
        Thread.sleep(2000);
        System.out.println(ennemis.get(0) + "\n" + App.LIGNE);
        Thread.sleep(2000);
        System.out.println("Attaquer:att \t Inventaire:inv \t ");

    }

    private void loadNouveauDonjon(int donjon) {

        // On load les ennemis du donjon
        ennemis = new ArrayList<>();
        try
        {
            dbm.executerLecture(
                    "SELECT * FROM ennemis WHERE donjon = ?",
                    donjon,
                    rs -> {
                       while (rs.next()) {
                           Ennemi en = new Ennemi(
                                   rs.getString("nom"),
                                   rs.getInt("ptsVie"),
                                   rs.getInt("niveau"),
                                   rs.getInt("attaque"),
                                   rs.getInt("poidsSpawn"));

                           ennemis.add(en);
                       }
                       return null;
                    }
                    );
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // On load les armes du donjon
        armes = new ArrayList<>();
        try
        {
            dbm.executerLecture(
                    "SELECT * FROM armes WHERE donjon = ?",
                    donjon,
                    rs -> {
                       while (rs.next()) {
                           Arme arme = new Arme(
                                   rs.getString("nom"),
                                   rs.getInt("degats"),
                                   rs.getString("effetStatut"));

                           armes.add(arme);
                       }
                       return null;
                    }
                    );
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
