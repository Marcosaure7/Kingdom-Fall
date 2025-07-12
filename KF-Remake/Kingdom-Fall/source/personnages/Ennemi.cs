using System.Collections;
namespace personnages;

public class Ennemi : Entite 
{
    public double poidsSpawn { get; set; }
    public List<objets.Objet> drops { get; }
    internal objets.Exp xpDrop { get; }

    public static Ennemi LAMBDA { get { return new Ennemi(); } }

    public Ennemi() : base("lambda", 0, 0, 0)
    {
        this.poidsSpawn = 0;
        this.drops = new List<objets.Objet>();
        this.xpDrop = new objets.Exp(0);
    }

    public Ennemi(string nom, int ptsVie, int niveau, int attBase, double poidsSpawn, int xpDrop, List<objets.Objet> drops) : base(nom, ptsVie, niveau, attBase)
    {
        this.drops = drops;
        this.poidsSpawn = poidsSpawn;
        this.xpDrop = new objets.Exp(xpDrop);
    }

    public Ennemi (Ennemi autre) : base(autre)
    {
        this.drops = autre.drops;
        this.poidsSpawn = autre.poidsSpawn;
        this.xpDrop = new objets.Exp(autre.xpDrop.valeur);
    }

    public override string ToString()
    {
        return $"{base.nom}\n\nNiv:{niveau}\tAtt:{attBase}\t";
    }

    public static void PondererPoidsSpawn(List<Ennemi> ennemis)
    {
        double totalDropRates = 0;
        foreach (Ennemi ennemi in ennemis)
        {
            totalDropRates += ennemi.poidsSpawn;
        }
        foreach (Ennemi ennemi in ennemis)
        {
            ennemi.poidsSpawn /= totalDropRates;
        }
    }
}
