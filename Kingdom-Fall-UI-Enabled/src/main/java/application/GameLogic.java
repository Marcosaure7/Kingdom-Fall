package application;

import controller.FenetreAppController;
import exceptions.InventairePleinException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameLogic extends Thread {
    private volatile boolean running = true; // Contrôle l'exécution du thread

    public Jeu jeuEnCours;
    private ExecutorService executor;
    private CountDownLatch latchFinTour;
    private FenetreAppController controller;

    public GameLogic(FenetreAppController controller)
    {
        super();
        this.controller = controller;
        controller.setThread(this);
        this.executor = Executors.newSingleThreadExecutor();
        this.jeuEnCours = new Jeu(this, controller);
    }

    @Override
    public void run() {
        jeuEnCours.lancerJeu();
    }

    public Jeu getJeuEnCours()
    {
        return jeuEnCours;
    }

    public void attendreFinTour() {
        try {
            latchFinTour = new CountDownLatch(1);
            latchFinTour.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void relacherLatch()
    {
        latchFinTour.countDown();
    }

    public void attaque() {
        executor.submit(() -> jeuEnCours.effectuerAttaque());
    }

    public void ramasser() {
        try {
            jeuEnCours.ramasser();
            controller.dissiperDrop();
        }
        catch (InventairePleinException e)
        {
            controller.inventairePlein();
        }
    }

    public void updateUsername(String playerName) {
        jeuEnCours.updateUsername(playerName);
    }

    public void equiper() {
        jeuEnCours.equiper();
        relacherLatch();
    }
}

