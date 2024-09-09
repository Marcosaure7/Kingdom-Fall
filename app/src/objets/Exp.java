package objets;

public class Exp extends Drops {

    private int valeur;

    public Exp(int valeur) {
        this.valeur = valeur;
    }

    public int getValeur() {
        return valeur;
    }

    public void gainExp(Exp expGagnee) {
        valeur += expGagnee.getValeur();
    }

    public String toString() {
        return "+" + valeur + " XP";
    }
}
