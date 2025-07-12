using System;
using System.IO;
using Avalonia.Controls; // Pour OpenFileDialog et SaveFileDialog
namespace App;

public class Sauvegarde
{
    private static readonly string SAVE_DIRECTORY_PATH = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.UserProfile), "games/Kingdom-Fall/sauvegardes");

    public static async Task SauvegarderJeu(Jeu jeu, string nomJoueur)
    {
        var window = controllers.FenetreAppController.GetSingleton();
        var options = new Avalonia.Platform.Storage.FilePickerSaveOptions
        {
            Title = "Sauvegarder jeu",
            SuggestedFileName = nomJoueur + ".jeu",
            FileTypeChoices = new[]
            {
                new Avalonia.Platform.Storage.FilePickerFileType("Jeux") { Patterns = new[] { "*.jeu" } }
            }
        };

        DirectoryInfo directory = new DirectoryInfo(SAVE_DIRECTORY_PATH);
        Console.WriteLine(directory.FullName);
        if (!directory.Exists)
        {
            // Utilisation de StorageProvider pour sauvegarder le fichier
            var result = await window.StorageProvider.SaveFilePickerAsync(options);

            if (result != null)
            {
                try
                {
                    using (var fs = await result.OpenWriteAsync())
                    {
                        var json = System.Text.Json.JsonSerializer.Serialize(jeu);
                        using (var writer = new StreamWriter(fs))
                        {
                            writer.Write(json);
                        }
                        Console.WriteLine("Jeu sauvegardé avec succès.");
                    }
                }
                catch (IOException ex)
                {
                    Console.WriteLine("Erreur lors de la sauvegarde.");
                    Console.WriteLine(ex.ToString());
                }
            }
        }
    }

    public static async Task<Jeu> ChargerJeu()
    {
        var fileChooser = new OpenFileDialog
        {
            Title = "Charger jeu",
            Filters = { new FileDialogFilter { Name = "Jeux", Extensions = { "jeu" } } },
            Directory = SAVE_DIRECTORY_PATH,
            AllowMultiple = false
        };

        DirectoryInfo directory = new DirectoryInfo(SAVE_DIRECTORY_PATH);
        Console.WriteLine(directory.FullName);
        if (!directory.Exists)
        {
            directory.Create();
        }

        // Note : Vous devez passer une référence à la fenêtre principale (Avalonia Window)
        var files = await fileChooser.ShowAsync(controllers.FenetreAppController.GetSingleton());

        if (files != null && files.Length > 0)
        {
            try
            {
                using (FileStream fs = new FileStream(files[0], FileMode.Open))
                using (var reader = new StreamReader(fs))
                {
                    var json = reader.ReadToEnd();
                    Jeu jeu = System.Text.Json.JsonSerializer.Deserialize<Jeu>(json);
                    controllers.OptionsController.RenommerJoueur(jeu.joueur.nom);
                    return jeu;
                }
            }
            catch (Exception ex) when (ex is IOException || ex is System.Runtime.Serialization.SerializationException)
            {
                Console.WriteLine("Erreur lors du chargement.");
                Console.WriteLine(ex.ToString());
                return null;
            }
        }

        return null;
    }

    public static bool HasSaves()
    {
        DirectoryInfo savesDir = new DirectoryInfo(SAVE_DIRECTORY_PATH);

        if (!savesDir.Exists)
        {
            return false;
        }

        FileInfo[] saveFiles = savesDir.GetFiles("*.jeu");
        return saveFiles.Length > 0;
    }
}