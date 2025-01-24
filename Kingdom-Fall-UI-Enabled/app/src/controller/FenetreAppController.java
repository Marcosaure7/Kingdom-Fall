package controller;

import application.GameLogic;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import objets.Objet;
import objets.Potion;
import objets.Type_Objet;
import personnages.Ennemi;
import personnages.Entite;
import personnages.Joueur;

import java.io.IOException;
import java.util.ArrayList;

import static javafx.application.Application.STYLESHEET_CASPIAN;

public class FenetreAppController {

    private GameLogic gameLogic;
    private InventaireController inventaireController;
    private OptionsController optionsController;

    @FXML
    private ProgressBar barreVie;

    @FXML
    private ProgressBar barreVieEnnemie;

    @FXML
    private ProgressBar barreXP;

    @FXML
    private Button boutonAttaquer;

    @FXML
    private Button boutonInventaire;

    @FXML
    private Button boutonJeter;

    @FXML
    private Button boutonRamasser;

    @FXML
    private Button boutonSoinRapide;

    @FXML
    private Label labelAttaqueEnnemie;

    @FXML
    private Label labelDonjon;

    @FXML
    private Label labelGainXP;

    @FXML
    private Label labelEnnemiLache;

    @FXML
    private Label labelItemDrop;

    @FXML
    private Label labelNiveauJoueur;

    @FXML
    private Label labelNomEnnemi;

    @FXML
    private Label labelNomJoueur;

    @FXML
    private Label labelVie;

    @FXML
    private Label labelVieEnnemi;

    @FXML
    private Label labelXPJoueur;

    @FXML
    private MenuItem menuOptions;

    @FXML
    private TextFlow textFlowMessages;

    @FXML
    private ImageView imageDrop;
    private final ContextMenu menuInfosDrop = new ContextMenu();

    @FXML
    private BorderPane root;

    @FXML
    private ImageView imageEnnemi;


    @FXML
    public void initialize() {
        imageEnnemi.setImage(null);
        imageDrop.setImage(null);


        labelGainXP.setText("");
        labelEnnemiLache.setText("");
        labelItemDrop.setText("");
        labelDonjon.setText("0");
        labelAttaqueEnnemie.setText("");
        labelNiveauJoueur.setText("0");
        labelNomEnnemi.setText("");
        labelXPJoueur.setText("0.0%");
        labelVieEnnemi.setText("");

        barreVie.setProgress(1.0);
        barreXP.setProgress((0.0));
        barreVieEnnemie.setProgress(0.0);

        boutonInventaire.setDisable(true);
        boutonSoinRapide.setDisable(true);
        boutonAttaquer.setDisable(true);
        boutonRamasser.setVisible(false);
        boutonJeter.setVisible(false);

        initializeFenetreInventaire();
        initializeFenetreOptions();

        root.setOnKeyPressed(event -> {
            switch (event.getCode())
            {
                case A -> gameLogic.attaque();
                case I -> inventaireController.ouvrirInventaire(root.getScene().getWindow().getX(), root.getScene().getWindow().getY(), gameLogic.getJeuEnCours().getJoueur());
                case H -> soinRapide();
                case ESCAPE -> ouvrirOptions();
            }
        });

        menuOptions.setOnAction(event -> {
            ouvrirOptions();
        });

        boutonAttaquer.setOnAction(event -> gameLogic.attaque());
        boutonSoinRapide.setOnAction(event -> soinRapide());
        boutonRamasser.setOnAction(event -> {
            gameLogic.ramasser();
            Platform.runLater(() -> {
                labelEnnemiLache.setText("");
                imageDrop.setImage(null);
                labelItemDrop.setText("");
                boutonRamasser.setVisible(false);
                boutonJeter.setVisible(false);
            });
        });

        boutonJeter.setOnAction(event -> {
            Platform.runLater(() -> {
                labelEnnemiLache.setText("");
                imageDrop.setImage(null);
                labelItemDrop.setText("");
                boutonRamasser.setVisible(false);
                boutonJeter.setVisible(false);
            });
            gameLogic.relacherLatch();
        });

        boutonInventaire.setOnAction(event ->
                inventaireController.ouvrirInventaire(((Button) event.getSource()).getScene().getWindow().getX(),
                                                        ((Button) event.getSource()).getScene().getWindow().getY(),
                                                        gameLogic.getJeuEnCours().getJoueur())
        );

        imageDrop.setOnMouseMoved(event -> {
            if (imageDrop.getImage() != null)
                menuInfosDrop.show(imageDrop, event.getScreenX() + 10, event.getScreenY() - 120);
        });

        imageDrop.setOnMouseExited(event -> {
            menuInfosDrop.hide(); // Ferme le popup
        });

        menuInfosDrop.setConsumeAutoHidingEvents(true);

        Text messageBienvenue = new Text("Bienvenue à Kingdom Fall!\n\n");
        messageBienvenue.setFill(Color.WHITE);
        textFlowMessages.getChildren().add(messageBienvenue);
    }

