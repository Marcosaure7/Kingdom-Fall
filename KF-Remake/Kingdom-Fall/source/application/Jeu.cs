using Avalonia.Threading;

namespace App;

using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using controllers;
using Microsoft.CSharp.RuntimeBinder;
using MySql.Data.MySqlClient;
using objets;
using personnages;

public class Jeu
{
    private DatabaseManager? dbm;
    private readonly controllers.FenetreAppController controller;
    private readonly GameLogic gameLogic;
    public Joueur? joueur { get; private set; }
    public int numDonjon = 0;
    private List<Donjon> donjonsDebloques = new List<Donjon>(5);

    public Ennemi? ennemiCourant;

    private List<Ennemi> ennemis = new List<Ennemi>();
    Dictionary<string, Drops>? dropsCourants;

    private Random randGen = new Random();

    public Jeu(controllers.FenetreAppController controller, GameLogic gameLogic)
    {
        this.controller = controller;
        this.gameLogic = gameLogic;
    }

    public async Task LancerJeu()
    {
        dbm = new DatabaseManager();
        joueur = new Joueur(this, 100, 0, 1);
        LoadNouveauDonjon(0);
        await PremierTourUI();
        LoadNouveauDonjon(1);

        while (true)
        {
            donjonsDebloques[numDonjon].NewTour();
            await gameLogic.AttendreFinTour(); // TODO À changer !!!!!!
        }
    }

    private async Task PremierTourUI()
    {
        ennemiCourant = ennemis[0];

        try
        {
            controller.EnvoyerMessage("Voici le tour d'introduction.");
            Thread.Sleep(1000);

            controller.EnvoyerMessage("Un ennemi va apparaître pour vous montrer les bases.");
            Thread.Sleep(2000);

            controller.AfficherEnnemi(ennemiCourant);
            Thread.Sleep(1000);

            controller.EnvoyerMessage("Appuyer sur 'attaquer' pour mettre un terme à ce squelette.");
            controller.ActiverNode("attaquer");

            await gameLogic.AttendreFinTour();

            Thread.Sleep(1000);
            controller.EnvoyerMessage("Maintenant que vous avez une nouvelle arme, nous allons naviguer l'inventaire pour l'équiper. Appuyez sur le bouton 'Inventaire'.");
            controller.ActiverNode("inventaire");

            Thread.Sleep(6000);
            controller.EnvoyerMessage("Parfait, vous êtes maintenant prêts à jouer !");

            Thread.Sleep(2000);
            controller.EnvoyerMessage("En situation difficile, n'oubliez pas de vous soigner! Un soin rapide peut être fait à l'aide du bouton correspondant.");

            Thread.Sleep(500);
            controller.EnvoyerMessage("Voici 5 potions de soin pour bien débuter !");
            DonnerPotions("Soin basique (1)", 5);

            controller.EnvoyerMessage("Chargement du donjon...");
        }
        catch (ThreadInterruptedException e)
        {
            throw new ThreadInterruptedException(e.Message);
        }
    }

    private void DonnerPotions(string nomPotion, int quantite)
    {
        if (dbm == null)
            throw new exceptions.KFException("La base de données n'est pas initialisée.");

        Potion potionADonner;

        string selectPotion = $"SELECT * FROM potions WHERE nom = '{nomPotion}'";
        dbm.OpenConnection();
        using (MySqlDataReader reader = dbm.ExecuteQuery(selectPotion))
        {
            if (reader.Read())
            {
                potionADonner = new Potion(
                    nomPotion,
                    reader.GetInt32("soin"),
                    reader.GetDouble("drop_rate"));
            }
            else
            {
                throw new KeyNotFoundException($"Potion '{nomPotion}' non trouvée dans la base de données.");
            }
        }

        dbm.CloseConnection();

        for (int i = 0; i < quantite; i++)
        {
            joueur?.inventaire.RamasserObjet(potionADonner);
        }
        
        FenetreAppController.GetSingleton().ActiverNode("soin rapide");
        FenetreAppController.GetSingleton().SetNbPotions(5);
    }

    public Objet GetDropCourant()
    {
        if (dropsCourants == null)
            throw new exceptions.KFException("Aucun drop courant défini.");

        return (Objet)dropsCourants["Objet"];
    }

    public void EffectuerAttaque()
    {
        if (ennemiCourant == null)
            throw new exceptions.KFException("Aucun ennemi courant défini.");

        if (joueur == null)
            throw new exceptions.KFException("Aucun joueur défini.");

        controller.EnvoyerMessage(ennemiCourant.seFaitAttaquer(joueur.att, joueur.GetEffetArme()));
        controller.AfficherAttaquer(ennemiCourant);

        if (ennemiCourant.estMort())
            EnnemiVaincu(ennemiCourant);
        else
        {
            controller.EnvoyerMessage(joueur.seFaitAttaquer(ennemiCourant.attBase, EffetStatut.Aucun));
            controller.AfficherAttaquer(joueur);
        }
        if (!controllers.OptionsController.DEV_MODE && joueur.estMort())
            Dispatcher.UIThread.Post(() => controller.JeuTermine());
    }

