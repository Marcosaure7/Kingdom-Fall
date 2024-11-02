package controller;

import application.GameLogic;
import application.Jeu;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.image.ImageView;
import objets.Objet;
import personnages.Ennemi;
import personnages.Joueur;

public class FenetreAppController {

    private GameLogic gameLogic;

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
    private TextFlow textFlowMessages;

    @FXML
    private ImageView imageDrop;
    private final ContextMenu menuInfosDrop = new ContextMenu();

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



        boutonAttaquer.setOnAction(event -> gameLogic.attaque());
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
        boutonJeter.setOnAction(event ->
            Platform.runLater(() -> {
                imageDrop.setImage(null);
                boutonRamasser.setVisible(false);
                boutonJeter.setVisible(false);
                gameLogic.relacherLatch();
            }));

        imageDrop.setOnMouseEntered(event -> {
            if (imageDrop.getImage() != null)
                menuInfosDrop.show(imageDrop, event.getScreenX(), event.getScreenY());
        });
        imageDrop.setOnMouseExited(event -> menuInfosDrop.hide());


        Text messageBienvenue = new Text("Bienvenue à Kingdom Fall!\n\n");
        messageBienvenue.setFill(Color.WHITE);
        textFlowMessages.getChildren().add(messageBienvenue);
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
            // TODO Faire afficher l'image spécifique de l'ennemi
            imageEnnemi.setImage(new Image("/images/" + ennemiAffiche.getNom().toLowerCase() + ".png"));

            labelNomEnnemi.setText(ennemiAffiche.getNom());
            labelVieEnnemi.setText(ennemiAffiche.getVieRestante() + "/" + ennemiAffiche.getPtsVie());
            labelAttaqueEnnemie.setText(ennemiAffiche.getAttBase() + "");
        });

        changerProgresBarreAnime(barreVieEnnemie, (double) ennemiAffiche.getVieRestante() / ennemiAffiche.getPtsVie());
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
        Platform.runLater(() -> {
            labelGainXP.setText("");
            labelXPJoueur.setText(((double) joueur.getXP().getValeur() / joueur.getXpCap()) + "%");
            labelVie.setText(joueur.getPtsVie() + "/" + joueur.getPtsVie());
        });

    }

    public void showDrops(Ennemi source, Objet objetChoisi) {
        Platform.runLater(() -> {
            boutonAttaquer.setDisable(true);

            imageDrop.setImage(new Image("/images/" + objetChoisi.getNom().toLowerCase() + ".png"));
            if (!menuInfosDrop.getItems().isEmpty()) menuInfosDrop.getItems().removeFirst();
            menuInfosDrop.getItems().add(new MenuItem(objetChoisi.getDescription()));

            labelEnnemiLache.setText(source.getNom() + " a lâché :");
            labelItemDrop.setText(objetChoisi.getNom());
            boutonRamasser.setVisible(true);
            boutonJeter.setVisible(true);
        });
    }
}
