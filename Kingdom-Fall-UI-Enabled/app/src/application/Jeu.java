package application;

import objets.*;
import personnages.Boss;
import personnages.Ennemi;
import personnages.Entite;
import personnages.Joueur;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import controller.FenetreAppController;

public class Jeu {

    public final FenetreAppController controller;
    public GameLogic gameLogic;

    private DatabaseManager dbm;
    private Joueur joueur;
    protected int numDonjon = 0;
    private final ArrayList<Donjon> donjonsDebloques = new ArrayList<>(5);

    public Ennemi ennemiCourant;

    ArrayList<Ennemi> ennemis;

    Map<String, Drops> dropsCourants;

    final Scanner sc;

    public Jeu(GameLogic gameLogic, FenetreAppController controller) {
        this.controller = controller;
        this.gameLogic = gameLogic;
        sc = new Scanner(System.in);
    }

    public void lancerJeu() {
        dbm = new DatabaseManager();
        joueur = new Joueur(this, 100, 0, 1);
        loadNouveauDonjon(0);
        premierTourUI();
        loadNouveauDonjon(1);
        while (true)
        {
            donjonsDebloques.get(numDonjon).newTour();
            gameLogic.attendreFinTour();
        }
    }

    private void premierTourUI() {
        ennemiCourant = ennemis.getFirst();

        try {
            controller.envoyerMessage("Voici le tour d'introduction.");
            Thread.sleep(1000);

            controller.envoyerMessage("Un ennemi va apparaître pour vous montrer les bases.");
            Thread.sleep(2000);

            controller.afficherEnnemi(ennemiCourant);
            Thread.sleep(1000);

            controller.envoyerMessage("Appuyer sur 'attaquer' pour mettre un terme à ce squelette.");
            controller.activerNode("attaquer");

            gameLogic.attendreFinTour();

            Thread.sleep(1000);
            controller.envoyerMessage("Maintenant que vous avez une nouvelle arme, nous allons naviguer l'inventaire pour l'équiper. Appuyez sur le bouton 'Inventaire'.");
            controller.activerNode("inventaire");

            Thread.sleep(6000);
            controller.envoyerMessage("Parfait, vous êtes maintenant prêts à jouer !");

            Thread.sleep(2000);
            controller.envoyerMessage("En situation difficile, n'oubliez pas de vous soigner! Un soin rapide peut être fait à l'aide du bouton correspondant.");

            controller.envoyerMessage("Chargement du donjon...");
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public Joueur getJoueur()
    {
        return joueur;
    }

    public Objet getDropCourant() {
        return (Objet) dropsCourants.get("Objet");
    }

    public void effectuerAttaque() {
        ennemiCourant.seFaitAttaquer(joueur.getAtt(), joueur.getEffetStatut());
        controller.afficherAttaquer(ennemiCourant);
        if (ennemiCourant.estMort())
            ennemiVaincu(ennemiCourant);
        else
        {
            joueur.seFaitAttaquer(ennemiCourant.getAttBase(), ennemiCourant.getEffetStatut());
            controller.afficherAttaquer(joueur);
        }
    }

    void ennemiVaincu(Ennemi ennemiActuel) {
        controller.envoyerMessage(ennemiActuel.getNom() + " a été vaincu(e)!");
        dropsCourants = genererDrops(ennemiActuel);
        controller.envoyerMessage(String.format("Il a lâché : %s, %s", dropsCourants.get("Objet").toString(), dropsCourants.get("XP").toString()));
        int ancienNiveau = joueur.getNiveau();
        joueur.gainXp((Exp) dropsCourants.get("XP"));
        controller.gainXp(joueur, ancienNiveau, ((Exp) dropsCourants.get("XP")).getValeur());

        if (ennemiActuel instanceof Boss bossActuel) bossVaincu(bossActuel);
    }

    void bossVaincu(Boss bossActuel) {
        boolean donjonDejaDebloque = false;

        // On regarde si le donjon a déjà été débloqué par le joueur
        for (Donjon d: donjonsDebloques) {
            if (d.getNiveau() == bossActuel.getDonjonDebloque())
            {
                donjonDejaDebloque = true;
                break;
            }
        }
        if (!donjonDejaDebloque)
        {
            System.out.printf(
                    "Vous avez vaincu le/la %s qui hantait ce donjon. C'est tout a votre honneur.%n" +
                            "Vous venez de debloquer le donjon de niveau %d.%n", bossActuel.getNom(), bossActuel.getDonjonDebloque());

            System.out.println("Vous voyagez vers le prochain donjon...");

            loadNouveauDonjon(bossActuel.getDonjonDebloque());
        }
    }

    public void ramasser() {
        Objet drop = (Objet) dropsCourants.get("Objet");

        // Si le joueur ramasse une potion, alors le soin rapide devient disponible.
        if (drop instanceof Potion)
            controller.activerNode("soin rapide");

        joueur.getInventaire().ramasserObjet(drop);
        controller.envoyerMessage(drop.getNom() + " ajouté à l'inventaire !");
    }

    Map<String, Drops> genererDrops(Ennemi ennemiActuel) {
        Map<String, Drops> drops = new HashMap<>();

        // Générer le drop d'arme
        Objet objetChoisi = null;
        double r = Math.random();
        double sommeAccumul = 0;
        ArrayList<Objet> dropsList = ennemiActuel.getDrops();

        for (Objet objet : dropsList) {
            sommeAccumul += objet.getDropRate();
            if (sommeAccumul >= r) {
                switch (objet) {
                    case Arme arme -> objetChoisi = new Arme(arme);
                    case Armure armure -> objetChoisi = new Armure(armure);
                    case Potion potion -> objetChoisi = new Potion(potion);
                    case ObjetInvoqueBoss objetInvoqueBoss -> objetChoisi = new ObjetInvoqueBoss(objetInvoqueBoss);
                    default -> {}
                }
                break;
            }
        }
        drops.put("Objet", objetChoisi);

        // Generer xp
        drops.put("XP", ennemiActuel.getXpDrop());

        controller.showDrops(ennemiActuel, objetChoisi);

        return drops;
    }

    private void loadNouveauDonjon(int donjon) {

        Donjon nouveauDonjon = new Donjon(donjon, this);

        // On charge les ennemis du donjon
        try {
            Connection c = dbm.getDataSource().getConnection();
            EnnemiDAO ennemiDAO = new EnnemiDAO(c);
            ennemis = ennemiDAO.recupererEnnemis(nouveauDonjon);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Ennemi.pondererPoidsSpawn(ennemis);

        donjonsDebloques.add(nouveauDonjon);
        controller.afficherDonjon(donjon);
        numDonjon = donjon; // Permet de dire à l'objet jeu à quel donjon on est présentement.
    }

    public Ennemi genererProchainEnnemi ()
    {
        Ennemi ennemiChoisi = Ennemi.LAMBDA;
        double r = Math.random();
        double sommeAccumul = 0;

        for (Ennemi ennemi : ennemis) {
            sommeAccumul += ennemi.getPoidsSpawn();
            if (sommeAccumul >= r) {
                ennemiChoisi = new Ennemi(ennemi);
                break;
            }
        }

        return ennemiChoisi;
    }

    public void quitterJeu()
    {
        System.out.println("Appuyer sur la touche Entree pour fermer Kingdom Fall...");
        sc.nextLine(); System.exit(0);
    }


    public void afficherSoin(Entite entite) {
        controller.afficherSoin(entite);
    }
}
