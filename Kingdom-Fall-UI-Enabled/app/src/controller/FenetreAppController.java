package controller;

import application.App;
import application.GameLogic;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import personnages.Ennemi;

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
    private Button boutonSoinRapide;

    @FXML
    private Label labelAttaqueEnnemie;

    @FXML
    private Label labelDonjon;

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
    private ImageView imageEnnemi;

    @FXML
    public void initialize() {
        imageEnnemi.setImage(null);

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

        boutonAttaquer.setOnAction(event -> gameLogic.attaque());

        Text messageBienvenue = new Text("Bienvenue à Kingdom Fall!");
        messageBienvenue.setFill(Color.WHITE);
        textFlowMessages.getChildren().add(messageBienvenue);
    }

    public void setThread (GameLogic thread) {
        gameLogic = thread;
    }

    public void envoyerMessage(String message) {
        Platform.runLater(() -> {
            Text messageEnTexte = new Text("\n\n" + message);
            messageEnTexte.setFill(Color.WHITE);
            textFlowMessages.getChildren().add(messageEnTexte);
        });
    }

    public void afficherEnnemi(Ennemi ennemiAffiche) {
        Platform.runLater(() -> {
            // TODO Faire afficher l'image spécifique de l'ennemi
            imageEnnemi.setImage(new Image("/images/squelette-0.png"));

            labelNomEnnemi.setText(ennemiAffiche.getNom());
            labelVieEnnemi.setText(ennemiAffiche.getVieRestante() + "/" + ennemiAffiche.getPtsVie());
            labelAttaqueEnnemie.setText(ennemiAffiche.getAttBase() + "");
        });

        //barreVieEnnemie.setProgress(1.0);
        changerProgresBarreAnime(barreVieEnnemie, (double) ennemiAffiche.getVieRestante() / ennemiAffiche.getPtsVie());
    }

    public void changerProgresBarreAnime(ProgressBar barre, double nouvelleValeur) {
        double ancienneValeur = barre.getProgress();

        if (ancienneValeur != nouvelleValeur) {
            double progresParIteration = nouvelleValeur > ancienneValeur ? 0.08 : -0.08;
            int nombreDAnimations = (int) ((nouvelleValeur - ancienneValeur) / progresParIteration);
            double progresActuel = ancienneValeur;

            for (int i = 0; i < nombreDAnimations; i++)
            {
                progresActuel += progresParIteration;
                double progres = progresActuel;
                try {
                    Thread.sleep(50);
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
}
