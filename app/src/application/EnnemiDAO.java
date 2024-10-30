package application;

import objets.*;
import personnages.Boss;
import personnages.Ennemi;

import java.sql.*;
import java.util.ArrayList;

public class EnnemiDAO {
    private final Connection connection;
    private Donjon donjon;

    public EnnemiDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<Ennemi> recupererEnnemis(Donjon donjon) throws SQLException {
        ArrayList<Ennemi> ennemis = new ArrayList<>();
        this.donjon = donjon;

        // Étape 1 : Récupérer les informations de l'ennemi
        String selectEnnemiSQL = "SELECT * FROM ennemis WHERE donjon = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectEnnemiSQL)) {
            pstmt.setInt(1, donjon.getNiveau());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ennemis.add(new Ennemi(
                        rs.getString("nom"),
                        rs.getInt("ptsVie"),
                        rs.getInt("niveau"),
                        rs.getInt("attaque"),
                        rs.getDouble("poidsSpawn"),
                        rs.getInt("xpDrop"),
                        recupererDropsPourEnnemi(rs.getInt("id"), "ennemi")));
            }
        }
        return ennemis;
    }

    private ArrayList<Objet> recupererDropsPourEnnemi(int ennemiId, String typeEnnemi) throws SQLException {
        ArrayList<Objet> drops = new ArrayList<>();
        String selectDropTypesSQL = "SELECT drop_id, drop_type FROM " + typeEnnemi + "_drops WHERE " + typeEnnemi + "_id = ?";

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
                        String selectInvocationSQL = "SELECT nom, description, drop_rate, boss_id FROM invocation_boss WHERE id = ?";
                        try (PreparedStatement pstmtInvocation = connection.prepareStatement(selectInvocationSQL)) {
                            pstmtInvocation.setInt(1, dropId);
                            ResultSet rsInvocation = pstmtInvocation.executeQuery();

                            if (rsInvocation.next()) {
                                drops.add(new ObjetInvoqueBoss(
                                        rsInvocation.getString("nom"),
                                        rsInvocation.getString("description"),
                                        rsInvocation.getDouble("drop_rate"),
                                        recupererBoss(rsInvocation.getInt("boss_id")),
                                        donjon));
                            }
                        }
                    }
                }
            }
        }
        return Objet.pondererDropRates(drops);
    }

    private Boss recupererBoss(int bossKey) throws SQLException
    {
        String selectBossSQL = "SELECT * FROM bosses WHERE id = ?";
        try (PreparedStatement pstmtInvocation = connection.prepareStatement(selectBossSQL)) {
            pstmtInvocation.setInt(1, bossKey);
            ResultSet rsBoss = pstmtInvocation.executeQuery();
            if (rsBoss.next())
            {
                return new Boss(
                        rsBoss.getString("nom"),
                        rsBoss.getString("description"),
                        rsBoss.getInt("donjon"),
                        rsBoss.getInt("ptsVie"),
                        rsBoss.getInt("attaque"),
                        rsBoss.getInt("xp_drop"),
                        rsBoss.getInt("ptsArmure"),
                        recupererDropsPourEnnemi(bossKey, "boss"));
            }
        }

        return null;
    }
}


