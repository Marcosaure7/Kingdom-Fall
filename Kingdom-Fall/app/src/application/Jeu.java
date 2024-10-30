package application;

import exceptions.InventairePleinException;
import objets.*;
import personnages.Boss;
import personnages.Ennemi;
import personnages.Joueur;
import user.Action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Jeu {

    private final DatabaseManager dbm;
    private final Joueur joueur;
    protected int numDonjon = 0;
    private final ArrayList<Donjon> donjonsDebloques = new ArrayList<>(5);

    ArrayList<Ennemi> ennemis;

    final Scanner sc;

    public Jeu() throws InterruptedException {
        dbm = new DatabaseManager();
        joueur = new Joueur(100, 0, 1);
        int nbTours = 0;
        loadNouveauDonjon(0);

        sc = new Scanner(System.in);
        premierTour();
        loadNouveauDonjon(numDonjon + 1);


        boolean quitterJeu = false;
        /* Boucle de jeu
         * Version très prématurée
         */
        while (!quitterJeu) {
            nbTours++;
            System.out.printf("\n%s\nTour %d\n", App.LIGNE, nbTours);
            quitterJeu = donjonsDebloques.get(numDonjon).newTour();
        }

        quitterJeu();
    }

    private void premierTour() throws InterruptedException {
        System.out.println("Voici le tour d'introduction a Kingdom Fall !");
        System.out.println("Appuyer sur Entree pour passer au prochain dialogue...");
        sc.nextLine();
        System.out.println("Un ennemi va apparaitre sous peu pour vous montrer les bases.\n" + App.LIGNE);
        Thread.sleep(2000);

        Ennemi ennemiActuel = ennemis.getFirst();
        System.out.println(ennemiActuel);
        Thread.sleep(2000);
        System.out.println(Action.ATTAQUER);
        System.out.println(App.LIGNE + "\n" + joueur);
        String action = sc.nextLine();
        while (!executerAction(action, ennemiActuel))
        {
            action = sc.nextLine();
        }
        Thread.sleep(1000);
        System.out.printf("%s%nBravo! %s en a mange une belle!%n", App.LIGNE, ennemiActuel.getNom());
        System.out.println("Cette derniere section du tutoriel vous servira a naviguer dans l'inventaire.");
        System.out.println("A chaque tour, la commande 'inv' restera disponible pour vous comme ceci :\nInventaire:inv");

        Action[] actions = {Action.OUI, Action.NON};
        Action finalAction = Action.NOT_DEFINED;

        do {
            System.out.printf("%s%nVoulez-vous commencer l'exploration de l'inventaire?%n%s %s%n", App.LIGNE, actions[0], actions[1]);
            action = sc.nextLine();

            for (Action a : actions) {
                if (a.correspondreStringAction(action)) {
                    finalAction = a;
                    break;
                }
            }

            if (finalAction == Action.NOT_DEFINED)
                System.out.println(App.CMD_INCONNUE);
        }
        while (finalAction == Action.NOT_DEFINED);

        if (finalAction == Action.OUI) // Reponse positive
            initiationALInventaire(sc);

        System.out.println("Retour au jeu.");

    }

    private void initiationALInventaire(Scanner sc) {
        System.out.println("Quand vous effectuez la commande 'inv' lorsque permis, votre inventaire s'ouvre comme ceci :");
        joueur.getInventaire().ouvrirInventaire(sc);
    }

    boolean executerAction(String action, Ennemi ennemiActuel) {
        boolean actionReconnue = Action.ATTAQUER.correspondreStringAction(action);
        if (actionReconnue) {
            System.out.printf("%s\nVous attaquez, infligeant %d degat(s) au %s%n", App.LIGNE, joueur.getAtt(), ennemiActuel.getNom());
            ennemiActuel.seFaitAttaquer(joueur.getAtt(), joueur.getEffetArme());
            if (ennemiActuel.estMort()) {
                ennemiVaincu(ennemiActuel);
            }
        }
        else
            System.out.println("La commande n'a pas ete reconnue !");

        return actionReconnue;
    }

    void ennemiVaincu(Ennemi ennemiActuel) {
        System.out.printf("%s a ete vaincu(e)!%n", ennemiActuel.getNom());
        Map<String, Drops> drops = genererDrops(ennemiActuel);
        System.out.printf("%s\nIl a lache : %s, %s%n", App.LIGNE, drops.get("Objet").toString(), drops.get("XP").toString());
        joueur.gainXp((Exp) drops.get("XP"));
        demanderRamasser((Objet) drops.get("Objet"));

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

    void demanderRamasser(Objet objet) {
        String action;

        try {
            do {
                System.out.printf("%s\nVoulez-vous ramasser \"%s\" ?\nInfo:info\tRamasser:ram\tRejeter:jet%n", App.LIGNE, objet);
                action = sc.nextLine();
            }
            while (!phaseRamassage(action, objet));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    boolean phaseRamassage(String action, Objet objet) throws InterruptedException {
        boolean actionReconnue = false;
        switch (action) {
            case "ram":
                try {
                    joueur.getInventaire().ramasserObjet(objet);
                    System.out.printf("Vous avez ajoute %s à votre inventaire !%n", objet);
                    actionReconnue = true;
                } catch (InventairePleinException e) {
                    System.err.println(e.getMessage());
                    System.out.print("");
                }
                break;
            case "jet":
                actionReconnue = true;
                System.out.printf("Vous laissez \"%s\" au sol...%n", objet);
                break;
            case "info":
                // On ne met pas actionReconnue à true car on veut redemander ce que l'usager fait de l'objet.
                System.out.println(App.LIGNE + "\n" + objet.getDescription());
                Thread.sleep(1000);
                break;
            default:
                System.out.println(App.CMD_INCONNUE);
        }
        return actionReconnue;
    }

    Map<String, Drops> genererDrops(Ennemi ennemiActuel) {
        Map<String, Drops> drops = new HashMap<>();

        // Generer le drop d'arme
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

        return drops;
    }

    private void loadNouveauDonjon(int donjon) {

        Donjon nouveauDonjon = new Donjon(donjon, this, joueur);

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
}
