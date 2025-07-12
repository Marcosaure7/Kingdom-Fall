using System.Collections;
using personnages;
using objets;
using MySql.Data.MySqlClient;
namespace App;

class EnnemiDAO {
    private Donjon donjon;
    private DatabaseManager dbm;


    public EnnemiDAO(DatabaseManager dbm, Donjon donjon)
    {
        this.dbm = dbm;
        this.donjon = donjon;
    }

    public List<Ennemi> RecupererEnnemis()
    {
        List<Ennemi> ennemis = new List<Ennemi>();
        // Étape 1 : Récupérer les informations de l'ennemi
        string selectEnnemiSQL = $"SELECT * FROM ennemis WHERE donjon = {donjon.niveau}";
        
        dbm.OpenConnection();

        using (MySqlDataReader reader = dbm.ExecuteQuery(selectEnnemiSQL))
        {
            while (reader.Read())
            {
                ennemis.Add(new Ennemi(
                            reader.GetString("nom"),
                            reader.GetInt32("ptsVie"),
                            reader.GetInt32("niveau"),
                            reader.GetInt32("attaque"),
                            reader.GetDouble("poidsSpawn"),
                            reader.GetInt32("xpDrop"),
                            RecupererDropsPourEnnemi(reader.GetInt32("id"), "ennemi", reader.GetString("nom"))));
            }
        }
        

        dbm.CloseConnection();

        return ennemis;
    }


    private List<Objet> RecupererDropsPourEnnemi(int ennemiId, string classeEnnemi, string typeEnnemi)
    {
        List<Objet> drops = new List<Objet>();
        string selectDropTypesSQL = $"SELECT drop_id, drop_type FROM {classeEnnemi}_drops WHERE {classeEnnemi}_id = {ennemiId}";

        dbm.OpenConnection();
        using (var reader = dbm.ExecuteQuery(selectDropTypesSQL))
        {
            while (reader.Read())
            {
                int dropId = reader.GetInt32("drop_id");
                string dropType = reader.GetString("drop_type");
                dbm.OpenConnection();

                switch (dropType)
                {
                    case "arme":
                        string selectArmeSQL = $"SELECT nom, description, drop_rate, degats, effetStatut FROM armes WHERE id = '{dropId}'";
                        using (var armeReader = dbm.ExecuteQuery(selectArmeSQL))
                        {
                            if (armeReader.Read())
                            {
                                drops.Add(new Arme(
                                            armeReader.GetString("nom"),
                                            armeReader.GetString("description"),
                                            armeReader.GetDouble("drop_rate"),
                                            armeReader.GetInt32("degats"),
                                            armeReader.GetString("effetStatut")));
                            }
                        }
                        break;

                    case "armure":
                        string selectArmureSQL = $"SELECT nom, description, drop_rate, ptsArmure FROM armures WHERE id = {dropId}";
                        using (var armureReader = dbm.ExecuteQuery(selectArmureSQL))
                        {
                            if (armureReader.Read())
                            {
                                drops.Add(new Armure(
                                            armureReader.GetString("nom"),
                                            armureReader.GetString("description"),
                                            armureReader.GetInt32("ptsArmure"),
                                            armureReader.GetDouble("drop_rate")));
                            }
                        }
                        break;

                    case "potion":
                        string selectPotionSQL = $"SELECT nom, soin, drop_rate FROM potions WHERE id = {dropId}";

                        using (var potionReader = dbm.ExecuteQuery(selectPotionSQL))
                        {
                            if (potionReader.Read())
                            {
                                drops.Add(new Potion(
                                            potionReader.GetString("nom"),
                                            potionReader.GetInt32("soin"),
                                            potionReader.GetDouble("drop_rate")));
                            }
                        }
                        break;

                    case "divers":
                        string selectInvocationSQL = $"SELECT nom, description, drop_rate, boss_id FROM invocation_boss WHERE id = {dropId}";
                        using (var invocationReader = dbm.ExecuteQuery(selectInvocationSQL))
                        {
                            if (invocationReader.Read())
                            {
                                drops.Add(new ObjetInvoqueBoss(
                                            invocationReader.GetString("nom"),
                                            invocationReader.GetString("description"),
                                            invocationReader.GetDouble("drop_rate"),
                                            RecupererBoss(invocationReader.GetInt32("boss_id")),
                                            donjon));

                                dbm.CloseConnection();
                            }
                        }
                        break;
                }
                dbm.CloseConnection();
            }
        
        }
        dbm.CloseConnection();
        return Objet.PondererDropRates(drops, typeEnnemi);
    }

    private Boss RecupererBoss(int bossKey)
    {
        string selectBossSQL = $"SELECT * FROM bosses WHERE id = {bossKey}";
        dbm.OpenConnection();


        using var reader = dbm.ExecuteQuery(selectBossSQL);
        if (reader.Read())
        {
            return new Boss(
                    reader.GetString("nom"),
                    reader.GetString("description"),
                    reader.GetInt32("donjon"),
                    reader.GetInt32("ptsVie"),
                    reader.GetInt32("attaque"),
                    reader.GetInt32("xp_drop"),
                    reader.GetInt32("ptsArmure"),
                    RecupererDropsPourEnnemi(bossKey, "boss", reader.GetString("nom")));
        }

        dbm.CloseConnection();
        throw new KeyNotFoundException($"Boss avec ID {bossKey} non trouvé dans la base de données.");


    }
}
