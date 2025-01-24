package personnages;

import application.App;
import objets.EffetStatut;

public abstract class Entite {

    public final int efficaciteArmure = 100; // 100 et 200 sont des valeurs courantes utilisées

    String nom;
    int ptsVie;
    int vieRestante;
    int niveau;
    int attBase;
    EffetStatut effetStatut;
    int dureeEffetStatut;
    int degatsEffetStatut;
    int ptsArmure;

    public Entite(String nom, int ptsVie, int niveau, int attBase) {
        this.nom = nom;
        this.niveau = niveau;
        this.ptsVie = ptsVie;
        this.vieRestante = ptsVie;
        this.attBase = attBase;
        effetStatut = EffetStatut.AUCUN;
        dureeEffetStatut = 0;
        ptsArmure = 0;
        degatsEffetStatut = 0;
    }

    public Entite (Entite autre)
    {
        this.nom = autre.nom;
        this.niveau = autre.niveau;
        this.ptsVie = autre.ptsVie;
        this.vieRestante = autre.vieRestante;
        this.attBase = autre.attBase;
        this.effetStatut = autre.effetStatut;
        this.dureeEffetStatut = autre.dureeEffetStatut;
        this.degatsEffetStatut = autre.degatsEffetStatut;
        this.ptsArmure = autre.ptsArmure;
    }

    public int getPtsVie() {
        return ptsVie;
    }
    public int getVieRestante() {
        return vieRestante;
    }

    public int getNiveau() {
        return niveau;
    }

    public int getAttBase() {
        return attBase;
    }

    public void seFaitAttaquer(int attaqueRecue, EffetStatut effetStatutApplique) {
        effetStatut = effetStatutApplique;
        switch (effetStatut)
        {
            case SAIGNEMENT -> degatsEffetStatut = (int) (0.3*ptsVie);
            case null, default -> degatsEffetStatut = 0;
        }

        if (effetStatut != EffetStatut.AUCUN)
            dureeEffetStatut = effetStatut.getDuree();


        int degatsReduits = attaqueRecue/(1 + ptsArmure/efficaciteArmure);
        vieRestante -= degatsReduits;

        if (vieRestante <= 0)
            vieRestante = 0;
    }

    public void subirEffetPeriodique ()
    {
        if (dureeEffetStatut > 0)
        {
            switch (effetStatut)
            {
                case SAIGNEMENT -> {
                    vieRestante -= degatsEffetStatut;
                    --dureeEffetStatut;
                    System.out.printf("%s%n%s subit un effet de %s ce tour-ci !\tReste : %d tours à subir %s !%n",
                            App.LIGNE, nom, effetStatut, dureeEffetStatut, effetStatut);
                }
                case null, default -> {}
            }
        }

        else {
            effetStatut = EffetStatut.AUCUN;
            degatsEffetStatut = 0;
        }

    }

    public boolean estMort() {
        return vieRestante <= 0;
    }

    public EffetStatut getEffetStatut () {
        return effetStatut;
    }
}
