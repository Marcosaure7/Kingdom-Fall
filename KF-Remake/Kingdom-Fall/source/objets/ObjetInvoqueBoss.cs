namespace objets;
using Avalonia.Controls;

public class ObjetInvoqueBoss : Divers
{
    private personnages.Boss bossInvoque;
    private App.Donjon donjon;

    public ObjetInvoqueBoss(string nom, string description, double dropRate, personnages.Boss bossInvoque, App.Donjon donjon)
        : base(nom, description, dropRate)
    {
        this.bossInvoque = bossInvoque;
        this.donjon = donjon;
    }

    public ObjetInvoqueBoss(ObjetInvoqueBoss autre) : base(autre)
    {
        bossInvoque = autre.bossInvoque;
        donjon = autre.donjon;
    }

    public override void utiliser()
    {
        donjon.BossEnFileDattente(bossInvoque);
    }

    public override StackPanel FormatComparedDescription(personnages.Joueur joueur)
    {
        StackPanel stackPanel = new StackPanel();
        string[] descriptionSplit = base.GetDescription().Split("\n");

        foreach (string s in descriptionSplit)
            stackPanel.Children.Add(new TextBlock
                    {
                        Text = s
                    });

        return stackPanel;
    }
}
