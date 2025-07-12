namespace objets;
using Avalonia.Controls;

public class Potion : Objet
{
    internal int soin { get; private set; }
    private string description;

    public Potion(string nom, int soin, double drop_rate) : base(TypeObjet.Potions, nom, drop_rate)
    {
        this.description = $"Soigne de {soin} PV";
        this.soin = soin;
    }

    public Potion(Potion autre) : base(autre)
    {
        this.description = autre.description;
        this.soin = autre.soin;
    }

    public override string GetDescription()
    {
        return description;
    }

    public override StackPanel FormatComparedDescription(personnages.Joueur joueur)
    {
        StackPanel stackPanel = new StackPanel();
        string[] descriptionSplit = description.Split("\n");

        foreach (string s in descriptionSplit)
            stackPanel.Children.Add(new TextBlock
                    {
                        Text = s
                    });

        return stackPanel;
    }
}