    private void EnnemiVaincu(Ennemi ennemiCourant)
    {

        Console.WriteLine($"EnnemiVaincu({ennemiCourant.nom})");

        if (ennemiCourant == null)
            throw new exceptions.KFException("L'ennemi courant est null.");

        if (joueur == null)
            throw new exceptions.KFException("Le joueur est null.");

        joueur.ennemiVaincu();
        controller.ResetArmure(joueur.ptsArmure); 
        controller.EnvoyerMessage($"Vous avez vaincu {ennemiCourant.nom} !");
        dropsCourants = GenererDrops(ennemiCourant);
        controller.EnvoyerMessage($"Il a lâché : {dropsCourants["Objet"]}, {dropsCourants["XP"]}.");
        int ancienNiveau = joueur.niveau;
        joueur.GainXp((Exp)dropsCourants["XP"]);
        controller.GainXp(joueur, ancienNiveau, ((Exp)dropsCourants["XP"]).valeur);
        FenetreAppController.GetSingleton().ShowDrops(ennemiCourant, (Objet)dropsCourants["Objet"], joueur);

        if (ennemiCourant is Boss bossActuel) BossVaincu(bossActuel);
    }

    void BossVaincu(Boss bossActuel)
    {
        bool donjonDejaDebloque = false;

        // On regarde si le donjon a déjà été débloqué
        foreach (Donjon d in donjonsDebloques)
        {
            if (d.niveau == bossActuel.DonjonDebloque)
            {
                donjonDejaDebloque = true;
                break;
            }
        }
        if (!donjonDejaDebloque)
        {
            controller.EnvoyerMessage($"Vous avez vaincu le/la {bossActuel.nom} qui hantait ce donjon. C'est tout a votre honneur.\n" +
                            $"Vous venez de debloquer le donjon de niveau {bossActuel.DonjonDebloque}.");
            controller.EnvoyerMessage("Vous voyagez vers le prochain donjon...");

            LoadNouveauDonjon(bossActuel.DonjonDebloque);
        }
        else
        {
            controller.EnvoyerMessage($"Le donjon {bossActuel.DonjonDebloque} est déjà débloqué.");
        }
    }

    public void Ramasser()
    {
        if (dropsCourants == null)
            throw new exceptions.KFException("Rien à ramasser, dropsCourants est null.");

        Objet drop = (Objet)dropsCourants["Objet"];
        Ramasser(drop);
    }

    public void Ramasser(Objet drop)
    {
        if (drop is null)
            throw new exceptions.KFException("Le drop à ramasser est null.");
        if (joueur == null)
            throw new exceptions.KFException("Le joueur est null.");

        joueur.inventaire.RamasserObjet(drop);

        // Si le joueur ramasse une potion, alors le soin rapide devient disponible.
        if (drop is Potion)
            controller.SetNbPotions(joueur.inventaire.GetListType(TypeObjet.Potions).Capacity);

        controller.EnvoyerMessage($"{drop.nom} ajouté à l'inventaire !");
    }

    public void Equiper()
    {
        if (dropsCourants == null)
            throw new exceptions.KFException("Rien à équiper, dropsCourants est null.");
        if (joueur == null)
            throw new exceptions.KFException("Le joueur est null.");

        Objet drop = (Objet)dropsCourants["Objet"];
        Ramasser(drop);

        var (ind, equip) = joueur.inventaire.InvContient(drop);
        joueur.inventaire.Equiper(drop, ind);

        controller.EnvoyerMessage($"{drop.nom} équipé !");
    }

    Dictionary<string, Drops> GenererDrops(Ennemi ennemiActuel)
    {

        Console.WriteLine($"GenererDrops({ennemiActuel.nom})");

        if (ennemiActuel is null)
            throw new exceptions.KFException("L'ennemi est null.");

        Dictionary<string, Drops> drops = new Dictionary<string, Drops>();

        // Générer le drop d'objet
        Objet objetChoisi = null;

        double r = randGen.NextDouble();
        double sommeAccumul = 0;
        List<Objet> objets = ennemiActuel.drops;

        foreach (Objet objet in objets)
        {
            sommeAccumul += objet.dropRate;
            if (r <= sommeAccumul)
            {
                switch (objet)
                {
                    case Arme arme:
                        objetChoisi = new Arme(arme);
                        break;
                    case Armure armure:
                        objetChoisi = new Armure(armure);
                        break;
                    case Potion potion:
                        objetChoisi = new Potion(potion);
                        break;
                    case ObjetInvoqueBoss objetInvoqueBoss:
                        objetChoisi = new ObjetInvoqueBoss(objetInvoqueBoss);
                        break;
                    default:
                        throw new exceptions.KFException($"Type d'objet inconnu : {objet.GetType()}");
                }
                drops["Objet"] = objetChoisi;
                break;
            }
        }
        drops["XP"] = ennemiActuel.xpDrop;
        return drops;
    }

    public void LoadNouveauDonjon(int donjon)
    {
        if (dbm == null)
            throw new exceptions.KFException("La base de données n'est pas initialisée.");

        Donjon nouveauDonjon = new Donjon(donjon, this);
        donjonsDebloques.Add(nouveauDonjon);

        EnnemiDAO ennemiDAO = new EnnemiDAO(dbm, nouveauDonjon);
        ennemis = ennemiDAO.RecupererEnnemis();

        Ennemi.PondererPoidsSpawn(ennemis);
        controller.AfficherDonjon(donjon);
        numDonjon = donjon;
    }

    public Ennemi GenererProchainEnnemi()
    {
        Ennemi ennemiChoisi = Ennemi.LAMBDA;
        double r = randGen.NextDouble();
        double sommeAccumul = 0;

        foreach (Ennemi ennemi in ennemis)
        {
            sommeAccumul += ennemi.poidsSpawn;
            if (r <= sommeAccumul)
            {
                ennemiChoisi = new Ennemi(ennemi);
                break;
            }
        }

        return ennemiChoisi;
    }

    public void AfficherSoin(Entite entite)
    {
        controller.AfficherSoin(entite);
    }

    public void UpdateUsername(string playerName)
    {
        if (joueur == null)
            throw new exceptions.KFException("Le joueur est null.");

        joueur.Renommer(playerName);
    }
}
