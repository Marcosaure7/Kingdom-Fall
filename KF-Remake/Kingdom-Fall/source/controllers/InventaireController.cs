using Avalonia;
using Avalonia.Controls;
using Avalonia.Controls.Shapes;
using Avalonia.Input;
using Avalonia.Interactivity;
using Avalonia.Media;
using Avalonia.Media.Imaging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using personnages;
using objets;
using Avalonia.Threading;
using Avalonia.Layout;
using Avalonia.Markup.Xaml;
using Avalonia.Markup.Xaml.Styling;
using Avalonia.Styling;
using Avalonia.Themes.Fluent;

namespace controllers
{
    public partial class InventaireController : UserControl
    {
        private Window _stageInventaire;
        private Joueur? _joueur;
        private ObjetSlot _emphasizedSlot;
        private TextBlock _labelArmes;
        private TextBlock _labelArmesOuvert;
        private TextBlock _labelArmures;
        private TextBlock _labelArmuresOuvert;
        private TextBlock _labelDescriptionEquipee;
        private TextBlock _labelDivers;
        private TextBlock _labelDiversOuvert;
        private TextBlock _labelPotions;
        private TextBlock _labelPotionsOuvert;
        private WrapPanel _paneObjets;
        private StackPanel _vboxEquipe;
        private Image _imageObjetEquipe;
        private FenetreAppController _appController;

        public InventaireController()
        {
            InitializeComponent();
        }

        private void InitializeComponent()
        {
            AvaloniaXamlLoader.Load(this);

            ObjetSlot._inventaireController = this;

            _labelArmes = this.FindControl<TextBlock>("LabelArmes");
            _labelArmesOuvert = this.FindControl<TextBlock>("LabelArmesOuvert");
            _labelArmures = this.FindControl<TextBlock>("LabelArmures");
            _labelArmuresOuvert = this.FindControl<TextBlock>("LabelArmuresOuvert");
            _labelDescriptionEquipee = this.FindControl<TextBlock>("LabelDescriptionEquipee");
            _labelDivers = this.FindControl<TextBlock>("LabelDivers");
            _labelDiversOuvert = this.FindControl<TextBlock>("LabelDiversOuvert");
            _labelPotions = this.FindControl<TextBlock>("LabelPotions");
            _labelPotionsOuvert = this.FindControl<TextBlock>("LabelPotionsOuvert");
            _paneObjets = this.FindControl<WrapPanel>("PaneObjets");
            _vboxEquipe = this.FindControl<StackPanel>("VboxEquipe");
            _imageObjetEquipe = this.FindControl<Image>("ImageObjetEquipe");

            _labelArmesOuvert.Text = "";
            _labelArmuresOuvert.Text = "";
            _labelPotionsOuvert.Text = "";
            _labelDiversOuvert.Text = "";
            _imageObjetEquipe.Source = null;
            _labelDescriptionEquipee.Text = "";
            _vboxEquipe.IsVisible = false;

            _labelArmes.PointerPressed += (s, e) =>
            {
                if (e.GetCurrentPoint(null).Properties.IsLeftButtonPressed)
                {
                    _labelArmesOuvert.Text = " >";
                    _labelArmuresOuvert.Text = "";
                    _labelPotionsOuvert.Text = "";
                    _labelDiversOuvert.Text = "";
                    OuvrirEquipement(TypeObjet.Armes);
                }
            };

            _labelArmures.PointerPressed += (s, e) =>
            {
                if (e.GetCurrentPoint(null).Properties.IsLeftButtonPressed)
                {
                    _labelArmesOuvert.Text = "";
                    _labelArmuresOuvert.Text = " >";
                    _labelPotionsOuvert.Text = "";
                    _labelDiversOuvert.Text = "";
                    OuvrirEquipement(TypeObjet.Armures);
                }
            };

            _labelPotions.PointerPressed += (s, e) =>
            {
                if (e.GetCurrentPoint(null).Properties.IsLeftButtonPressed)
                {
                    _labelArmesOuvert.Text = "";
                    _labelArmuresOuvert.Text = "";
                    _labelPotionsOuvert.Text = " >";
                    _labelDiversOuvert.Text = "";
                    OuvrirEquipement(TypeObjet.Potions);
                }
            };

            _labelDivers.PointerPressed += (s, e) =>
            {
                if (e.GetCurrentPoint(null).Properties.IsLeftButtonPressed)
                {
                    _labelArmesOuvert.Text = "";
                    _labelArmuresOuvert.Text = "";
                    _labelPotionsOuvert.Text = "";
                    _labelDiversOuvert.Text = " >";
                    OuvrirEquipement(TypeObjet.Divers);
                }
            };

            this.KeyDown += OnKeyDown;
        }

        private void OnKeyDown(object? sender, KeyEventArgs e)
        {
            if (e.Key == Key.Left || e.Key == Key.Right)
            {
                Console.WriteLine($"Key pressed: {e.Key}");
                int leftOrRight = e.Key == Key.Left ? -1 : 1;
                int emphasizedSlotIndex = _emphasizedSlot != null ? _emphasizedSlot.Index : 0;
                int nextEmphasizedSlotIndex = GetNextEmphasizedSlotIndex(emphasizedSlotIndex, leftOrRight);
                var nextEmphasizedSlot = _paneObjets.Children[nextEmphasizedSlotIndex] as ObjetSlot;

                if (nextEmphasizedSlot != null && nextEmphasizedSlot.ObjetStock != null)
                {
                    nextEmphasizedSlot.SetEmphasized(true);
                }
            }
        }

