package personnages;

public class Ennemi extends Entite {

    private int poidsSpawn;
    private String nom;

    public Ennemi(String nom, int niveau, int poidsSpawn){
        super(niveau);
        this.nom = nom;
        this.poidsSpawn = poidsSpawn;
    }

    // public void setPoidsSpawn(int poidsSpawn) { this.poidsSpawn = poidsSpawn; }
    public int getPoidsSpawn() { return this.poidsSpawn; }
    public String getNom() { return this.nom; }


}
