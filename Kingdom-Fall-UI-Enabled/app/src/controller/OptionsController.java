package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class OptionsController {

    public static boolean CONFIRMER_JETER = true;

    private Stage stageOptions;

    @FXML
    private CheckBox OPT_CONFIRMER_JETER;

    @FXML
    public void initialize() {
        OPT_CONFIRMER_JETER.setSelected(CONFIRMER_JETER);
        OPT_CONFIRMER_JETER.selectedProperty().addListener((observable, oldValue, newValue) -> CONFIRMER_JETER = newValue);
    }

    public void setStageOptions(Stage stageOptions)
    {
        this.stageOptions = stageOptions;
    }

    public Stage getStageOptions()
    {
        return stageOptions;
    }
}
