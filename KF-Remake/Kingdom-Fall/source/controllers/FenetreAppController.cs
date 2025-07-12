namespace controllers;

using App;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Controls.Shapes;
using Avalonia.Input;
using Avalonia.Interactivity;
using Avalonia.Layout;
using Avalonia.Markup.Xaml;
using Avalonia.Media;
using Avalonia.Media.Imaging;
using Avalonia.Threading;
using personnages;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Timers;


public partial class FenetreAppController : Window
{
    private readonly double vieProgresParIteration = 0.025;
    private readonly double vieEnnemieProgresParIteration = 0.025;
    private readonly double xpProgresParIteration = 0.01;
    private readonly double armureProgresParIteration = 0.03;
    private readonly double durationPerIteration = 10;

    private GameLogic gameLogic;
    private InventaireController inventaireController;
    private OptionsController optionsController;
    private static FenetreAppController singleton;

    // Contrôles Avalonia
    private ProgressBar barreVie;
    private ProgressBar barreArmure;
    private ProgressBar barreVieEnnemie;
    private ProgressBar barreXP;
    private Button boutonAttaquer;
    private Button boutonEquiper;
    private Button boutonInventaire;
    private Button boutonJeter;
    private Button boutonRamasser;
    private Button boutonSoinRapide;
    private TextBlock labelAttaqueEnnemie;
    private TextBlock labelDonjon;
    private TextBlock labelGainXP;
    private TextBlock labelEnnemiLache;
    private TextBlock labelItemDrop;
    private TextBlock labelNiveauJoueur;
    private TextBlock labelNomEnnemi;
    private TextBlock labelNomJoueur;
    private TextBlock labelPotionsRestantes;
    private TextBlock labelVie;
    private TextBlock labelArmure;
    private TextBlock labelVieEnnemi;
    private TextBlock labelXPJoueur;
    private MenuItem menuOptions;
    private MenuItem menuCharger;
    private MenuItem menuQuitter;
    private MenuItem menuSauvegarder;
    private ScrollViewer scrollMessages;
    private TextBlock textFlowMessages;
    private Image imageDrop;
    private Image imageEnnemi;

    public FenetreAppController()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        AvaloniaXamlLoader.Load(this);
        singleton = this;
        
        // Initialisation des contrôles (normalement définis dans XAML)
        barreVie = this.FindControl<ProgressBar>("BarreVie");
        barreArmure = this.FindControl<ProgressBar>("BarreArmure");
        barreVieEnnemie = this.FindControl<ProgressBar>("BarreVieEnnemie");
        barreXP = this.FindControl<ProgressBar>("BarreXP");
        boutonAttaquer = this.FindControl<Button>("BoutonAttaquer");
        boutonEquiper = this.FindControl<Button>("BoutonEquiper");
        boutonInventaire = this.FindControl<Button>("BoutonInventaire");
        boutonJeter = this.FindControl<Button>("BoutonJeter");
        boutonRamasser = this.FindControl<Button>("BoutonRamasser");
        boutonSoinRapide = this.FindControl<Button>("BoutonSoinRapide");
        labelAttaqueEnnemie = this.FindControl<TextBlock>("LabelAttaqueEnnemie");
        labelDonjon = this.FindControl<TextBlock>("LabelDonjon");
        labelGainXP = this.FindControl<TextBlock>("LabelGainXP");
        labelEnnemiLache = this.FindControl<TextBlock>("LabelEnnemiLache");
        labelItemDrop = this.FindControl<TextBlock>("LabelItemDrop");
        labelNiveauJoueur = this.FindControl<TextBlock>("LabelNiveauJoueur");
        labelNomEnnemi = this.FindControl<TextBlock>("LabelNomEnnemi");
        labelNomJoueur = this.FindControl<TextBlock>("LabelNomJoueur");
        labelPotionsRestantes = this.FindControl<TextBlock>("LabelPotionsRestantes");
        labelVie = this.FindControl<TextBlock>("LabelVie");
        labelArmure = this.FindControl<TextBlock>("LabelArmure");
        labelVieEnnemi = this.FindControl<TextBlock>("LabelVieEnnemi");
        labelXPJoueur = this.FindControl<TextBlock>("LabelXPJoueur");
        menuOptions = this.FindControl<MenuItem>("MenuOptions");
        menuCharger = this.FindControl<MenuItem>("MenuCharger");
        menuQuitter = this.FindControl<MenuItem>("MenuQuitter");
        menuSauvegarder = this.FindControl<MenuItem>("MenuSauvegarder");
        scrollMessages = this.FindControl<ScrollViewer>("ScrollMessages");
        textFlowMessages = this.FindControl<TextBlock>("TextFlowMessages");
        imageDrop = this.FindControl<Image>("ImageDrop");
        imageEnnemi = this.FindControl<Image>("ImageEnnemi");

