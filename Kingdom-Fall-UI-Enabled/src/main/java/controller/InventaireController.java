package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objets.Objet;
import objets.Type_Objet;
import personnages.Joueur;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class InventaireController {

    private Stage stageInventaire;
    private Joueur joueur;
    private ObjetSlot emphasizedSlot = null;

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
    private BorderPane root;

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

        // TODO : not working
       root.setOnKeyPressed(event ->
       {
           System.out.println("key typed: " + event.getCharacter());
           if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT)
           {
               int leftOrRight = event.getCode() == KeyCode.LEFT ? -1 : 1; // left = -1, right = 1
               System.out.println(leftOrRight);
               int emphasizedSlotIndex = emphasizedSlot != null ? emphasizedSlot.index : 0;
               int nextEmphasizedSlotIndex = getNextEmphasizedSlotIndex(emphasizedSlotIndex, leftOrRight);
               ObjetSlot nextEmphasizedSlot = ((ObjetSlot) paneObjets.getChildren().get(nextEmphasizedSlotIndex));

               if (nextEmphasizedSlot != null && nextEmphasizedSlot.objetStock != null)
                   nextEmphasizedSlot.setEmphasized(true);
           }
       });
    }

    private int getNextEmphasizedSlotIndex(int emphasizedSlotIndex, int leftOrRight) {
        int nextEmphasizedSlot = emphasizedSlotIndex + leftOrRight;

        // on veut aller à droite mais on est au dernier élément ;
        // retour au début
        if (nextEmphasizedSlot >= paneObjets.getChildren().size()
                || ((ObjetSlot) paneObjets.getChildren().get(nextEmphasizedSlot)).objetStock != null)
        {
            nextEmphasizedSlot = 0;
        }
        // On veut aller à gauche mais on est au premier élément ;
        // envoi à la fin
        else if (nextEmphasizedSlot < 0)
        {
            nextEmphasizedSlot = paneObjets.getChildren().size() - 1;
        }
        return nextEmphasizedSlot;
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

    private void ouvrirEquipement(Type_Objet typeObjet) {
        Platform.runLater(() -> {
            paneObjets.getChildren().clear();
            imageObjetEquipe.setImage(null);
            labelDescriptionEquipee.setText("");
            vboxEquipe.setVisible(true);
        });

        if (typeObjet.isEquipable())
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
        ArrayList<Objet> objetsSlotInventaire = joueur.getInventaire().getListType(typeObjet);

        for (int i = 0; i < typeObjet.getEspaceInventaire(); i++) {
            ObjetSlot objetSlot;

            if (i > objetsSlotInventaire.size() - 1 || objetsSlotInventaire.get(i) == null)
                objetSlot = new ObjetSlot(typeObjet, null, i);
            else
                objetSlot = new ObjetSlot(typeObjet, objetsSlotInventaire.get(i), i);

            Platform.runLater(() -> paneObjets.getChildren().add(objetSlot));
        }
    }

    private void equiperObjet(ObjetSlot objetSlot) {
        objetSlot.emphasized = false;
        emphasizedSlot = null;
        joueur.getInventaire().equiper(objetSlot.objetStock, objetSlot.index);
        ouvrirEquipement(objetSlot.type);
    }

    private class ObjetSlot extends VBox
    {
        Type_Objet type;
        Objet objetStock;
        int index;
        ContextMenu menuInfos;
        Label labelNom;
        boolean emphasized;

        public ObjetSlot(Type_Objet type, Objet objet, int index)
        {
            this.index = index;
            this.type = type;
            emphasized = false;

            setMinSize(100, 120);
            setPrefSize(getMinWidth(), getMinHeight());
            setAlignment(Pos.BOTTOM_CENTER);

            if (objet != null)
            {
                objetStock = objet;
                ImageView imageObjet = new ImageView(new Image("/images/" + objetStock.getNom().toLowerCase() + ".png"));
                imageObjet.setFitHeight(75);
                imageObjet.setFitWidth(50);

                this.menuInfos = new ContextMenu(new CustomMenuItem(objet.formatComparedDescription(joueur)));

                Platform.runLater(() -> getChildren().add(imageObjet));
                labelNom = new Label(objetStock.getNom());
                // labelNom.setWrapText(true); // Retour à la ligne auto
                Platform.runLater(() -> getChildren().add(labelNom));

                setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY)
                    {
                        if (event.getClickCount() >= 2)
                        {
                            if (type.isEquipable())
                                equiperObjet( this);
                            else
                                utiliserObjet(this);
                        }
                        else
                            setEmphasized(!emphasized);

                    }
                    else if (event.getButton() == MouseButton.SECONDARY)
                    {
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
                    if (!emphasized && emphasizedSlot == null)
                    {
                        Platform.runLater(() -> {
                            setStyle("-fx-border-style: solid; -fx-border-color: lightgreen; -fx-border-width: 2px;");
                            labelNom.setWrapText(true);
                            menuInfos.show(this, event.getScreenX() + 10, event.getScreenY() - menuInfos.getHeight());
                        });
                    }
                });

                setOnMouseExited(event -> {
                    if (!emphasized && emphasizedSlot == null)
                    {
                        Platform.runLater(() -> {
                            setStyle("");
                            labelNom.setWrapText(false);
                            menuInfos.hide();
                        });
                    }
                });
            }
            else getChildren().add(new Label("(Vide)"));
        }

        protected void setEmphasized(boolean emphase)
        {
            if (emphase)
            {
                this.emphasized = true;
                emphasizedSlot = this;
                Platform.runLater(() -> {
                    setStyle("-fx-border-style: solid; -fx-border-color: lightgreen; -fx-border-width: 4px;");
                    menuInfos.show(this, menuInfos.getX(), menuInfos.getY());
                });
            }
            else
            {
                this.emphasized = false;
                emphasizedSlot = null;
                Platform.runLater(() -> setStyle(""));
            }

            Platform.runLater(() -> labelNom.setWrapText(emphase));
        }
    }

    private void jeterObjet(ObjetSlot objetSlot)
    {
        joueur.getInventaire().jeter(objetSlot.type, objetSlot.index);
        if (objetSlot.type == Type_Objet.POTIONS)
            FenetreAppController.getSingleton().setNbPotions(joueur.getInventaire().getListType(Type_Objet.POTIONS).size());
    }

    private void utiliserObjet(ObjetSlot objetSlot) {
        joueur.getInventaire().utiliser(objetSlot.objetStock, objetSlot.index);
        switch (objetSlot.type) {
            case Type_Objet.POTIONS -> ouvrirEquipement(Type_Objet.POTIONS);
            case Type_Objet.DIVERS -> ouvrirEquipement(Type_Objet.DIVERS);
        }
    }
}
