package application;

import objets.EffetStatut;
import personnages.Boss;
import personnages.Ennemi;
import personnages.Joueur;
import user.Action;

import java.util.*;

public class Donjon {
    private final int niveau;
    private final Jeu jeu;
    private Boss boss_donjon;
    boolean bossInvoque = false;

    public Donjon(int niveau, Jeu jeu) {
        this.niveau = niveau;
        this.jeu = jeu;
    }

    public void newTour() {
        Ennemi ennemi;
        if (bossInvoque) ennemi = boss_donjon;
        else ennemi = jeu.genererProchainEnnemi();
        jeu.ennemiCourant = ennemi;

        jeu.controller.afficherEnnemi(ennemi);

        jeu.controller.activerNode("attaquer");
    }

    public void bossEnFileDattente(Boss bossAInvoquer)
    {
        bossInvoque = true;
        boss_donjon = bossAInvoquer;
        jeu.controller.envoyerMessage(String.format("%s sera invoqué au prochain tour!%n", bossAInvoquer.getNom()));
    }

    public int getNiveau()
    {
        return niveau;
    }


}
