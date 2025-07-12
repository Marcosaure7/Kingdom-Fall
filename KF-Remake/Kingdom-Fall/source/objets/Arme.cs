namespace objets;
using Avalonia.Controls;
using Avalonia.Layout;

public class Arme : Objet
{
    internal int degats { get; private set; }
    internal EffetStatut effet { get; private set; }
    internal string description { get; private set; }

    public Arme(string nom, string description, double dropRate, int degats, string effetStatut)
        : base(TypeObjet.Armes, nom, dropRate)
    {
        this.degats = degats;
        SetEffetStatutFromString(effetStatut);
        this.description = description.Equals("null") ? "" : description;
    }

    public Arme(Arme autre) : base(autre)
    {
        this.degats = autre.degats;
        this.effet = autre.effet;
        this.description = autre.description;
    }

    public void SetEffetStatutFromString(string effetStatut)
    {
        // Première lettre en maj.
        effetStatut = char.ToUpper(effetStatut[0]) + effetStatut.Substring(1);
        
        effet = (EffetStatut)Enum.Parse(typeof(EffetStatut), effetStatut);
    }

    public override string GetDescription()
    {
        return $"{base.nom}\n{this.description}\nDégâts : {degats}\nEffet de statut : {effet.ToString()}\nChances de drop : {dropRateString}";
    }

    public override StackPanel FormatComparedDescription(personnages.Joueur joueur)
    {
        StackPanel stackPanel = new StackPanel();
        string[] descriptionSplit = GetDescription().Split("\n");

        for (int i = 0; i < descriptionSplit.Length; i++)
        {
            if (i != 2)
                stackPanel.Children.Add(new TextBlock
                        { 
                            Text = $"{descriptionSplit[i]}\n" 
                        });
            else
            {
                WrapPanel wrapPanel = new WrapPanel
                {
                    Orientation = Orientation.Horizontal
                };
                wrapPanel.Children.Add(new TextBlock
                {
                    Text = $"{descriptionSplit[i]} "
                });
                wrapPanel.Children.Add(GetDifferenceDegats((Arme)joueur.GetEquip(TypeObjet.Armes)));
                stackPanel.Children.Add(wrapPanel);
            }
        }

        return stackPanel;
    }

    private TextBlock GetDifferenceDegats(Arme autre)
    {
        return GetComparisonText(degats, autre.degats);
    }
}
