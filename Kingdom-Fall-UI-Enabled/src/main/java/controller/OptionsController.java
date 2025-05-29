package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OptionsController {

    public static boolean CONFIRMER_JETER = true;
    public static String PLAYER_NAME = "Joueur";
    public static boolean DEV_MODE = false;

    private Stage stageOptions;
    private static FenetreAppController fenetreAppController;

    @FXML
    private CheckBox OPT_CONFIRMER_JETER;

    @FXML
    private TextField PLAYER_NAME_TextField;


    @FXML
    public void initialize() {
        OPT_CONFIRMER_JETER.setSelected(CONFIRMER_JETER);
        OPT_CONFIRMER_JETER.selectedProperty().addListener((observable, oldValue, newValue) -> CONFIRMER_JETER = newValue);
        PLAYER_NAME_TextField.setOnKeyTyped(keyEvent -> renommerJoueur(PLAYER_NAME_TextField.getText()));
    }

    public static void renommerJoueur(String nom) {
        PLAYER_NAME = nom;
        fenetreAppController.updateUsername(PLAYER_NAME);
    }

    public void setStageOptions(Stage stageOptions)
    {
        this.stageOptions = stageOptions;
    }

    public Stage getStageOptions()
    {
        return stageOptions;
    }

    public void setFenetreAppController(FenetreAppController fenetreAppController)
    {
        this.fenetreAppController = fenetreAppController;
    }
}
