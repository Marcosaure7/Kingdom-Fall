namespace objets;
using Avalonia.Controls;
using Avalonia.Layout;

public class Armure : Objet
{
    private string description;
    internal int capaciteArmure { get; private set; }
    internal int ptsArmure { get; private set; }

    public Armure(string nom, string description, int capaciteArmure, double dropRate)
        : base(TypeObjet.Armures, nom, dropRate)
    {
        this.capaciteArmure = capaciteArmure;
        this.description = description.Equals("null") ? "" : description;
        ptsArmure = capaciteArmure;
    }

    public Armure(Armure autre) : base(autre)
    {
        this.description = autre.description;
        this.capaciteArmure = autre.capaciteArmure;
        this.ptsArmure = autre.ptsArmure;
    }

    public override string GetDescription()
    {
        return $"{nom}\n{description}\nCapacité : {capaciteArmure}\nArmure : {ptsArmure}\nChances de drop : {dropRateString}";
    }

    public override StackPanel FormatComparedDescription(personnages.Joueur joueur)
    {
        StackPanel stackPanel = new StackPanel();
        string[] descriptionSplit = GetDescription().Split("\n");

        for (int i = 0; i < descriptionSplit.Length; i++)
        {
            if (i != 2 && i != 3 || joueur.GetEquip(TypeObjet.Armures) == null)
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
                    Text = descriptionSplit[i]
                });
                if (i == 2)
                    wrapPanel.Children.Add(GetDifferenceCapaciteArmure((Armure)joueur.GetEquip(TypeObjet.Armures)));
                else
                    wrapPanel.Children.Add(GetDifferencePtsArmure((Armure)joueur.GetEquip(TypeObjet.Armures)));

                stackPanel.Children.Add(wrapPanel);
            }
        }

        return stackPanel;
    }

    /**
     * Fait subir l'attaque à l'armure seulement.
     * @param attaque L'attaque reçue à l'entité.
     * @return Les dégâts non absorbés par l'armure.
     */
    public int MangerAttaque(int attaque)
    {
        if (attaque <= ptsArmure)
        {
            ptsArmure -= attaque;
            attaque = 0;
        }
        else
        {
            attaque -= ptsArmure;
            ptsArmure = 0;
        }

        return attaque;
    }


    public void ResetArmure()
    {
        ptsArmure = capaciteArmure;
    }

    private TextBlock GetDifferenceCapaciteArmure(Armure? autre) {
        return GetComparisonText(capaciteArmure, autre?.capaciteArmure ?? 0);
    }

    private TextBlock GetDifferencePtsArmure(Armure? autre) {
        return GetComparisonText(ptsArmure, autre?.ptsArmure ?? 0);
    }
}
