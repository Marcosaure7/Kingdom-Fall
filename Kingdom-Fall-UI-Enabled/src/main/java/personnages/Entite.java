package personnages;

import objets.EffetStatut;

import java.io.Serializable;

public abstract class Entite implements Serializable
{
    String nom;
    int ptsVie;
    int vieRestante;
    int niveau;
    int attBase;
    EffetStatut effetStatut;
    int dureeEffetStatut;
    int degatsEffetStatut;
    int ptsArmure;

    public Entite()
    {
        this.nom = "";
        this.ptsVie = 0;
        this.vieRestante = 0;
        this.niveau = 0;
        this.attBase = 0;
    }

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

    public String getNom() {
        return nom;
    }

    public String seFaitAttaquer(int attaqueRecue, EffetStatut effetStatutApplique) {
        StringBuilder resultat = new StringBuilder();

        if (effetStatut == EffetStatut.AUCUN)
        {
            effetStatut = effetStatutApplique;
            dureeEffetStatut = effetStatut.getDuree();
        }

        switch (effetStatut)
        {
            case SAIGNEMENT -> degatsEffetStatut = (int) (0.3*attaqueRecue);
            case null, default -> degatsEffetStatut = 0;
        }

        recoitAttaqueSelonArmure(attaqueRecue);

        resultat.append(subirEffetPeriodique());

        if (vieRestante <= 0)
            vieRestante = 0;

        return resultat.toString();
    }

    public String subirEffetPeriodique ()
    {
        String message = "";
        if (dureeEffetStatut > 0)
        {
            switch (effetStatut)
            {
                case SAIGNEMENT -> {
                    vieRestante -= degatsEffetStatut;
                    --dureeEffetStatut;
                    message += String.format("%s subit un effet de %s de %s PV ce tour-ci !\tReste : %d tours à subir %s !%n",
                    nom, effetStatut, degatsEffetStatut, dureeEffetStatut, effetStatut);
                }
                case null, default -> {}
            }
        }

        else {
            effetStatut = EffetStatut.AUCUN;
            degatsEffetStatut = 0;
        }

        return message;

    }

    public boolean estMort() {
        return vieRestante <= 0;
    }

    public EffetStatut getEffetStatut () {
        return effetStatut;
    }
    protected void recoitAttaqueSelonArmure(int attaqueRecue)
    {
        vieRestante -= attaqueRecue;
    }
}
