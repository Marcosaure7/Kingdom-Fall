package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objets.Objet;
import objets.Type_Objet;
import personnages.Joueur;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;

public class InventaireController {

    private Stage stageInventaire;
    private Joueur joueur;

    @FXML
    private ImageView imageObjetEquipe;

    @FXML
    private Label labelArmes;

    @FXML
    private Label labelArmesOuvert;

    @FXML
    private Label labelArmures;

    @FXML
    private Label labelArmuresOuvert;

    @FXML
    private Label labelDescriptionEquipee;

    @FXML
    private Label labelDivers;

    @FXML
    private Label labelDiversOuvert;

    @FXML
    private Label labelPotions;

    @FXML
    private Label labelPotionsOuvert;

    @FXML
    private FlowPane paneObjets;

    @FXML
    private VBox vboxEquipe;


    @FXML
    public void initialize() {
        labelArmesOuvert.setText("");
        labelArmuresOuvert.setText("");
        labelPotionsOuvert.setText("");
        labelDiversOuvert.setText("");

        imageObjetEquipe.setImage(null);
        labelDescriptionEquipee.setText("");
        vboxEquipe.setVisible(false);

        labelArmes.setOnMouseClicked(event -> {
            labelArmesOuvert.setText(" >");
            labelArmuresOuvert.setText("");
            labelPotionsOuvert.setText("");
            labelDiversOuvert.setText("");

            ouvrirEquipement(Type_Objet.ARMES);
        });

        labelArmures.setOnMouseClicked(event -> {
            labelArmesOuvert.setText("");
            labelArmuresOuvert.setText(" >");
            labelPotionsOuvert.setText("");
            labelDiversOuvert.setText("");

            ouvrirEquipement(Type_Objet.ARMURES);
        });

        labelPotions.setOnMouseClicked(event -> {
            labelArmesOuvert.setText("");
            labelArmuresOuvert.setText("");
            labelPotionsOuvert.setText(" >");
            labelDiversOuvert.setText("");

            ouvrirEquipement(Type_Objet.POTIONS);
        });

        labelDivers.setOnMouseClicked(event -> {
            labelArmesOuvert.setText("");
            labelArmuresOuvert.setText("");
            labelPotionsOuvert.setText("");
            labelDiversOuvert.setText(" >");

            ouvrirEquipement(Type_Objet.DIVERS);
        });
    }


    public void setStageInventaire(Stage stageInventaire) {
        this.stageInventaire = stageInventaire;
    }

    public void ouvrirInventaire(double coordX, double coordY, @Nullable Joueur joueur) {
        labelArmesOuvert.setText("");
        labelArmuresOuvert.setText("");
        labelPotionsOuvert.setText("");
        labelDiversOuvert.setText("");
        imageObjetEquipe.setImage(null);
        labelDescriptionEquipee.setText("");
        vboxEquipe.setVisible(false);
        paneObjets.getChildren().clear();


        this.joueur = joueur;
        stageInventaire.setX(coordX);
        stageInventaire.setY(coordY);
        stageInventaire.show();
    }

    /**
     * @param typeObjet ARMES ou ARMURES
     */
    private void ouvrirEquipement(Type_Objet typeObjet) {
        Platform.runLater(() -> {
            paneObjets.getChildren().clear();
            imageObjetEquipe.setImage(null);
            labelDescriptionEquipee.setText("");
            vboxEquipe.setVisible(true);
        });

        if (typeObjet == Type_Objet.ARMES || typeObjet == Type_Objet.ARMURES)
        {
            Objet equipee = joueur.getEquip(typeObjet);

            Platform.runLater(() -> {
                if (equipee != null)
                {
                    imageObjetEquipe.setImage(new Image("/images/" + equipee.getNom().toLowerCase() + ".png"));
                    labelDescriptionEquipee.setText(equipee.getDescription());
                }
                else
                {
                    imageObjetEquipe.setImage(null);
                    labelDescriptionEquipee.setText("Aucun objet équipé");
                }
            });
        }
        afficherObjets(typeObjet);
    }

