package application;

import controller.FenetreAppController;
import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameLogic extends Thread {
    private volatile boolean running = true; // Contrôle l'exécution du thread

    public Jeu jeuEnCours;
    private ExecutorService executor;
    private CountDownLatch latch;

    public GameLogic(FenetreAppController controller)
    {
        super();
        controller.setThread(this);
        executor = Executors.newSingleThreadExecutor();
        latch = new CountDownLatch(1);
        jeuEnCours = new Jeu(this, controller);
    }

    @Override
    public void run() {
        jeuEnCours.lancerJeu();

        while (running) {
            // Logique du jeu ici
            updateGame(); // Met à jour l'état du jeu
            try {
                Thread.sleep(100); // Contrôle la vitesse de mise à jour
            } catch (InterruptedException e) {
                // Gérer l'interruption du thread
                Thread.currentThread().interrupt();
            }
        }
    }

    public Jeu getJeuEnCours()
    {
        return jeuEnCours;
    }

    public void stopGame() {
        running = false; // Arrête le jeu
        this.interrupt(); // Réveille le thread si en pause
    }

    private void updateGame() {
        // Code pour mettre à jour le jeu (mouvement, collision, etc.)
    }

    public void attendreAction() {
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void relacherLatch()
    {
        latch.countDown();
    }

    public void attaque() {
        executor.submit(() -> jeuEnCours.effectuerAttaque());
    }

    public void ramasser() {
        jeuEnCours.ramasser();
        relacherLatch();
    }
}

