package application;

import exceptions.InventairePleinException;
import objets.*;
import personnages.Ennemi;
import personnages.Joueur;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Jeu {

    private final DatabaseManager dbm;
    private final Joueur joueur;
    protected int donjon = 0;

    ArrayList<Ennemi> ennemis;
    ArrayList<Objet> armes;
    ArrayList<Objet> armures;
    ArrayList<Objet> potions;
    ArrayList<Objet> divers;

    final Scanner sc;

    public Jeu() throws InterruptedException {
        dbm = new DatabaseManager();
        joueur = new Joueur(100, 0, 1);
        int nbTours = 0;
        loadNouveauDonjon(0);

        sc = new Scanner(System.in);
        premierTour();
        donjon++;
        loadNouveauDonjon(donjon);

        /* Boucle de jeu
         * Version très prematuree
         */
        while (donjon == 1) {
            nbTours++;
            System.out.printf("\n%s\nTour %d\n", App.LIGNE, nbTours);
            new Tour(this, joueur);
        }
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
        System.out.println("Attaquer:att");
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

        boolean actionReconnue = false;
        String[][] reponses = {{"oui", "o"}, {"non", "n"}};
        do {
            System.out.printf("%s%nVoulez-vous passer l'exploration de l'inventaire? (Oui:%s, Non:%s)%n", App.LIGNE, Arrays.toString(reponses[0]), Arrays.toString(reponses[1]));
            action = sc.nextLine();

            String finalAction = action;
            if (Arrays.stream(reponses)
                    .flatMap(Arrays::stream)
                    .anyMatch(element -> element.equals(finalAction)))
            {
                actionReconnue = true;
                if (Arrays.asList(reponses[1]).contains(finalAction)) // Reponse positive
                    initiationALInventaire(sc);

            }
            else
                System.out.println("La commande n'a pas ete reconnue !");
        } while (!actionReconnue);

        System.out.println("Retour au jeu.");

    }

    private void initiationALInventaire(Scanner sc) {
        System.out.println("Quand vous effectuez la commande 'inv' lorsque permis, votre inventaire s'ouvre comme ceci :");
        joueur.getInventaire().ouvrirInventaire(sc);
    }

    boolean executerAction(String action, Ennemi ennemiActuel) {
        boolean actionReconnue = action.equalsIgnoreCase("att");
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
                    case Divers divers -> objetChoisi = new Divers(divers);
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


//        try
//        {
//            dbm.executerLecture(
//                    "SELECT * FROM ennemis WHERE donjon = ?",
//                    donjon,
//                    rs -> {
//                       while (rs.next()) {
//                           Ennemi en = new Ennemi(
//                                   rs.getString("nom"),
//                                   rs.getInt("ptsVie"),
//                                   rs.getInt("niveau"),
//                                   rs.getInt("attaque"),
//                                   rs.getDouble("poidsSpawn"),
//                                   rs.getInt("xpDrop"));
//
//                           ennemis.add(en);
//                       }
//                       return null;
//                    }
//                    );
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }


        // On charge les ennemis du donjon
        try {
            Connection c = dbm.getDataSource().getConnection();
            EnnemiDAO ennemiDAO = new EnnemiDAO(c);
            ennemis = ennemiDAO.recupererEnnemis(donjon);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Ennemi.pondererPoidsSpawn(ennemis);

        // On load les armes du donjon
//        armes = new ArrayList<>();
//        try
//        {
//            dbm.executerLecture(
//                    "SELECT * FROM armes WHERE donjon = ?",
//                    donjon,
//                    rs -> {
//                       while (rs.next()) {
//                           Arme arme = new Arme(
//                                   rs.getString("nom"),
//                                   rs.getString("description"),
//                                   rs.getDouble("drop_rate"),
//                                   rs.getInt("degats"),
//                                   rs.getString("effetStatut"));
//
//                           armes.add(arme);
//                       }
//                       return null;
//                    });
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        Arme.pondererDropRates(armes);
//
//
//        // On load les armures
//        armures = new ArrayList<>();
//        try
//        {
//            dbm.executerLecture(
//                    "SELECT * FROM armures WHERE donjon = ?",
//                    donjon,
//                    rs -> {
//                       while (rs.next()) {
//                           Armure armure = new Armure(
//                                   rs.getString("nom"),
//                                   rs.getString("description"),
//                                   rs.getInt("ptsArmure"),
//                                   rs.getDouble("drop_rate"));
//
//                           armures.add(armure);
//                       }
//                       return null;
//                    });
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        Armure.pondererDropRates(armures);
//
//        potions = new ArrayList<>();
//        try {
//            dbm.executerLecture(
//                    "SELECT * FROM potions WHERE donjon = ?",
//                    donjon,
//                    rs -> {
//                        while (rs.next()) {
//                            Potion potion = new Potion(
//                                    rs.getString("nom"),
//                                    rs.getInt("soin"),
//                                    rs.getDouble("drop_rate"));
//
//                            potions.add(potion);
//                        }
//                        return null;
//                    }
//            );
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        Potion.pondererDropRates(potions);
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
}