    private void ouvrirOptions()
    {
        optionsController.getStageOptions().show();
        optionsController.getStageOptions().toFront();
    }

    private void soinRapide() {

        Joueur joueurCourant = gameLogic.getJeuEnCours().getJoueur();
        ArrayList<Objet> potions = joueurCourant.getInventaire().getListType(Type_Objet.POTIONS);

        if (!potions.isEmpty())
            joueurCourant.getInventaire().utiliser(potions.getFirst(), 0);
        else
            envoyerMessage("Vous n'avez pas de potions dans votre inventaire.");
    }

    private void initializeFenetreInventaire() {
        Stage stageInventaire = new Stage();

        FXMLLoader loaderInventaire = new FXMLLoader(getClass().getResource("/fenetreInventaire.fxml"));
        Parent root;
        try {
            // Charger le layout depuis le fichier FXML
            root = loaderInventaire.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root);

        Application.setUserAgentStylesheet(STYLESHEET_CASPIAN);

        stageInventaire.setTitle("Kingdom Fall - Inventaire");
        stageInventaire.setScene(scene);
        stageInventaire.initModality(Modality.APPLICATION_MODAL);

        stageInventaire.setWidth(960);
        stageInventaire.setHeight(540);

        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        inventaireController = loaderInventaire.getController();
        inventaireController.setStageInventaire(stageInventaire);
    }

    private void initializeFenetreOptions()
    {
        Stage stageOptions = new Stage();

        FXMLLoader loaderOptions = new FXMLLoader(getClass().getResource("/fenetreOptions.fxml"));
        Parent root;
        try {
            // Charger le layout depuis le fichier FXML
            root = loaderOptions.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root);

        Application.setUserAgentStylesheet(STYLESHEET_CASPIAN);

        stageOptions.setTitle("Kingdom Fall - Options");
        stageOptions.setScene(scene);
        stageOptions.initModality(Modality.APPLICATION_MODAL);

        stageOptions.setWidth(960);
        stageOptions.setHeight(540);

        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        optionsController = loaderOptions.getController();
        optionsController.setStageOptions(stageOptions);
    }

    public void setThread (GameLogic thread) {
        gameLogic = thread;
    }

    public void envoyerMessage(String message) {
        Platform.runLater(() -> {
            if (textFlowMessages.getChildren().size() >= 4) textFlowMessages.getChildren().removeFirst(); // Permet de régler le nombre de messages affichés à 4.
            Text messageEnTexte = new Text(message + "\n\n");
            messageEnTexte.setFill(Color.WHITE);
            textFlowMessages.getChildren().add(messageEnTexte);
        });
    }

    public void afficherEnnemi(Ennemi ennemiAffiche) {
        Platform.runLater(() -> {
            imageEnnemi.setImage(new Image("/images/" + ennemiAffiche.getNom().toLowerCase() + ".png"));

            labelNomEnnemi.setText(ennemiAffiche.getNom());
            labelVieEnnemi.setText(ennemiAffiche.getVieRestante() + "/" + ennemiAffiche.getPtsVie());
            labelAttaqueEnnemie.setText(ennemiAffiche.getAttBase() + "");
        });

        changerProgresBarreAnime(barreVieEnnemie, (double) ennemiAffiche.getVieRestante() / ennemiAffiche.getPtsVie());
    }

