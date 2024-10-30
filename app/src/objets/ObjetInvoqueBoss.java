package objets;

import application.Donjon;
import personnages.Boss;

public class ObjetInvoqueBoss extends Divers {

    private final Boss bossInvoque;
    private final Donjon donjon;

    public ObjetInvoqueBoss (String nom, String description, double dropRate, Boss bossInvoque, Donjon donjon) {
        super(nom, description, dropRate);
        this.bossInvoque = bossInvoque;
        this.donjon = donjon;
    }

    public ObjetInvoqueBoss (ObjetInvoqueBoss source)
    {
        super(source);
        bossInvoque = source.bossInvoque;
        donjon = source.donjon;
    }

    @Override
    public void utiliser ()
    {
        donjon.bossEnFileDattente(bossInvoque);
    }
}