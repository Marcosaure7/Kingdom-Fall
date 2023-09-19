package personnages;

public abstract class Entite {

    int ptsVie;
    int niveau;
    int att_base;

    public Entite(int niveau) {
        this.niveau = niveau;
        this.ptsVie = 20 * (niveau+1);
        this.att_base = 5 * (niveau+1);
    }
}
