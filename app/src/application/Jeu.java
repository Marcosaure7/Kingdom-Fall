package application;

import objets.Arme;
import personnages.Ennemi;
import personnages.Joueur;

import java.sql.SQLException;
import java.util.ArrayList;

public class Jeu {

    private DatabaseManager dbm;
    private Joueur joueur;
    private int donjon = 0;
    private ArrayList<Ennemi> ennemis;
    private ArrayList<Arme> armes;

    public Jeu() {
        dbm = new DatabaseManager();
        joueur = new Joueur(100, 0, 1);
        int nbTours = 0;
        loadNouveauDonjon(0);

        /* Boucle de jeu
         * Version très prématurée
         */
        while (true) {
            nbTours++;
            System.out.printf("\n%sTour %d\n", App.LIGNE, nbTours);
            new Tour(joueur, nbTours % 2 == 1);
        }
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