        if (barreVie == null || barreArmure == null || barreVieEnnemie == null || barreXP == null ||
            boutonAttaquer == null || boutonEquiper == null || boutonInventaire == null || boutonJeter == null ||
            boutonRamasser == null || boutonSoinRapide == null || labelAttaqueEnnemie == null ||
            labelDonjon == null || labelGainXP == null || labelEnnemiLache == null || labelItemDrop == null ||
            labelNiveauJoueur == null || labelNomEnnemi == null || labelNomJoueur == null ||
            labelPotionsRestantes == null || labelVie == null || labelArmure == null ||
            labelVieEnnemi == null || labelXPJoueur == null || menuOptions == null ||
            menuCharger == null || menuQuitter == null || menuSauvegarder == null ||
            scrollMessages == null || textFlowMessages == null || imageDrop == null || imageEnnemi == null)
        {
            throw new InvalidOperationException("Un ou plusieurs contrôles requis sont manquants.");
        }

        // Initialisation des valeurs par défaut
        imageEnnemi.Source = null;
        imageDrop.Source = null;
        labelGainXP.Text = "";
        labelEnnemiLache.Text = "";
        labelItemDrop.Text = "";
        labelDonjon.Text = "0";
        labelAttaqueEnnemie.Text = "";
        labelNiveauJoueur.Text = "0";
        labelNomEnnemi.Text = "";
        labelArmure.Text = "0";
        labelXPJoueur.Text = "0.0%";
        labelVieEnnemi.Text = "";
        labelPotionsRestantes.Text = "(0)";
        barreVie.Value = 100;
        barreArmure.Value = 0;
        barreXP.Value = 0;
        barreVieEnnemie.Value = 0;
        boutonInventaire.IsEnabled = false;
        boutonSoinRapide.IsEnabled = false;
        boutonAttaquer.IsEnabled = false;
        boutonRamasser.IsVisible = false;
        boutonJeter.IsVisible = false;
        boutonEquiper.IsVisible = false;
        textFlowMessages.Text = "";

        this.KeyDown += OnKeyDown;
        menuOptions.Click += (s, e) => OuvrirOptions();
        menuQuitter.Click += async (s, e) => await OnQuitRequest();
        boutonAttaquer.Click += (s, e) => gameLogic.Attaque();
        boutonSoinRapide.Click += (s, e) => SoinRapide();
        boutonRamasser.Click += (s, e) => { Console.WriteLine("Bouton ramasser appuyé"); gameLogic.Ramasser(); };
        boutonJeter.Click += (s, e) => DissiperDrop();
        boutonEquiper.Click += (s, e) => gameLogic.Equiper();
        boutonInventaire.Click += (s, e) =>
            inventaireController.OuvrirInventaire(this.Position.X, this.Position.Y, gameLogic.JeuEnCours.joueur);
        menuSauvegarder.Click += async (s, e) => await Sauvegarde.SauvegarderJeu(gameLogic.JeuEnCours, OptionsController.PlayerName);
        if (Sauvegarde.HasSaves())
            menuCharger.Click += (s, e) => ChargerJeu();
        else
            menuCharger.IsEnabled = false;

        imageDrop.PointerMoved += (s, e) =>
        {
            if (imageDrop.Source != null)
            {
                var point = e.GetPosition(null);
                //TODO menuInfosDrop.(imageDrop, point.X + 10, point.Y - 120);
            }
        };
        //TODO imageDrop.PointerExited += (s, e) => menuInfosDrop.();

        // Initialisation des fenêtres secondaires
        InitializeFenetreInventaire();
        InitializeFenetreOptions();

