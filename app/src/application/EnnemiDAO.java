package application;

import objets.*;
import personnages.Ennemi;

import java.sql.*;
import java.util.ArrayList;

public class EnnemiDAO {
    private Connection connection;

    public EnnemiDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<Ennemi> recupererEnnemis(int donjon) throws SQLException {
        ArrayList<Ennemi> ennemis = new ArrayList<>();

        // Étape 1 : Récupérer les informations de l'ennemi
        String selectEnnemiSQL = "SELECT * FROM ennemis WHERE donjon = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectEnnemiSQL)) {
            pstmt.setInt(1, donjon);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ennemis.add(new Ennemi(
                        rs.getString("nom"),
                        rs.getInt("ptsVie"),
                        rs.getInt("niveau"),
                        rs.getInt("attaque"),
                        rs.getDouble("poidsSpawn"),
                        rs.getInt("xpDrop"),
                        recupererDropsPourEnnemi(rs.getInt("id"))));
            }
        }
        return ennemis;
    }

    private ArrayList<Objet> recupererDropsPourEnnemi(int ennemiId) throws SQLException {
        ArrayList<Objet> drops = new ArrayList<>();
        String selectDropTypesSQL = "SELECT drop_id, drop_type FROM ennemi_drops WHERE ennemi_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(selectDropTypesSQL)) {
            pstmt.setInt(1, ennemiId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int dropId = rs.getInt("drop_id");
                String dropType = rs.getString("drop_type");

                switch (dropType) {
                    case "arme" -> {
                        // Préparer le statement pour récupérer les informations d'arme
                        String selectArmeSQL = "SELECT nom, description, drop_rate, degats, effetStatut FROM armes WHERE id = ?";
                        try (PreparedStatement pstmtArme = connection.prepareStatement(selectArmeSQL)) {
                            pstmtArme.setInt(1, dropId);
                            ResultSet rsArme = pstmtArme.executeQuery();

                            if (rsArme.next()) {
                                drops.add(new Arme(
                                        rsArme.getString("nom"),
                                        rsArme.getString("description"),
                                        rsArme.getDouble("drop_rate"),
                                        rsArme.getInt("degats"),
                                        rsArme.getString("effetStatut")));
                            }
                        }
                    }
                    case "armure" -> {
                        // Préparer le statement pour récupérer les informations d'armure
                        String selectArmureSQL = "SELECT nom, description, drop_rate, ptsArmure FROM armures WHERE id = ?";
                        try (PreparedStatement pstmtArmure = connection.prepareStatement(selectArmureSQL)) {
                            pstmtArmure.setInt(1, dropId);
                            ResultSet rsArmure = pstmtArmure.executeQuery();

                            if (rsArmure.next()) {
                                drops.add(new Armure(
                                        rsArmure.getString("nom"),
                                        rsArmure.getString("description"),
                                        rsArmure.getInt("ptsArmure"),
                                        rsArmure.getDouble("drop_rate")));
                            }
                        }
                    }
                    case "potion" -> {
                        // Préparer le statement pour récupérer les informations de potion
                        String selectPotionSQL = "SELECT nom, soin, drop_rate FROM potions WHERE id = ?";
                        try (PreparedStatement pstmtPotion = connection.prepareStatement(selectPotionSQL)) {
                            pstmtPotion.setInt(1, dropId);
                            ResultSet rsPotion = pstmtPotion.executeQuery();

                            if (rsPotion.next()) {
                                drops.add(new Potion(
                                        rsPotion.getString("nom"),
                                        rsPotion.getInt("soin"),
                                        rsPotion.getDouble("drop_rate")));
                            }
                        }
                    }
                    case "divers" -> {
                        // Préparer le statement pour récupérer les informations de divers
                        String selectDiversSQL = "SELECT nom, description, drop_rate FROM divers WHERE id = ?";
                        try (PreparedStatement pstmtDivers = connection.prepareStatement(selectDiversSQL)) {
                            pstmtDivers.setInt(1, dropId);
                            ResultSet rsDivers = pstmtDivers.executeQuery();

                            if (rsDivers.next()) {
                                drops.add(new Divers(
                                        rsDivers.getString("nom"),
                                        rsDivers.getString("description"),
                                        rsDivers.getDouble("drop_rate")));
                            }
                        }
                    }
                }
            }
        }
        return Objet.pondererDropRates(drops);
    }
}


