namespace objets;

public class Exp : Drops
{
    internal int valeur { get; set; }

    public Exp(int valeur)
    {
        this.valeur = valeur;
    }

    public void GainXp(Exp expGagnee)
    {
        valeur += expGagnee.valeur;
    }

    public override string ToString()
    {
        return $"+{valeur} XP";
    }
}
