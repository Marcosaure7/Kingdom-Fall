using Avalonia.LogicalTree;
using Avalonia.Themes.Fluent;

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
    private readonly double _vieProgresParIteration = 0.025;
    private readonly double _vieEnnemieProgresParIteration = 0.025;
    private readonly double _xpProgresParIteration = 0.01;
    private readonly double _armureProgresParIteration = 0.03;
    private readonly double _durationPerIteration = 10;

    private GameLogic _gameLogic;
    private InventaireController _inventaireController;
    private OptionsController _optionsController;
    private static FenetreAppController _singleton;

    private static int _nbMessages = 0;

    // Contrôles Avalonia
    private ProgressBar _barreVie;
    private ProgressBar _barreArmure;
    private ProgressBar _barreVieEnnemie;
    private ProgressBar _barreXp;
    private Button _boutonAttaquer;
    private Button _boutonEquiper;
    private Button _boutonInventaire;
    private Button _boutonJeter;
    private Button _boutonRamasser;
    private Button _boutonSoinRapide;
    private TextBlock _labelAttaqueEnnemie;
    private TextBlock _labelDonjon;
    private TextBlock _labelGainXp;
    private TextBlock _labelEnnemiLache;
    private TextBlock _labelItemDrop;
    private TextBlock _labelNiveauJoueur;
    private TextBlock _labelNomEnnemi;
    private TextBlock _labelNomJoueur;
    private TextBlock _labelPotionsRestantes;
    private TextBlock _labelVie;
    private TextBlock _labelArmure;
    private TextBlock _labelVieEnnemi;
    private TextBlock _labelXpJoueur;
    private MenuItem _menuOptions;
    private MenuItem _menuCharger;
    private MenuItem _menuQuitter;
    private MenuItem _menuSauvegarder;
    private ScrollViewer _scrollMessages;
    private Image _imageDrop;
    private Image _imageEnnemi;

    public FenetreAppController()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        AvaloniaXamlLoader.Load(this);
        _singleton = this;
        
        // Initialisation des contrôles (normalement définis dans XAML)
        _barreVie = this.FindControl<ProgressBar>("BarreVie");
        _barreArmure = this.FindControl<ProgressBar>("BarreArmure");
        _barreVieEnnemie = this.FindControl<ProgressBar>("BarreVieEnnemie");
        _barreXp = this.FindControl<ProgressBar>("BarreXp");
        _boutonAttaquer = this.FindControl<Button>("BoutonAttaquer");
        _boutonEquiper = this.FindControl<Button>("BoutonEquiper");
        _boutonInventaire = this.FindControl<Button>("BoutonInventaire");
        _boutonJeter = this.FindControl<Button>("BoutonJeter");
        _boutonRamasser = this.FindControl<Button>("BoutonRamasser");
        _boutonSoinRapide = this.FindControl<Button>("BoutonSoinRapide");
        _labelAttaqueEnnemie = this.FindControl<TextBlock>("LabelAttaqueEnnemie");
        _labelDonjon = this.FindControl<TextBlock>("LabelDonjon");
        _labelGainXp = this.FindControl<TextBlock>("LabelGainXp");
        _labelEnnemiLache = this.FindControl<TextBlock>("LabelEnnemiLache");
        _labelItemDrop = this.FindControl<TextBlock>("LabelItemDrop");
        _labelNiveauJoueur = this.FindControl<TextBlock>("LabelNiveauJoueur");
        _labelNomEnnemi = this.FindControl<TextBlock>("LabelNomEnnemi");
        _labelNomJoueur = this.FindControl<TextBlock>("LabelNomJoueur");
        _labelPotionsRestantes = this.FindControl<TextBlock>("LabelPotionsRestantes");
        _labelVie = this.FindControl<TextBlock>("LabelVie");
        _labelArmure = this.FindControl<TextBlock>("LabelArmure");
        _labelVieEnnemi = this.FindControl<TextBlock>("LabelVieEnnemi");
        _labelXpJoueur = this.FindControl<TextBlock>("LabelXpJoueur");
        _menuOptions = this.FindControl<MenuItem>("MenuOptions");
        _menuCharger = this.FindControl<MenuItem>("MenuCharger");
        _menuQuitter = this.FindControl<MenuItem>("MenuQuitter");
        _menuSauvegarder = this.FindControl<MenuItem>("MenuSauvegarder");
        _scrollMessages = this.FindControl<ScrollViewer>("ScrollMessages");
        _imageDrop = this.FindControl<Image>("ImageDrop");
        _imageEnnemi = this.FindControl<Image>("ImageEnnemi");
        StackMessages = this.FindControl<StackPanel>("StackMessages");

        if (_barreVie == null || _barreArmure == null || _barreVieEnnemie == null || _barreXp == null ||
            _boutonAttaquer == null || _boutonEquiper == null || _boutonInventaire == null || _boutonJeter == null ||
            _boutonRamasser == null || _boutonSoinRapide == null || _labelAttaqueEnnemie == null ||
            _labelDonjon == null || _labelGainXp == null || _labelEnnemiLache == null || _labelItemDrop == null ||
            _labelNiveauJoueur == null || _labelNomEnnemi == null || _labelNomJoueur == null ||
            _labelPotionsRestantes == null || _labelVie == null || _labelArmure == null ||
            _labelVieEnnemi == null || _labelXpJoueur == null || _menuOptions == null ||
            _menuCharger == null || _menuQuitter == null || _menuSauvegarder == null ||
            _scrollMessages == null || _imageDrop == null || _imageEnnemi == null || StackMessages == null)
        {
            throw new InvalidOperationException("Un ou plusieurs contrôles requis sont manquants.");
        }

        // Initialisation des valeurs par défaut
        _imageEnnemi.Source = null;
        _imageDrop.Source = null;
        _labelGainXp.Text = "";
        _labelEnnemiLache.Text = "";
        _labelItemDrop.Text = "";
        _labelDonjon.Text = "0";
        _labelAttaqueEnnemie.Text = "";
        _labelNiveauJoueur.Text = "0";
        _labelNomEnnemi.Text = "";
        _labelArmure.Text = "0";
        _labelXpJoueur.Text = "0.0%";
        _labelVieEnnemi.Text = "";
        _labelPotionsRestantes.Text = "(0)";
        _barreVie.Value = 100;
        _barreArmure.Value = 0;
        _barreXp.Value = 0;
        _barreVieEnnemie.Value = 0;
        _boutonInventaire.IsEnabled = false;
        _boutonSoinRapide.IsEnabled = false;
        _boutonAttaquer.IsEnabled = false;
        _boutonRamasser.IsVisible = false;
        _boutonJeter.IsVisible = false;
        _boutonEquiper.IsVisible = false;

        this.KeyDown += OnKeyDown;
        _menuOptions.Click += (s, e) => OuvrirOptions();
        _menuQuitter.Click += async (s, e) => await OnQuitRequest();
        _boutonAttaquer.Click += (s, e) => _gameLogic.Attaque();
        _boutonSoinRapide.Click += (s, e) => SoinRapide();
        _boutonRamasser.Click += (s, e) => { Console.WriteLine("Bouton ramasser appuyé"); _gameLogic.Ramasser(); };
        _boutonJeter.Click += (s, e) => DissiperDrop();
        _boutonEquiper.Click += (s, e) => _gameLogic.Equiper();
        _boutonInventaire.Click += (s, e) =>
        {
            InitializeFenetreInventaire();
            _inventaireController.OuvrirInventaire(this.Position.X, this.Position.Y, _gameLogic.JeuEnCours.joueur);
        };
            
        _menuSauvegarder.Click += async (s, e) => await Sauvegarde.SauvegarderJeu(_gameLogic.JeuEnCours, OptionsController.PlayerName);
        if (Sauvegarde.HasSaves())
            _menuCharger.Click += (s, e) => ChargerJeu();
        else
            _menuCharger.IsEnabled = false;

        // Initialisation des fenêtres secondaires
        InitializeFenetreInventaire();
        InitializeFenetreOptions();

        // Message de bienvenue
        EnvoyerMessage("Bienvenue à Kingdom Fall!");
    }

    private void OnKeyDown(object sender, KeyEventArgs e)
    {
        switch (e.Key)
        {
            case Key.I:
                if (_boutonInventaire.IsEnabled)
                    _inventaireController.OuvrirInventaire(this.Position.X, this.Position.Y, _gameLogic.JeuEnCours.joueur);
                break;
            case Key.H:
                if (_boutonSoinRapide.IsEnabled) SoinRapide();
                break;
            case Key.R:
                if (_boutonRamasser.IsVisible) _gameLogic.Ramasser();
                break;
            case Key.J:
                if (_boutonJeter.IsVisible) DissiperDrop();
                break;
            case Key.E:
                if (_boutonEquiper.IsVisible) _gameLogic.Equiper();
                break;
            case Key.Escape:
                OuvrirOptions();
                break;
        }
    }


    private async void ChargerJeu()
    {
        _gameLogic.JeuEnCours = await Sauvegarde.ChargerJeu();
        _labelNomJoueur.Text = OptionsController.PlayerName;
        _gameLogic.JeuEnCours.LoadNouveauDonjon(_gameLogic.JeuEnCours.numDonjon);

        if (_gameLogic.JeuEnCours.joueur == null || _gameLogic.JeuEnCours.ennemiCourant == null)
        {
            throw new exceptions.KFException("Le joueur ou l'ennemi courant est null après le chargement du jeu.");
        }

        _labelDonjon.Text = _gameLogic.JeuEnCours.numDonjon.ToString();
        _labelNomEnnemi.Text = _gameLogic.JeuEnCours.ennemiCourant.nom;
        _labelAttaqueEnnemie.Text = _gameLogic.JeuEnCours.ennemiCourant.attBase.ToString();
        _imageEnnemi.Source = new Bitmap($"Resources/images/{_gameLogic.JeuEnCours.ennemiCourant.nom.ToLower()}.png");
        _boutonInventaire.IsEnabled = true;
        if (_gameLogic.JeuEnCours.joueur.inventaire.GetListType(objets.TypeObjet.Potions).Any())
            _boutonSoinRapide.IsEnabled = true;

        _barreVie.Value = 0;
        _barreArmure.Value = 0;
        _barreVieEnnemie.Value = 0;
        _barreXp.Value = 0;
        AfficherAttaquer(_gameLogic.JeuEnCours.joueur);
        AfficherAttaquer(_gameLogic.JeuEnCours.ennemiCourant);
        ChangerProgresBarreAnime(_barreXp, _gameLogic.JeuEnCours.joueur.xp.valeur, _xpProgresParIteration);
    }

    /// <summary>
    /// PLEASE CALL ON UITHREAD !!!
    /// </summary>
    /// <returns></returns>
    public async Task<bool> OnQuitRequest()
    {
        var dialog = new Window
        {
            Title = "Quitter",
            Width = 400,
            Height = 200,
            WindowStartupLocation = WindowStartupLocation.CenterOwner,
            CanResize = false
        };
        dialog.Styles.Add(new FluentTheme());
        var stackPanel = new StackPanel
        {
            Spacing = 10,
            Margin = new Thickness(10)
        };
        stackPanel.Children.Add(new TextBlock { Text = "Quitter Kingdom-Fall ?" });
        stackPanel.Children.Add(new TextBlock
        {
            TextWrapping = TextWrapping.Wrap,
            Text = "Êtes-vous sûr de vouloir quitter Kingdom-Fall ?\n\n(Tout changement non sauvegardé sera définitivement perdu)"
        });
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

    public static FenetreAppController GetSingleton() => _singleton;

    private void OuvrirOptions()
    {
        if (_optionsController.StageOptions is null)
        {
            throw new InvalidOperationException("OptionsController n'est pas initialisé.");
        }

        _optionsController.StageOptions.Show();
        _optionsController.StageOptions.Activate();
    }

    private void SoinRapide()
    {
        var joueurCourant = _gameLogic.JeuEnCours.joueur;
        var potions = joueurCourant.inventaire.GetListType(objets.TypeObjet.Potions);

        if (potions.Count > 0)
            joueurCourant.inventaire.Utiliser(potions.First(), 0);
        else
            EnvoyerMessage("Vous n'avez pas de potions dans votre inventaire.");

        SetNbPotions(potions.Count);

        if (potions.Count == 0)
            BoutonSoinRapide.IsEnabled = true;
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
        _inventaireController = inventaireControllerInstance;
        _inventaireController.SetStageInventaire(stageInventaire);
        _inventaireController.SetFenetreAppController(this);
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
        _optionsController = optionsControllerInstance;
        _optionsController.SetStageOptions(stageOptions);
        _optionsController.SetFenetreAppController(this);
        stageOptions.SizeToContent = SizeToContent.WidthAndHeight;
    }

    public void SetThread(GameLogic thread)
    {
        _gameLogic = thread;
    }

    public void EnvoyerMessage(string message)
    {
        if (!string.IsNullOrEmpty(message))
        {
            Dispatcher.UIThread.Post(() =>
            {
                TextBlock textBlockMessage = new TextBlock
                {
                    Text = message + "\n",
                    TextWrapping = TextWrapping.Wrap,
                    Foreground = Brushes.White,
                };
                StackMessages.Children.Add(textBlockMessage);
                StackMessages.Height += textBlockMessage.Height;
                _scrollMessages.ScrollToEnd();
                
            });
        }
    }

    public void AfficherEnnemi(Ennemi ennemiAffiche)
    {
        Dispatcher.UIThread.Post(() =>
        {
           _imageEnnemi.Source = new Bitmap($"Resources/images/{ennemiAffiche.nom.ToLower()}.png");
           _labelNomEnnemi.Text = ennemiAffiche.nom;
           _labelVieEnnemi.Text = $"{ennemiAffiche.vieRestante}/{ennemiAffiche.ptsVie}";
           _labelAttaqueEnnemie.Text = ennemiAffiche.attBase.ToString();
        });
        ChangerProgresBarreAnime(_barreVieEnnemie, (double)ennemiAffiche.vieRestante / ennemiAffiche.ptsVie, _vieEnnemieProgresParIteration);
    }

    public void AfficherAttaquer(Entite entiteAttaquee)
    {
        string stringVieRestante = $"{entiteAttaquee.vieRestante}/{entiteAttaquee.ptsVie}";
        double rationVieRestante = (double)entiteAttaquee.vieRestante / entiteAttaquee.ptsVie;

        if (entiteAttaquee is Ennemi ennemi)
        {
            Dispatcher.UIThread.Post(() => _labelVieEnnemi.Text = stringVieRestante);
            ChangerProgresBarreAnime(_barreVieEnnemie, rationVieRestante, _vieEnnemieProgresParIteration);
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
            Dispatcher.UIThread.Post(() => _labelArmure.Text = stringArmureRestante);
            ChangerProgresBarreAnime(_barreArmure, ratioArmureRestante, _armureProgresParIteration);
        }
        if (armure != null && armure.ptsArmure == 0)
        {
            ChangerProgresBarreAnime(_barreArmure, 0.0, _armureProgresParIteration);
            Dispatcher.UIThread.Post(() => _labelArmure.Text = $"0/{((objets.Armure)joueur.GetEquip(objets.TypeObjet.Armures)).capaciteArmure}");
        }
        
        if (armure == null || armure.ptsArmure == 0)
        {
            string stringVieRestante = $"{joueur.vieRestante}/{joueur.ptsVie}";
            double ratioVieRestante = (double)joueur.vieRestante / joueur.ptsVie;
            Dispatcher.UIThread.Post(() => _labelVie.Text = stringVieRestante);
            ChangerProgresBarreAnime(_barreVie, ratioVieRestante, _vieProgresParIteration);
        }
    }

    public void ChangerProgresBarreAnime(ProgressBar barre, double nouvelleValeur, double progresParIteration)
    {

        double ancienneValeur = 0;
        Dispatcher.UIThread.Invoke(() =>  ancienneValeur = barre.Value);
        if (!(Math.Abs(ancienneValeur - nouvelleValeur) > 0.0001)) return;
        
        double progresParIterationCorr =
            nouvelleValeur > ancienneValeur ? progresParIteration : -progresParIteration;
        int nombreDAnimations = (int)((nouvelleValeur - ancienneValeur) / progresParIterationCorr);
        double progresActuel = ancienneValeur;
        for (int i = 0; i < nombreDAnimations; i++)
        {
            Dispatcher.UIThread.Post(() =>
            {
                progresActuel += progresParIterationCorr;
                barre.Value = progresActuel;
                Thread.Sleep((int)_durationPerIteration);
            });
        }
        Dispatcher.UIThread.Post(() => barre.Value = nouvelleValeur);
    }

    /// <summary>
    /// Override for xp show
    /// </summary>
    /// <param name="nouvelleValeur"></param>
    public void ChangerXpProgresAnime(double nouvelleValeur)
    {
        double ancienneValeur = 0;
        Dispatcher.UIThread.Invoke(() =>  ancienneValeur = _barreXp.Value);
        if (!(Math.Abs(ancienneValeur - nouvelleValeur) > 0.0001)) return;
        
        int nombreDAnimations = (int)((nouvelleValeur - ancienneValeur) / _xpProgresParIteration);
        double progresActuel = ancienneValeur;
        for (int i = 0; i < nombreDAnimations; i++)
        {
            Dispatcher.UIThread.Post(() =>
            {
                progresActuel += _xpProgresParIteration;
                _barreXp.Value = progresActuel;
                _labelXpJoueur.Text = $"{progresActuel * 100:F1}%";
                Thread.Sleep((int)_durationPerIteration);
            });
        }
        Dispatcher.UIThread.Post(() => _barreXp.Value = nouvelleValeur);
        Dispatcher.UIThread.Post(() => _labelXpJoueur.Text = $"{nouvelleValeur * 100:F1}%");
    }

    public void ActiverNode(string node)
    {
        switch (node)
        {
            case "attaquer":
                Dispatcher.UIThread.Post(() => _boutonAttaquer.IsEnabled = true );
                break;
            case "inventaire":
                Dispatcher.UIThread.Post(() => _boutonInventaire.IsEnabled = true );
                break;
            case "soin rapide":
                Dispatcher.UIThread.Post(() => _boutonSoinRapide.IsEnabled = true );
                break;
        }
    }

    public void EquiperArmure(objets.Armure armure)
    {
        double ratioArmure = (double)armure.ptsArmure / armure.capaciteArmure;
        ChangerProgresBarreAnime(_barreArmure, ratioArmure, _armureProgresParIteration);
        Dispatcher.UIThread.Post(() => _labelArmure.Text = $"{armure.ptsArmure}/{armure.capaciteArmure}");
    }

    public void ResetArmure(int ptsArmure)
    {
        if (ptsArmure != 0)
        {
            ChangerProgresBarreAnime(_barreArmure, 1.0, _armureProgresParIteration);
            Dispatcher.UIThread.Post(() => _labelArmure.Text = $"{ptsArmure}/{ptsArmure}");
        }
    }

    public void GainXp(Joueur joueur, int ancienNiveau, int xpGagne)
    {
        Dispatcher.UIThread.Post(() => _labelGainXp.Text = $"+{xpGagne} XP");
        int nbNiveauxGagnes = joueur.niveau - ancienNiveau;

        for (int i = 0; i < nbNiveauxGagnes; i++)
        {
            ChangerXpProgresAnime(1.0);
            int niveauCoutant = ancienNiveau + i + 1;
            Dispatcher.UIThread.Post(() => 
            {
                _barreXp.Value = 0;
                _labelNiveauJoueur.Text = $"{niveauCoutant}";
                _labelXpJoueur.Text = "0.0%";
            });
        }

        ChangerXpProgresAnime((double)joueur.xp.valeur / joueur.xpCap);
        Dispatcher.UIThread.Post(() => _labelGainXp.Text = "");

        if (nbNiveauxGagnes > 0)
            ChangerProgresBarreAnime(_barreVie, 1.0, _vieProgresParIteration);

        Dispatcher.UIThread.Post(() => _labelVie.Text = $"{joueur.vieRestante}/{joueur.ptsVie}");
    }

    public void ShowDrops(Ennemi source, objets.Objet objetChoisi, Joueur joueur)
    {
        Dispatcher.UIThread.Post(() =>
        {
            _boutonAttaquer.IsEnabled = false;
            string url = $"Resources/images/{objetChoisi.nom.ToLower()}.png";
            Console.WriteLine(url);
            _imageDrop.Source = new Bitmap(url);
            ToolTip.SetTip(_imageDrop, objetChoisi.FormatComparedDescription(joueur));
            _labelEnnemiLache.Text = $"{source.nom} a làché : ";
            _labelItemDrop.Text = objetChoisi.nom;
            _boutonRamasser.IsVisible = true;
            _boutonJeter.IsVisible = true;
            
            if (objetChoisi.type == objets.TypeObjet.Armes || objetChoisi.type == objets.TypeObjet.Armures)
                _boutonEquiper.IsVisible = true;
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
            _labelEnnemiLache.Text = "";
            _imageDrop.Source = null;
            _labelItemDrop.Text = "";
            _boutonRamasser.IsVisible = false;
            _boutonJeter.IsVisible = false;
            _boutonEquiper.IsVisible = false;
        });
        _gameLogic.RelacherLatch();
    }

    public void AfficherDonjon(int donjon)
    {
        Dispatcher.UIThread.Post(() => _labelDonjon.Text = donjon.ToString());
    }

    public void AfficherSoin(Entite entite)
    {
        if (entite is Joueur joueur)
        {
            Dispatcher.UIThread.Post(() => _labelVie.Text = $"{joueur.vieRestante}/{joueur.ptsVie}");
            ChangerProgresBarreAnime(_barreVie, (double)joueur.vieRestante / joueur.ptsVie, _vieProgresParIteration);
            _boutonSoinRapide.IsEnabled = joueur.vieRestante != joueur.ptsVie || joueur.inventaire.GetListType(objets.TypeObjet.Potions).Any();
        }
        else
        {
            Dispatcher.UIThread.Post(() => _labelVieEnnemi.Text = $"{entite.vieRestante}/{entite.ptsVie}");
            ChangerProgresBarreAnime(_barreVieEnnemie, (double)entite.vieRestante / entite.ptsVie, _vieProgresParIteration);
        }
    }

    public void UpdateUsername(string playerName)
    {
        _labelNomJoueur.Text = playerName;
        _gameLogic.UpdateUsername(playerName);
    }

    public void SetNbPotions(int nbPotions)
    {
        Dispatcher.UIThread.Post(() => _labelPotionsRestantes.Text = $"({nbPotions})");
    }

    /// <summary>
    /// PLEASE CALL ME ON UITHREAD !!!
    /// </summary>
    public async void JeuTermine()
    {
        var dialog = new Window
        {
            Title = "Partie terminée",
            Width = 400,
            Height = 200,
            WindowStartupLocation = WindowStartupLocation.CenterOwner,
            CanResize = false
        };
        dialog.Styles.Add(new FluentTheme());
        var stackPanel = new StackPanel
        {
            Spacing = 10,
            Margin = new Thickness(10)
        };
        stackPanel.Children.Add(new TextBlock { Text = "Votre joueur est mort..." });
        stackPanel.Children.Add(new TextBlock
        {
            Text = "Vous pouvez commencer une nouvelle partie ou choisir une ancienne sauvegarde.",
            TextWrapping  = TextWrapping.Wrap
        });
        var quitButton = new Button { Content = "Quitter"};
        var newGameButton = new Button { Content = "Nouvelle partie"};
        var oldSaveButton = new Button { Content = "Charger sauv."};
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
            _gameLogic = new App.GameLogic(this);
            _gameLogic.Run();
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
