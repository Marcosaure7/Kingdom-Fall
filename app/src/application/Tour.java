package application;

import objets.EffetStatut;
import personnages.Ennemi;
import personnages.Joueur;
import user.Action;

import java.util.*;

public class Tour {
    private final Joueur joueur;
    private final Jeu jeu;
    private final Scanner sc = new Scanner(System.in);

    public Tour(Jeu jeu, Joueur joueur) {
        this.jeu = jeu;
        this.joueur = joueur;

        tour();
    }


    private void tour() {
        Ennemi ennemi = jeu.genererProchainEnnemi();
        System.out.printf("%s%nUn ennemi apparait!%n", App.LIGNE);
        boolean tourDuJoueur = true;

        while (!ennemi.estMort())
        {
            if (tourDuJoueur)
            {

                boolean attaqueEffectuee = false;
                while (!attaqueEffectuee)
                {
                    System.out.printf("%s%n%s%n", ennemi, App.LIGNE);
                    Action[] actionsPermises = {Action.ATTAQUER, Action.OUVRIR_INVENTAIRE, Action.INFO};
                    for (Action a : actionsPermises)
                        System.out.print(a);

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
                    }
                }

                if (ennemi.estMort())
                    jeu.ennemiVaincu(ennemi);

            }
            else
            {
                ennemi.subirEffetPeriodique();

                if (ennemi.estMort())
                    jeu.ennemiVaincu(ennemi);

                else
                {
                    System.out.printf("%s vous attaque de %d !%n", ennemi.getNom(), ennemi.getAttBase());
                    joueur.seFaitAttaquer(ennemi.getAttBase(), EffetStatut.AUCUN);
                    System.out.printf("%s%n%s%n%s%n", App.LIGNE, joueur, App.LIGNE);

                    if (joueur.estMort())
                    {
                        System.out.println("Fin du jeu. Vous etes mort.");
                        System.out.println("Appuyer sur Entree pour fermer le jeu...");
                        sc.nextLine();
                        System.exit(0);
                    }
                }

            }

            tourDuJoueur = !tourDuJoueur;
        }

    }

    Action enregistrerAction(Scanner sc, Action[] actions) {
        Action finalAction = Action.NOT_DEFINED;

        do {
            String action = sc.nextLine();

            for (Action a : actions)
            {
                if (a.correspondreStringAction(action))
                {
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

        private void decoder(Action action) {

        }
}