    private void afficherObjets(Type_Objet typeObjet)
    {
        Platform.runLater(() -> paneObjets.getChildren().clear()); // clear le pane si jamais, c'est une actualisation de l'inventaire
        ArrayList<Objet> listeObjetsInv = joueur.getInventaire().getListType(typeObjet);

        for (int i = 0; i < typeObjet.getEspaceInventaire(); i++) {
            ObjetSlot objetSlot;

            if (i > listeObjetsInv.size() - 1 || listeObjetsInv.get(i) == null)
                objetSlot = new ObjetSlot(typeObjet, null, i);
            else
                objetSlot = new ObjetSlot(typeObjet, listeObjetsInv.get(i), i);

            Platform.runLater(() -> paneObjets.getChildren().add(objetSlot));
        }
    }

    private void equiperObjet(ObjetSlot objetSlot) {
        joueur.getInventaire().equiper(objetSlot.objetStock, objetSlot.index);
        switch (objetSlot.type) {
            case Type_Objet.ARMES -> ouvrirEquipement(Type_Objet.ARMES);
            case Type_Objet.ARMURES -> ouvrirEquipement(Type_Objet.ARMURES);
        }
    }

    private class ObjetSlot extends VBox
    {
        Type_Objet type;
        Objet objetStock;
        int index;
        ContextMenu menuInfos;

        public ObjetSlot(Type_Objet type, Objet objet, int index)
        {
            this.index = index;
            this.type = type;

            setMinSize(100, 120);
            setPrefSize(getMinWidth(), getMinHeight());
            //setMargin(this, new Insets(100, 0, 0, 0));
            //setPadding(new Insets(20, 0, 0, 0));
            setAlignment(Pos.BOTTOM_CENTER);

            if (objet != null)
            {
                objetStock = objet;
                ImageView imageObjet = new ImageView(new Image("/images/" + objetStock.getNom() + ".png"));
                imageObjet.setFitHeight(75);
                imageObjet.setFitWidth(50);

                this.menuInfos = new ContextMenu(new MenuItem(objet.getDescription()));

                getChildren().add(imageObjet);
                getChildren().add(new Label(objetStock.getNom()));

                setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
                        if (type == Type_Objet.ARMES || type == Type_Objet.ARMURES) {
                            equiperObjet( this);
                        }
                        else {
                            utiliserObjet(this);
                        }
                    }
                    else if (event.getButton() == MouseButton.SECONDARY) {
                        Optional<ButtonType> resultat = Optional.of(ButtonType.OK);
                        if (OptionsController.CONFIRMER_JETER)
                        {
                            Alert alertJeter = new Alert(Alert.AlertType.CONFIRMATION);
                            alertJeter.setTitle("Jeter l'objet");
                            alertJeter.setHeaderText("Voulez-vous jeter '" + objet.getNom() + "' ?");
                            alertJeter.setContentText("Cet objet sera perdu définitivement.");
                            resultat = alertJeter.showAndWait();
                        }

                        if (resultat.isPresent() && resultat.get() == ButtonType.OK)
                        {
                            jeterObjet(this);
                            afficherObjets(type);
                        }
                    }
                });

                setOnMouseMoved(event -> {
                    setStyle("-fx-border-style: solid; -fx-border-color: lightgreen; -fx-border-width: 2px;");
                    menuInfos.show(this, event.getScreenX() + 10, event.getScreenY() - 120);
                });

                setOnMouseExited(event -> {
                    setStyle("");
                    menuInfos.hide();
                });
            }

            else getChildren().add(new Label("(Vide)"));
        }
    }

    private void jeterObjet(ObjetSlot objetSlot)
    {
        joueur.getInventaire().jeter(objetSlot.type, objetSlot.index);
    }

    private void utiliserObjet(ObjetSlot objetSlot) {
        joueur.getInventaire().utiliser(objetSlot.objetStock, objetSlot.index);
        switch (objetSlot.type) {
            case Type_Objet.POTIONS -> ouvrirEquipement(Type_Objet.POTIONS);
            case Type_Objet.DIVERS -> ouvrirEquipement(Type_Objet.DIVERS);
        }
    }
}
