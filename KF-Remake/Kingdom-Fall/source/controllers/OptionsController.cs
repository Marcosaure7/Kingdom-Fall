using Avalonia.Controls;
namespace controllers;

public partial class OptionsController : UserControl
{
    internal static string PlayerName { get; set; } = "Joueur";
    public static bool CONFIRMER_JETER = true;
    public static bool DEV_MODE = false;

    internal Window? StageOptions { get; private set; }
    private FenetreAppController? fenetreAppController;

    public static void RenommerJoueur(string nouveauNom)
    {
        if (string.IsNullOrWhiteSpace(nouveauNom))
        {
            throw new ArgumentException("Le nom du joueur ne peut pas être vide ou uniquement composé d'espaces.");
        }
        PlayerName = nouveauNom;
    }

    public void SetStageOptions(Window stageOptions)
    {
        StageOptions = stageOptions;
    }

    public void SetFenetreAppController(FenetreAppController controller)
    {
        fenetreAppController = controller;
    }


}