namespace personnages;

public class Boss : Ennemi
{
    private readonly int _ptsArmure;
    private readonly int _donjonDebloque;
    private readonly string _description;

    public int DonjonDebloque => _donjonDebloque;
    public string GetDescription => _description;

    public Boss(string nom, string description, int niveau, int ptsVie, int att, int xpDrop, int ptsArmure, List<objets.Objet> drops)
        : base(nom, ptsVie, niveau, att, 0, xpDrop, drops)
    {
        _ptsArmure = ptsArmure;
        _donjonDebloque = niveau + 1;
        _description = description;
    }
}