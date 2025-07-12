using controllers;
using personnages;

namespace App;

public class Donjon
{
    internal readonly int niveau;
    private readonly Jeu jeu;
    private Boss? boss_donjon;
    bool bossInvoque = false;

    public Donjon(int niveau, Jeu jeu)
    {
        this.niveau = niveau;
        this.jeu = jeu;
    }

    public void NewTour()
    {
        Ennemi ennemi;
        if (bossInvoque) ennemi = boss_donjon;
        else ennemi = jeu.GenererProchainEnnemi();
        jeu.ennemiCourant = ennemi;

        FenetreAppController controller = FenetreAppController.GetSingleton();
        controller.AfficherEnnemi(ennemi);
        controller.ActiverNode("attaquer");
    }

    public void BossEnFileDattente(Boss bossAInvoquer)
    {
        bossInvoque = true;
        boss_donjon = bossAInvoquer;
        FenetreAppController.GetSingleton().EnvoyerMessage($"{boss_donjon.nom} sera invoqué au prochain tour!\n");
    }
}