        // Message de bienvenue
        Dispatcher.UIThread.Post(() =>
                {
                textFlowMessages.Text += "Bienvenue à Kingdom Fall!\n\n";
                textFlowMessages.Foreground = Brushes.White;
                });
    }

    private void OnKeyDown(object sender, KeyEventArgs e)
    {
        switch (e.Key)
        {
            case Key.A:
                if (boutonAttaquer.IsEnabled) gameLogic.Attaque();
                break;
            case Key.I:
                if (boutonInventaire.IsEnabled)
                    inventaireController.OuvrirInventaire(this.Position.X, this.Position.Y, gameLogic.JeuEnCours.joueur);
                break;
            case Key.H:
                if (boutonSoinRapide.IsEnabled) SoinRapide();
                break;
            case Key.R:
                if (boutonRamasser.IsVisible) gameLogic.Ramasser();
                break;
            case Key.J:
                if (boutonJeter.IsVisible) DissiperDrop();
                break;
            case Key.E:
                if (boutonEquiper.IsVisible) gameLogic.Equiper();
                break;
            case Key.Escape:
                OuvrirOptions();
                break;
        }
    }


    private async void ChargerJeu()
    {
        gameLogic.JeuEnCours = await Sauvegarde.ChargerJeu();
        labelNomJoueur.Text = OptionsController.PlayerName;
        gameLogic.JeuEnCours.LoadNouveauDonjon(gameLogic.JeuEnCours.numDonjon);

        if (gameLogic.JeuEnCours.joueur == null || gameLogic.JeuEnCours.ennemiCourant == null)
        {
            throw new exceptions.KFException("Le joueur ou l'ennemi courant est null après le chargement du jeu.");
        }

        labelDonjon.Text = gameLogic.JeuEnCours.numDonjon.ToString();
        labelNomEnnemi.Text = gameLogic.JeuEnCours.ennemiCourant.nom;
        labelAttaqueEnnemie.Text = gameLogic.JeuEnCours.ennemiCourant.attBase.ToString();
        imageEnnemi.Source = new Bitmap($"Resources/images/{gameLogic.JeuEnCours.ennemiCourant.nom.ToLower()}.png");
        boutonInventaire.IsEnabled = true;
        if (gameLogic.JeuEnCours.joueur.inventaire.GetListType(objets.TypeObjet.Potions).Any())
            boutonSoinRapide.IsEnabled = true;

        barreVie.Value = 0;
        barreArmure.Value = 0;
        barreVieEnnemie.Value = 0;
        barreXP.Value = 0;
        AfficherAttaquer(gameLogic.JeuEnCours.joueur);
        AfficherAttaquer(gameLogic.JeuEnCours.ennemiCourant);
        ChangerProgresBarreAnime(barreXP, gameLogic.JeuEnCours.joueur.xp.valeur, xpProgresParIteration);
    }

    public async Task<bool> OnQuitRequest()
    {
        var dialog = new Window
        {
            Title = "Quitter",
            Width = 300,
            Height = 200,
            WindowStartupLocation = WindowStartupLocation.CenterOwner,
            CanResize = false
        };
        var stackPanel = new StackPanel
        {
            Spacing = 10,
            Margin = new Thickness(10)
        };
        stackPanel.Children.Add(new TextBlock { Text = "Quitter Kingdom-Fall ?" });
        stackPanel.Children.Add(new TextBlock { Text = "Êtes-vous sûr de vouloir quitter Kingdom-Fall ?\n\n(Tout changement non sauvegardé sera définitivement perdu)" });
        var okButton = new Button { Content = "OK", Width = 100 };
        var cancelButton = new Button { Content = "Annuler", Width = 100 };
        var buttonPanel = new StackPanel { Orientation = Orientation.Horizontal, Spacing = 10, HorizontalAlignment = HorizontalAlignment.Center };
        buttonPanel.Children.Add(okButton);
        buttonPanel.Children.Add(cancelButton);
        stackPanel.Children.Add(buttonPanel);
        dialog.Content = stackPanel;

        bool result = false;
        okButton.Click += (s, e) => { result = true; dialog.Close(); };
        cancelButton.Click += (s, e) => dialog.Close();
        await dialog.ShowDialog(this);

        if (result)
            Environment.Exit(0);

        return result;
    }

    public static FenetreAppController GetSingleton() => singleton;

    private void OuvrirOptions()
    {
        if (optionsController.StageOptions is null)
        {
            throw new InvalidOperationException("OptionsController n'est pas initialisé.");
        }

        optionsController.StageOptions.Show();
        optionsController.StageOptions.Activate();
    }

    private void SoinRapide()
    {
        var joueurCourant = gameLogic.JeuEnCours.joueur;
    }

    private void InitializeFenetreInventaire()
    {
        var stageInventaire = new Window
        {
            Title = "Kingdom-Fall - Inventaire",
            WindowStartupLocation = WindowStartupLocation.CenterOwner,
            CanResize = false
        };
        var inventaireControllerInstance = new InventaireController();
        stageInventaire.Content = inventaireControllerInstance;
        inventaireController = inventaireControllerInstance;
        inventaireController.SetStageInventaire(stageInventaire);
        inventaireController.SetFenetreAppController(this);
    }

    private void InitializeFenetreOptions()
    {
        var stageOptions = new Window
        {
            Title = "Kingdom-Fall - Options",
            WindowStartupLocation = WindowStartupLocation.CenterOwner,
            CanResize = false
        };
        var optionsControllerInstance = new OptionsController();
        stageOptions.Content = optionsControllerInstance;
        optionsController = optionsControllerInstance;
        optionsController.SetStageOptions(stageOptions);
        optionsController.SetFenetreAppController(this);
        stageOptions.SizeToContent = SizeToContent.WidthAndHeight;
    }

    public void SetThread(GameLogic thread)
    {
        gameLogic = thread;
    }

    public void EnvoyerMessage(string message)
    {
        if (!string.IsNullOrEmpty(message))
        {
            Dispatcher.UIThread.Post(() =>
            {
                textFlowMessages.Text += $"{message}\n\n";
                textFlowMessages.Foreground = Brushes.White;
                scrollMessages.ScrollToEnd();
            });
        }
    }

    public void AfficherEnnemi(Ennemi ennemiAffiche)
    {
        Dispatcher.UIThread.Post(() =>
        {
           imageEnnemi.Source = new Bitmap($"Resources/images/{ennemiAffiche.nom.ToLower()}.png");
           labelNomEnnemi.Text = ennemiAffiche.nom;
           labelVieEnnemi.Text = $"{ennemiAffiche.vieRestante}/{ennemiAffiche.ptsVie}";
           labelAttaqueEnnemie.Text = ennemiAffiche.attBase.ToString();
        });
        ChangerProgresBarreAnime(barreVieEnnemie, (double)ennemiAffiche.vieRestante / ennemiAffiche.ptsVie, vieEnnemieProgresParIteration);
    }

    public void AfficherAttaquer(Entite entiteAttaquee)
    {
        string stringVieRestante = $"{entiteAttaquee.vieRestante}/{entiteAttaquee.ptsVie}";
        double rationVieRestante = (double)entiteAttaquee.vieRestante / entiteAttaquee.ptsVie;

        if (entiteAttaquee is Ennemi ennemi)
        {
            Dispatcher.UIThread.Post(() => labelVieEnnemi.Text = stringVieRestante);
            ChangerProgresBarreAnime(barreVieEnnemie, rationVieRestante, vieEnnemieProgresParIteration);
        }
        else if (entiteAttaquee is Joueur joueur)
        {
            JoueurRecoitAttaque(joueur);
        }
        else
        {
            throw new InvalidOperationException("Unexpected value: " + entiteAttaquee);
        }
    }

    private void JoueurRecoitAttaque(Joueur joueur)
    {
        objets.Armure armure = (objets.Armure)joueur.GetEquip(objets.TypeObjet.Armures);
        if (armure != null && armure.ptsArmure > 0)
        {
            string stringArmureRestante = $"{armure.ptsArmure}/{armure.capaciteArmure}";
            double ratioArmureRestante = (double)armure.ptsArmure / armure.capaciteArmure;
            Dispatcher.UIThread.Post(() => labelArmure.Text = stringArmureRestante);
            ChangerProgresBarreAnime(barreArmure, ratioArmureRestante, armureProgresParIteration);
        }
        else
        {
            if (barreArmure.Value != 0)
            {
                ChangerProgresBarreAnime(barreArmure, 0.0, armureProgresParIteration);
                Dispatcher.UIThread.Post(() => labelArmure.Text = $"0/{((objets.Armure)joueur.GetEquip(objets.TypeObjet.Armures)).capaciteArmure}");
            }
            string stringVieRestante = $"{joueur.vieRestante}/{joueur.ptsVie}";
            double ratioVieRestante = (double)joueur.vieRestante / joueur.ptsVie;
            Dispatcher.UIThread.Post(() => labelVie.Text = stringVieRestante);
            ChangerProgresBarreAnime(barreVie, ratioVieRestante, vieProgresParIteration);
        }
    }

    public void ChangerProgresBarreAnime(ProgressBar barre, double nouvelleValeur, double progresParIteration)
    {
        Dispatcher.UIThread.Post(async () =>
        {
            double ancienneValeur = barre.Value / 100.0;
            if (Math.Abs(ancienneValeur - nouvelleValeur) > 0.0001)
            {
                double progresParIterationCorr = nouvelleValeur > ancienneValeur ? progresParIteration : -progresParIteration;
                int nombreDAnimations = (int)((nouvelleValeur - ancienneValeur) / progresParIterationCorr);
                double progresActuel = ancienneValeur;

                for (int i = 0; i < nombreDAnimations; i++)
                {
                    progresActuel += progresParIterationCorr;
                    barre.Value = progresActuel * 100;
                    await Task.Delay((int)durationPerIteration);
                }
                barre.Value = nouvelleValeur * 100;
            }
        });

        
    }

    public void ActiverNode(string node)
    {
        switch (node)
        {
            case "attaquer":
                Dispatcher.UIThread.Post(() => boutonAttaquer.IsEnabled = true );
                break;
            case "inventaire":
                Dispatcher.UIThread.Post(() => boutonInventaire.IsEnabled = true );
                break;
            case "soin rapide":
                Dispatcher.UIThread.Post(() => boutonSoinRapide.IsEnabled = true );
                break;
        }
    }

    public void EquiperArmure(objets.Armure armure)
    {
        double ratioArmure = (double)armure.ptsArmure / armure.capaciteArmure;
        ChangerProgresBarreAnime(barreArmure, ratioArmure, armureProgresParIteration);
        Dispatcher.UIThread.Post(() => labelArmure.Text = $"{armure.ptsArmure}/{armure.capaciteArmure}");
    }

    public void ResetArmure(int ptsArmure)
    {
        if (ptsArmure != 0)
        {
            ChangerProgresBarreAnime(barreArmure, 1.0, armureProgresParIteration);
            Dispatcher.UIThread.Post(() => labelArmure.Text = $"{ptsArmure}/{ptsArmure}");
        }
    }

    public void GainXp(Joueur joueur, int ancienNiveau, int xpGagne)
    {
        Dispatcher.UIThread.Post(() => labelGainXP.Text = $"+{xpGagne} XP");
        int nbNiveauxGagnes = joueur.niveau - ancienNiveau;

        for (int i = 0; i < nbNiveauxGagnes; i++)
        {
            ChangerProgresBarreAnime(barreXP, 1.0, xpProgresParIteration);
            int niveauCoutant = ancienNiveau + i + 1;
            Dispatcher.UIThread.Post(() => 
            {
                barreXP.Value = 0;
                labelNiveauJoueur.Text = $"{niveauCoutant}";
            });
        }

        ChangerProgresBarreAnime(barreXP, (double)joueur.xp.valeur / joueur.xpCap, xpProgresParIteration);
        Thread.Sleep(500); // Pause pour laisser le temps à l'animation de se terminer
        Dispatcher.UIThread.Post(() => labelGainXP.Text = "");

        if (nbNiveauxGagnes > 0)
            ChangerProgresBarreAnime(barreVie, 1.0, vieProgresParIteration);

        Dispatcher.UIThread.Post(() =>
        {
            labelXPJoueur.Text = $"{(double)joueur.xp.valeur / joueur.xpCap * 100:F1}%";
            labelVie.Text = $"{joueur.vieRestante}/{joueur.ptsVie}";
        });
    }

    public void ShowDrops(Ennemi source, objets.Objet objetChoisi, Joueur joueur)
    {
        Dispatcher.UIThread.Post(() =>
        {
            boutonAttaquer.IsEnabled = false;
            string url = $"Resources/images/{objetChoisi.nom.ToLower()}.png";
            Console.WriteLine(url);
            imageDrop.Source = new Bitmap(url);
            ToolTip.SetTip(imageDrop, objetChoisi.FormatComparedDescription(joueur));
            labelEnnemiLache.Text = $"{source.nom} a làché : ";
            labelItemDrop.Text = objetChoisi.nom;
            boutonRamasser.IsVisible = true;
            boutonJeter.IsVisible = true;
            
            if (objetChoisi.type == objets.TypeObjet.Armes || objetChoisi.type == objets.TypeObjet.Armures)
                boutonEquiper.IsVisible = true;
        });
    }

    public void InventairePlein()
    {
        EnvoyerMessage("Votre inventaire est plein! Veuillez faire de la place ou jeter ce drop.");
    }

    public void DissiperDrop()
    {
        Dispatcher.UIThread.Post(() =>
        {
            labelEnnemiLache.Text = "";
            imageDrop.Source = null;
            labelItemDrop.Text = "";
            boutonRamasser.IsVisible = false;
            boutonJeter.IsVisible = false;
            boutonEquiper.IsVisible = false;
        });
        gameLogic.RelacherLatch();
    }

    public void AfficherDonjon(int donjon)
    {
        Dispatcher.UIThread.Post(() => labelDonjon.Text = donjon.ToString());
    }

    public void AfficherSoin(Entite entite)
    {
        if (entite is Joueur joueur)
        {
            Dispatcher.UIThread.Post(() => labelVie.Text = $"{joueur.vieRestante}/{joueur.ptsVie}");
            ChangerProgresBarreAnime(barreVie, (double)joueur.vieRestante / joueur.ptsVie, vieProgresParIteration);
            boutonSoinRapide.IsEnabled = joueur.vieRestante != joueur.ptsVie || joueur.inventaire.GetListType(objets.TypeObjet.Potions).Any();
        }
        else
        {
            Dispatcher.UIThread.Post(() => labelVieEnnemi.Text = $"{entite.vieRestante}/{entite.ptsVie}");
            ChangerProgresBarreAnime(barreVieEnnemie, (double)entite.vieRestante / entite.ptsVie, vieProgresParIteration);
        }
    }

    public void UpdateUsername(string playerName)
    {
        labelNomJoueur.Text = playerName;
        gameLogic.UpdateUsername(playerName);
    }

    public void SetNbPotions(int nbPotions)
    {
        Dispatcher.UIThread.Post(() => labelPotionsRestantes.Text = $"({nbPotions})");
    }

    public async void JeuTermine()
    {
        var dialog = new Window
        {
            Title = "Partie terminée",
            Width = 300,
            Height = 200,
            WindowStartupLocation = WindowStartupLocation.CenterOwner,
            CanResize = false
        };
        var stackPanel = new StackPanel
        {
            Spacing = 10,
            Margin = new Thickness(10)
        };
        stackPanel.Children.Add(new TextBlock { Text = "Votre joueur est mort..." });
        stackPanel.Children.Add(new TextBlock { Text = "Vous pouvez commencer une nouvelle partie ou choisir une ancienne sauvegarde." });
        var quitButton = new Button { Content = "Quitter", Width = 100 };
        var newGameButton = new Button { Content = "Nouvelle partie", Width = 100 };
        var oldSaveButton = new Button { Content = "Charger sauv.", Width = 100 };
        var buttonPanel = new StackPanel { Orientation = Orientation.Horizontal, Spacing = 10, HorizontalAlignment = HorizontalAlignment.Center };
        buttonPanel.Children.Add(quitButton);
        buttonPanel.Children.Add(newGameButton);
        buttonPanel.Children.Add(oldSaveButton);
        stackPanel.Children.Add(buttonPanel);
        dialog.Content = stackPanel;

        quitButton.Click += async (s, e) =>
        {
            if (!await OnQuitRequest())
                JeuTermine();
            dialog.Close();
        };
        newGameButton.Click += (s, e) =>
        {
            InitializeComponent();
            gameLogic = new App.GameLogic(this);
            gameLogic.Run();
            dialog.Close();
        };
        oldSaveButton.Click += (s, e) =>
        {
            if (Sauvegarde.HasSaves())
                ChargerJeu();
            dialog.Close();
        };

        await dialog.ShowDialog(this);
    }
}
