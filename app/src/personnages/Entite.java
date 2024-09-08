package personnages;

public abstract class Entite {

    int ptsVie;
    int niveau;
    int attBase;

    public Entite(int ptsVie, int niveau, int attBase) {
        this.niveau = niveau;
        this.ptsVie = ptsVie;
        this.attBase = attBase;
    }

    public int getPtsVie() {
        return ptsVie;
    }

    public int getNiveau() {
        return niveau;
    }

    public int getAttBase() {
        return attBase;
    }
}