        private int GetNextEmphasizedSlotIndex(int emphasizedSlotIndex, int leftOrRight)
        {
            int nextEmphasizedSlot = emphasizedSlotIndex + leftOrRight;

            if (nextEmphasizedSlot >= _paneObjets.Children.Count ||
                (_paneObjets.Children[nextEmphasizedSlot] as ObjetSlot)?.ObjetStock == null)
            {
                nextEmphasizedSlot = 0;
            }
            else if (nextEmphasizedSlot < 0)
            {
                nextEmphasizedSlot = _paneObjets.Children.Count - 1;
            }
            return nextEmphasizedSlot;
        }

        public void SetStageInventaire(Window stageInventaire)
        {
            _stageInventaire = stageInventaire;
            _stageInventaire.KeyDown += OnKeyDown;
        }

        public void SetFenetreAppController(FenetreAppController controller)
        {
            _appController = controller;
        }

        public void OuvrirInventaire(double coordX, double coordY, Joueur? joueur)
        {
            _joueur = joueur;
            _labelArmesOuvert.Text = "";
            _labelArmuresOuvert.Text = "";
            _labelPotionsOuvert.Text = "";
            _labelDiversOuvert.Text = "";
            _imageObjetEquipe.Source = null;
            _labelDescriptionEquipee.Text = "";
            _vboxEquipe.IsVisible = false;
            _paneObjets.Children.Clear();

            _stageInventaire.Position = new PixelPoint((int)coordX, (int)coordY);
            _stageInventaire.Show();
            _stageInventaire.Activate();
        }

        private void OuvrirEquipement(TypeObjet typeObjet)
        {
            Dispatcher.UIThread.Post(() =>
            {
                _paneObjets.Children.Clear();
                _imageObjetEquipe.Source = null;
                _labelDescriptionEquipee.Text = "";
                _vboxEquipe.IsVisible = true;
            });

            if (typeObjet.IsEquipable())
            {
                Objet? equipee = _joueur?.GetEquip(typeObjet);
                Dispatcher.UIThread.Post(() =>
                {
                    if (equipee != null)
                    {
                        _imageObjetEquipe.Source = new Bitmap($"Resources/images/{equipee.nom.ToLower()}.png");
                        _labelDescriptionEquipee.Text = equipee.GetDescription();
                    }
                    else
                    {
                        _imageObjetEquipe.Source = null;
                        _labelDescriptionEquipee.Text = "Aucun objet équipé";
                    }
                });
            }
            AfficherObjets(typeObjet);
        }

        private void AfficherObjets(TypeObjet typeObjet)
        {
            Dispatcher.UIThread.Post(() => _paneObjets.Children.Clear());
            var objetsSlotInventaire = _joueur?.inventaire.GetListType(typeObjet) ?? new List<Objet>();

            for (int i = 0; i < TypeObjetExtensions.GetEspaceInventaire(typeObjet); i++)
            {
                Border border = new Border();
                Dispatcher.UIThread.Post(() => _paneObjets.Children.Add(border));
                ObjetSlot objetSlot = i >= objetsSlotInventaire.Count || objetsSlotInventaire[i] == null
                    ? new ObjetSlot(typeObjet, null, i)
                    : new ObjetSlot(typeObjet, objetsSlotInventaire[i], i);
                Dispatcher.UIThread.Post(() => border.Child = objetSlot);
            }
        }

        private void EquiperObjet(ObjetSlot objetSlot)
        {
            objetSlot.SetEmphasized(false);
            _emphasizedSlot = null;
            _joueur?.inventaire.Equiper(objetSlot.ObjetStock, objetSlot.Index);
            OuvrirEquipement(objetSlot.Type);
        }

        private void UtiliserObjet(ObjetSlot objetSlot)
        {
            _joueur?.inventaire.Utiliser(objetSlot.ObjetStock, objetSlot.Index);
            if (objetSlot.Type == TypeObjet.Potions || objetSlot.Type == TypeObjet.Divers)
            {
                OuvrirEquipement(objetSlot.Type);
            }
            if (objetSlot.Type == TypeObjet.Potions)
            {
                _appController.SetNbPotions(_joueur?.inventaire.GetListType(TypeObjet.Potions).Count ?? 0);
            }
        }

