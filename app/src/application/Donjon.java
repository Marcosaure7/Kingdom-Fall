package application;

import objets.EffetStatut;
import personnages.Boss;
import personnages.Ennemi;
import personnages.Joueur;
import user.Action;

import java.util.*;

public class Donjon {
    private final int niveau;
    private final Joueur joueur;
    private final Jeu jeu;
    private final Scanner sc = new Scanner(System.in);
    private Boss boss_donjon;
    boolean bossInvoque = false;

    public Donjon(int niveau, Jeu jeu, Joueur joueur) {
        this.niveau = niveau;
        this.jeu = jeu;
        this.joueur = joueur;
    }

    public boolean newTour() {
        Ennemi ennemi;
        if (bossInvoque) ennemi = boss_donjon;
        else ennemi = jeu.genererProchainEnnemi();

        System.out.printf("%s%nUn ennemi apparait!%n", App.LIGNE);
        boolean tourDuJoueur = true;

        while (!ennemi.estMort()) {
            if (tourDuJoueur) {

                boolean attaqueEffectuee = false;
                while (!attaqueEffectuee) {
                    System.out.printf("%s%n%s%n", ennemi, App.LIGNE);
                    Action[] actionsPermises = {Action.ATTAQUER, Action.OUVRIR_INVENTAIRE, Action.INFO, Action.QUITTER};
                    for (Action a : actionsPermises)
                        System.out.print(a + " ");

                    System.out.println();
                    Action cmd = enregistrerAction(sc, actionsPermises);

                    switch (cmd) {
                        case ATTAQUER -> {
                            System.out.printf("%s\nVous attaquez, infligeant %d degat(s) au %s%n", App.LIGNE, joueur.getAtt(), ennemi.getNom());

                            if (joueur.getEffetArme() != EffetStatut.AUCUN)
                                System.out.printf("Vous infligez %s a %s%n", joueur.getEffetArme(), ennemi.getNom());

                            ennemi.seFaitAttaquer(joueur.getAtt(), joueur.getEffetArme());
                            attaqueEffectuee = true;
                        }
                        case OUVRIR_INVENTAIRE -> joueur.getInventaire().ouvrirInventaire(sc);
                        case INFO -> System.out.println(joueur + "\n" + App.LIGNE);
                        case QUITTER -> {return true;}
                    }
                }

                if (ennemi.estMort())
                {
                    jeu.ennemiVaincu(ennemi);
                    if (ennemi == boss_donjon)
                    {
                        jeu.bossVaincu(boss_donjon);
                    }
                }


            } else {
                ennemi.subirEffetPeriodique();

                if (ennemi.estMort())
                    jeu.ennemiVaincu(ennemi);

                else {
                    System.out.printf("%s vous attaque de %d !%n", ennemi.getNom(), ennemi.getAttBase());
                    joueur.seFaitAttaquer(ennemi.getAttBase(), EffetStatut.AUCUN);
                    System.out.printf("%s%n%s%n%s%n", App.LIGNE, joueur, App.LIGNE);

                    if (joueur.estMort()) {
                        System.out.println("Fin du jeu. Vous etes mort.");
                        System.out.println("Appuyer sur Entree pour fermer le jeu...");
                        sc.nextLine();
                        System.exit(0);
                    }
                }

            }

            tourDuJoueur = !tourDuJoueur;
        }
        return false;
    }

    Action enregistrerAction(Scanner sc, Action[] actions) {
        Action finalAction = Action.NOT_DEFINED;

        do {
            String action = sc.nextLine();

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

        return finalAction;
    }

    public void bossEnFileDattente(Boss bossAInvoquer)
    {
        bossInvoque = true;
        boss_donjon = bossAInvoquer;
        System.out.printf("%s sera invoqué au prochain tour!%n", bossAInvoquer.getNom());
    }

    public int getNiveau()
    {
        return niveau;
    }


}
