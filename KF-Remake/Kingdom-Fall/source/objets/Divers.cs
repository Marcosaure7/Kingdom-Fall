namespace objets;

public abstract class Divers : Objet
{
    private string description;

    public Divers(string nom, string description, double dropRate)
        : base(TypeObjet.Divers, nom, dropRate)
    {
        this.description = description;
    }

    public Divers(Divers autre) : base(autre)
    {
        this.description = autre.description;
    }

    public override string GetDescription()
    {
        return $"{nom}\n{description}\nChances de drop : {dropRateString}";
    }

    public virtual void utiliser() {}
}
