package personnages;

public class Ennemi extends Entite {

    private int poidsSpawn;
    private String nom;

    public Ennemi(String nom, int ptsVie, int niveau, int att, int poidsSpawn){
        super(ptsVie, niveau, att);
        this.nom = nom;
        this.poidsSpawn = poidsSpawn;
    }

    // public void setPoidsSpawn(int poidsSpawn) { this.poidsSpawn = poidsSpawn; }
    public int getPoidsSpawn() { return this.poidsSpawn; }
    public String getNom() { return this.nom; }


}
