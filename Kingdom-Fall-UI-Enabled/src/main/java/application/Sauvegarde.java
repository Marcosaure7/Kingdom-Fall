package application;

import controller.FenetreAppController;
import controller.OptionsController;
import javafx.stage.FileChooser;

import java.io.*;

public class Sauvegarde
{
    static final String SAVE_DIRECTORY_PATH = System.getProperty("user.home") + "/games/Kingdom-Fall/sauvegardes";

    public static void sauvegarderJeu(Jeu jeu, String nomJoueur)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger jeu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jeux", "*.jeu"));
        File directory = new File(SAVE_DIRECTORY_PATH);
        System.out.println(directory.getAbsolutePath());
        if(!directory.exists())
            directory.mkdirs();
        fileChooser.setInitialDirectory(directory);
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
        File directory = new File(SAVE_DIRECTORY_PATH);
        System.out.println(directory.getAbsolutePath());
        if(!directory.exists())
            directory.mkdirs();
        fileChooser.setInitialDirectory(directory);
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

    public static boolean hasSaves() {
        // Définir le répertoire des sauvegardes
        File savesDir = new File(SAVE_DIRECTORY_PATH);

        // Vérifier si le répertoire existe
        if (!savesDir.exists() || !savesDir.isDirectory()) {
            return false; // Aucun répertoire, donc aucune sauvegarde
        }

        // Lister les fichiers dans le répertoire
        File[] saveFiles = savesDir.listFiles((dir, name) -> name.endsWith(".jeu"));

        // Vérifier s'il y a des fichiers de sauvegarde
        return saveFiles != null && saveFiles.length > 0;
    }
}
