package application;

import controller.FenetreAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static final String LIGNE = "***********************************************************************************************************";
    public static final String CMD_INCONNUE = "La commande n'a pas ete reconnue !";

    private GameLogic gameLogic;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loaderApp = new FXMLLoader(getClass().getResource("/fenetreApp.fxml"));
        Parent root = loaderApp.load(); // Charger le layout depuis le fichier FXML

        Scene scene = new Scene(root);

        setUserAgentStylesheet(STYLESHEET_CASPIAN);

        primaryStage.setTitle("Kingdom Fall");
        primaryStage.setScene(scene);

        // Charger le fichier CSS pour la scène
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();

        gameLogic = new GameLogic(loaderApp.getController());


        gameLogic.start();
    }
}

