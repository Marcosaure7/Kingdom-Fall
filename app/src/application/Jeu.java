package application;

import objets.Arme;
import objets.EffetStatut;
import org.jetbrains.annotations.NotNull;
import personnages.Ennemi;
import personnages.Joueur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.Math.random;

public class Jeu {

    private DatabaseManager dbm;
    private ArrayList<Arme> banqueArmes;
    private ArrayList<Ennemi> banqueEnnemis;
    private Joueur joueur;

    public Jeu() {
        dbm = new DatabaseManager();
        joueur = new Joueur(0);
        // banqueEnnemis = genererEnnemis();
        // banqueArmes = genererBanqueArmes();
    }

//    @NotNull
//    private ArrayList<Arme> genererBanqueArmes() {
//        ArrayList<Arme> armeArrayList = new ArrayList<>();
//        armeArrayList.add(new Arme("Gourdin", 1, null));
//        armeArrayList.add(new Arme("Hache", 0, EffetStatut.SAIGNEMENT));
//        return armeArrayList;
//    }

    public Ennemi genererEnnemi(int id) {
        Ennemi nouvelEnnemi = null;
        try (Connection conn = dbm.getDataSource().getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ennemis WHERE id = ?");             stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    nouvelEnnemi = new Ennemi(rs.getString("nom"),
                            rs.getInt("niveau"), rs.getInt("poidsSpawn"));
                }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return nouvelEnnemi;
    }

    @NotNull
    private ArrayList<Ennemi> genererEnnemis() {
        ArrayList<Ennemi> ennemiArrayList = new ArrayList<>();
        ennemiArrayList.add(new Ennemi("Squelette", joueur.getNiveau(), 5));
        ennemiArrayList.add(new Ennemi("Ogre", joueur.getNiveau(), 2));
        return ennemiArrayList;
    }


    private Ennemi spawnerProchainEnnemi() {
        int probabiliteTotale = 0;
        for (Ennemi en : banqueEnnemis) {
            probabiliteTotale += en.getPoidsSpawn();
        }

        double r = random() * probabiliteTotale;
        double somme_actuelle = 0;
        for (Ennemi en : banqueEnnemis) {
            somme_actuelle += en.getPoidsSpawn();
            if (somme_actuelle >= r)
                return en;
        }

        return null;
    }
}