    public void afficherAttaquer(Entite entiteAttaquee) {
        String stringVieRestante = entiteAttaquee.getVieRestante() + "/" + entiteAttaquee.getPtsVie();
        double ratioVieRestante = (double) entiteAttaquee.getVieRestante() / entiteAttaquee.getPtsVie();

        switch (entiteAttaquee)
        {
            case Ennemi e ->
            {
                Platform.runLater(() -> labelVieEnnemi.setText(stringVieRestante));
                changerProgresBarreAnime(barreVieEnnemie, ratioVieRestante);
            }
            case Joueur j ->
            {
                Platform.runLater(() -> labelVie.setText(stringVieRestante));
                changerProgresBarreAnime(barreVie, ratioVieRestante);
            }
            default -> throw new IllegalStateException("Unexpected value: " + entiteAttaquee); // Pas sensé aller là
        }
    }

    public void changerProgresBarreAnime(ProgressBar barre, double nouvelleValeur) {
        double ancienneValeur = barre.getProgress();

        if (ancienneValeur != nouvelleValeur) {
            double progresParIteration = nouvelleValeur > ancienneValeur ? 0.02 : -0.02;
            int nombreDAnimations = (int) ((nouvelleValeur - ancienneValeur) / progresParIteration);
            double progresActuel = ancienneValeur;

            for (int i = 0; i < nombreDAnimations; i++)
            {
                progresActuel += progresParIteration;
                double progres = progresActuel;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> barre.setProgress(progres));
            }

            Platform.runLater(() -> barre.setProgress(nouvelleValeur));
        }
    }

    public void activerNode (String node) {
        switch (node) {
            case "attaquer" -> boutonAttaquer.setDisable(false);
            case "inventaire" -> boutonInventaire.setDisable(false);
            case "soin rapide" -> boutonSoinRapide.setDisable(false);
        }
    }

    public void gainXp(Joueur joueur, int ancienNiveau, int xpGagne) {
        Platform.runLater(() -> labelGainXP.setText("+" + xpGagne + " XP"));

        int nbNiveauxGagnes = joueur.getNiveau() - ancienNiveau;

        for (int i = 0; i < nbNiveauxGagnes; i++)
        {
            changerProgresBarreAnime(barreXP, 1.0);
            int niveauCourant = ancienNiveau + i + 1;
            Platform.runLater(() -> {
                barreXP.setProgress(0.0);
                labelNiveauJoueur.setText(niveauCourant + "");
            });
        }

        changerProgresBarreAnime(barreXP, (double) joueur.getXP().getValeur() / joueur.getXpCap());
        if (nbNiveauxGagnes > 0) changerProgresBarreAnime(barreVie, 1.0);

        Platform.runLater(() -> {
            labelGainXP.setText("");
            labelXPJoueur.setText(String.format("%.1f%%", (double) joueur.getXP().getValeur() / joueur.getXpCap() * 100));
            labelVie.setText(joueur.getVieRestante() + "/" + joueur.getPtsVie());
        });

    }

    public void showDrops(Ennemi source, Objet objetChoisi) {
        Platform.runLater(() -> {
            boutonAttaquer.setDisable(true);

            String url = "/images/" + objetChoisi.getNom().toLowerCase() + ".png";

            System.out.println(url);
            imageDrop.setImage(new Image(url));
            if (!menuInfosDrop.getItems().isEmpty()) menuInfosDrop.getItems().removeFirst();
            menuInfosDrop.getItems().add(new MenuItem(objetChoisi.getDescription()));

            labelEnnemiLache.setText(source.getNom() + " a lâché :");
            labelItemDrop.setText(objetChoisi.getNom());
            boutonRamasser.setVisible(true);
            boutonJeter.setVisible(true);
        });
    }


    public void afficherDonjon(int donjon) {
        Platform.runLater(() -> labelDonjon.setText(donjon + ""));
    }

    public void afficherSoin(Entite entite) {
        if (entite instanceof Joueur joueur)
        {
            Platform.runLater(() -> labelVie.setText(joueur.getVieRestante() + "/" + joueur.getPtsVie()));
            changerProgresBarreAnime(barreVie, (double) joueur.getVieRestante() / joueur.getPtsVie());
            boutonSoinRapide.setDisable(joueur.getVieRestante() == joueur.getPtsVie() && joueur.getInventaire().getListType(Type_Objet.POTIONS).isEmpty());
        }

        else
        {
            Platform.runLater(() -> labelVieEnnemi.setText(entite.getVieRestante() + "/" + entite.getPtsVie()));
            changerProgresBarreAnime(barreVieEnnemie, (double) entite.getVieRestante() / entite.getPtsVie());
        }
    }
}