        private async void JeterObjet(ObjetSlot objetSlot)
        {
            bool confirmerJeter = OptionsController.CONFIRMER_JETER;
            bool resultat = true;

            if (confirmerJeter)
            {
                var dialog = new Window
                {
                    Title = "Jeter l'objet",
                    Width = 300,
                    Height = 200,
                    WindowStartupLocation = WindowStartupLocation.CenterOwner,
                    CanResize = false,
                    
                };
                dialog.Styles.Add(new FluentTheme());
                var stackPanel = new StackPanel
                {
                    Spacing = 10,
                    Margin = new Thickness(10)
                };
                stackPanel.Children.Add(new TextBlock { Text = $"Voulez-vous jeter '{objetSlot.ObjetStock?.nom}' ?" });
                stackPanel.Children.Add(new TextBlock { Text = "Cet objet sera perdu définitivement." });
                var okButton = new Button { Content = "OK", Width = 100 };
                var cancelButton = new Button { Content = "Annuler", Width = 100 };
                var buttonPanel = new StackPanel { Orientation = Orientation.Horizontal, Spacing = 10, HorizontalAlignment = HorizontalAlignment.Center };
                buttonPanel.Children.Add(okButton);
                buttonPanel.Children.Add(cancelButton);
                stackPanel.Children.Add(buttonPanel);
                dialog.Content = stackPanel;

                bool dialogResult = false;
                okButton.Click += (s, e) => { dialogResult = true; dialog.Close(); };
                cancelButton.Click += (s, e) => dialog.Close();
                await dialog.ShowDialog(_stageInventaire);

                resultat = dialogResult;
            }

            if (resultat)
            {
                _joueur?.inventaire.Jeter(objetSlot.Type, objetSlot.Index);
                _appController.SetNbPotions(_joueur?.inventaire.GetListType(TypeObjet.Potions).Count ?? 0);
                AfficherObjets(objetSlot.Type);
            }
        }

        private class ObjetSlot : StackPanel
        {
            public static InventaireController? _inventaireController;
            public TypeObjet Type { get; }
            public Objet? ObjetStock { get; }
            public int Index { get; }
            private TextBlock LabelNom { get; }
            private bool Emphasized { get; set; }
            private DateTime _lastClickTime;
            private const int DoubleClickTimeMs = 300;

            public ObjetSlot(TypeObjet type, Objet? objet, int index)
            {
                Type = type;
                ObjetStock = objet;
                Index = index;
                Emphasized = false;
                MinWidth = 100;
                MinHeight = 120;
                VerticalAlignment = VerticalAlignment.Center;
                HorizontalAlignment = HorizontalAlignment.Left;

                if (objet != null)
                {
                    var imageObjet = new Image
                    {
                        Source = new Bitmap($"Resources/images/{objet.nom.ToLower()}.png"),
                        Width = MinWidth,
                        Height = MinHeight - 40,
                    };
                    LabelNom = new TextBlock { Text = objet.nom };
                    ToolTip.SetTip(this, objet.FormatComparedDescription(_inventaireController._joueur));
                    Children.Add(imageObjet);
                    Children.Add(LabelNom);

                    PointerPressed += (s, e) =>
                    {
                        var point = e.GetCurrentPoint(this);
                        if (point.Properties.IsLeftButtonPressed)
                        {
                            var currentTime = DateTime.Now;
                            if ((currentTime - _lastClickTime).TotalMilliseconds < DoubleClickTimeMs)
                            {
                                if (Type.IsEquipable())
                                    _inventaireController.EquiperObjet(this);
                                else
                                    _inventaireController.UtiliserObjet(this);
                            }
                            else
                            {
                                SetEmphasized(!Emphasized);
                            }
                            _lastClickTime = currentTime;
                        }
                        else if (point.Properties.IsRightButtonPressed)
                        {
                            _inventaireController.JeterObjet(this);
                        }
                    };

                    PointerMoved += (s, e) =>
                    {
                        if (!Emphasized && _inventaireController._emphasizedSlot == null && Parent is Border parentBorder)
                        {
                            Dispatcher.UIThread.Post(() =>
                            {
                                parentBorder.BorderBrush = Brushes.LightGreen;
                                parentBorder.BorderThickness = new Thickness(2);
                                LabelNom.TextWrapping = TextWrapping.Wrap;
                            });
                        }
                    };

                    PointerExited += (s, e) =>
                    {
                        if (!Emphasized && _inventaireController._emphasizedSlot == null && Parent is Border parentBorder)
                        {
                            Dispatcher.UIThread.Post(() =>
                            {
                                parentBorder.BorderBrush = null;
                                parentBorder.BorderThickness = new Thickness(0);
                                LabelNom.TextWrapping = TextWrapping.NoWrap;
                            });
                        }
                    };
                }
                else
                {
                    Children.Add(new TextBlock { Text = "(Vide)" });
                }
            }

            public void SetEmphasized(bool emphase)
            {
                Emphasized = emphase;
                _inventaireController._emphasizedSlot = emphase ? this : null;
                Dispatcher.UIThread.Post(() =>
                {
                    if (Parent is Border parentBorder)
                    {
                        if (emphase)
                        {
                            parentBorder.BorderBrush = Brushes.LightGreen;
                            parentBorder.BorderThickness = new Thickness(4);
                            LabelNom.TextWrapping = TextWrapping.Wrap;
                        }
                        else
                        {
                            parentBorder.BorderBrush = null;
                            parentBorder.BorderThickness = new Thickness(0);
                            LabelNom.TextWrapping = TextWrapping.NoWrap;
                        }
                    }
                });
            }
        }
    }
}