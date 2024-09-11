package application;

import exceptions.InventairePleinException;
import objets.Arme;
import objets.Drops;
import objets.Exp;
import objets.Objet;
import personnages.Ennemi;
import personnages.Joueur;

import java.sql.SQLException;
import java.util.*;

public class Jeu {

    private final DatabaseManager dbm;
    private final Joueur joueur;
    private final int donjon = 0;
    private ArrayList<Ennemi> ennemis;
    private ArrayList<Arme> armes;
    private final Scanner sc;

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
//        while (donjon == 1) {
//            nbTours++;
//            System.out.printf("\n%sTour %d\n", App.LIGNE, nbTours);
//            new Tour(joueur, nbTours % 2 == 1);
//        }
    }

    private void premierTour() throws InterruptedException {
        System.out.println("Voici le tour d'introduction à Kingdom Fall !");
        System.out.println("Appuyer sur Entrée pour passer au prochain dialogue...");
        sc.nextLine();
        System.out.println("Un ennemi va apparaître sous peu pour vous montrer les bases.\n" + App.LIGNE);
        Thread.sleep(2000);

        Ennemi ennemiActuel = ennemis.get(0);
        System.out.println(ennemiActuel + "\n" + App.LIGNE);
        Thread.sleep(2000);
        System.out.println("Attaquer:att");
        System.out.println(joueur);
        String action = sc.nextLine();
        while (!executerAction(action, ennemiActuel))
        {
            action = sc.nextLine();
        }
        Thread.sleep(1000);
        System.out.printf("%s%nBravo! %s en a mangé une belle!%n", App.LIGNE, ennemiActuel.getNom());
        System.out.println("Cette dernière section du tutoriel vous servira à naviguer dans l'inventaire.");
        System.out.println("À chaque tour, la commande 'inv' restera disponible pour vous comme ceci :\nInventaire:inv");

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
                if (Arrays.asList(reponses[1]).contains(finalAction)) // Réponse positive
                    initiationALInventaire(sc);

            }
            else
                System.out.println("La commande n'a pas été reconnue !");
        } while (!actionReconnue);


    }

    private void initiationALInventaire(Scanner sc) {
        System.out.println("Quand vous effectuez la commande 'inv' lorsque permis, votre inventaire s'ouvre comme ceci :");
        joueur.ouvrirInventaire(sc);
    }

    private boolean executerAction(String action, Ennemi ennemiActuel) {
        boolean actionReconnue = action.equalsIgnoreCase("att");
        if (actionReconnue) {
            System.out.printf("%s\nVous attaquez, infligeant %d dégât(s) au %s%n", App.LIGNE, joueur.getAttBase(), ennemiActuel.getNom());
            ennemiActuel.seFaitAttaquer(joueur.getAttBase());
            if (ennemiActuel.estMort()) {
                ennemiVaincu(ennemiActuel);
            }
        }
        else
            System.out.println("La commande n'a pas été reconnue !");

        return actionReconnue;
    }

    private void ennemiVaincu(Ennemi ennemiActuel) {
        System.out.printf("%s a été vaincu(e)!%n", ennemiActuel.getNom());
        Map<String, Drops> drops = genererDrops(ennemiActuel);
        System.out.printf("%s\nIl a lâché : %s, %s%n", App.LIGNE, drops.get("Objet").toString(), drops.get("XP").toString());
        joueur.gainXp((Exp) drops.get("XP"));
        demanderRamasser((Objet) drops.get("Objet"));
    }

    private void demanderRamasser(Objet objet) {
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

    private boolean phaseRamassage(String action, Objet objet) throws InterruptedException {
        boolean actionReconnue = false;
        switch (action) {
            case "ram":
                try {
                    joueur.ramasserObjet(objet);
                    System.out.printf("Vous avez ajouté %s à votre inventaire !%n", objet);
                    actionReconnue = true;
                } catch (InventairePleinException e) {
                    System.err.println(e.getMessage());
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
                System.out.println("La commande n'a pas été reconnue !");
        }
        return actionReconnue;
    }

    private Map<String, Drops> genererDrops(Ennemi ennemiActuel) {
        Map<String, Drops> drops = new HashMap<>();

        // Générer le drop d'arme
        Arme armeChoisie = null;
        double r = Math.random();
        double sommeAccumul = 0;
        for (Arme arme : armes) {
            sommeAccumul += arme.getDropRate();
            if (sommeAccumul >= r) {
                armeChoisie = arme;
                break;
            }
        }
        drops.put("Objet", armeChoisie);

        // Générer xp
        drops.put("XP", ennemiActuel.getXpDrop());

        return drops;
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
                                   rs.getInt("poidsSpawn"),
                                   rs.getInt("xpDrop"));

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
                                   rs.getString("description"),
                                   rs.getDouble("drop_rate"),
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

        Arme.pondererDropRates(armes);
    }
}
