package application;

import controller.FenetreAppController;
import controller.OptionsController;
import javafx.stage.FileChooser;

import java.io.*;

public class Sauvegarde
{
    public static void sauvegarderJeu(Jeu jeu, String nomJoueur)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger jeu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jeux", "*.jeu"));
        fileChooser.setInitialDirectory(new File("sauvegardes"));
        fileChooser.setInitialFileName(nomJoueur + ".jeu");
        File file = fileChooser.showSaveDialog(null);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(jeu);
            System.out.println("Jeu sauvegardé avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la sauvegarde.");
        }
    }

    public static Jeu chargerJeu()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger jeu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jeux", "*.jeu"));
        fileChooser.setInitialDirectory(new File("sauvegardes"));
        File file = fileChooser.showOpenDialog(FenetreAppController.getSingleton().stageApp);

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Jeu jeu = (Jeu) in.readObject();
            OptionsController.renommerJoueur(jeu.getJoueur().getNom());
            return jeu;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement.");
            return null;
        }
    }
}
