package application;

import personnages.Joueur;

public class Tour {
    private boolean estRelax = false;
    private Joueur joueur;

    public Tour(Joueur joueur, boolean estRelax) {
        this.estRelax = estRelax;
        this.joueur = joueur;


    }
}
