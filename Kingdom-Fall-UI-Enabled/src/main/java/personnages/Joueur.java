package personnages;

import application.App;
import application.Jeu;
import controller.FenetreAppController;
import exceptions.InventairePleinException;
import objets.*;


import java.io.Serializable;
import java.sql.SQLOutput;
import java.util.*;

public class Joueur extends Entite implements Serializable
{
    private static final double XP_CAP_INC = 1.5;

    // La Map contient comme cle un type d'objet et comme valeur la liste des objets de ce type dans l'inventaire.
    private final Inventaire inventaire;
    private Arme armeEquipee;
    private Armure armureEquipee;
    private Exp xp;
    private int xpCap;
    private int att;
    private Jeu jeu;

    public Joueur(Jeu jeu, int ptsVie, int niveau, int attBase)
    {
        super("Joueur", ptsVie, niveau, attBase);
        this.jeu = jeu;
        inventaire = new Inventaire();
        xp = new Exp(0);
        xpCap = 10;
        att = attBase;
        armeEquipee = new Arme ("Vieille branche", "Un vieux bout de bois trouve par terre", 0.0, 0, "AUCUN");
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public void pincer_bouche() {
        ptsVie -= 50;
    }
    
    /**
     * Change le nom
     */ 
    public void renommer(String nom)
    {
        this.nom = nom;
    }

    public String toString() {
        return String.format("Joueur\n\nNiv:%d\tPV:%d\tAtt:%d", niveau, ptsVie, attBase);
    }

    public void gainXp (Exp xp) {
        this.xp.gainExp(xp);
        if (this.xp.getValeur() >= xpCap) gainNiveau();
    }

    private void gainNiveau() {
        // Le joueur est entièrement soigné et ses PV max augmente de 10 par niveau gagné.

        while (xp.getValeur() >= xpCap) {
            this.xp = new Exp(this.xp.getValeur() - xpCap);
            xpCap = (int) (xpCap * XP_CAP_INC);
            niveau++; ptsVie += 10;
        }
        vieRestante = ptsVie;
    }

    public int getAtt() {
        return att;
    }

    public EffetStatut getEffetArme() { return armeEquipee.getEffet(); }

    public Exp getXP() {
        return xp;
    }

    public int getXpCap() {
        return xpCap;
    }

    public Objet getEquip (Type_Objet type)
    {
        return type == Type_Objet.ARMES ? armeEquipee : armureEquipee;
    }

    public int getPtsArmure()
    {
        return ptsArmure;
    }

    @Override
    protected void recoitAttaqueSelonArmure(int attaqueRecue) {
        if (armureEquipee != null) {
            attaqueRecue = armureEquipee.mangerAttaque(attaqueRecue);
            ptsArmure = armureEquipee.getPtsArmure();
        }

        super.recoitAttaqueSelonArmure(attaqueRecue);
    }

    /**
     * Reset l'armure du joueur
     */
    public void ennemiVaincu()
    {
        if (armureEquipee != null) {
            armureEquipee.resetArmure();
            ptsArmure = armureEquipee.getPtsArmure();
        }
        for (Objet armure : inventaire.getListType(Type_Objet.ARMURES))
        {
            if (armure != null)
                ((Armure) armure).resetArmure();
        }
    }

    public class Inventaire implements Serializable
    {
        protected Map<Type_Objet, ArrayList<Objet>> inv = new HashMap<>();

        public Inventaire() {
            for (Type_Objet o: Type_Objet.values()) {
                inv.put(o, new ArrayList<>(o.getEspaceInventaire()));
            }
        }

        public void ramasserObjet(Objet objet) throws InventairePleinException {
            // Check si inventaire disponible
            if (inv.get(objet.getType()).size() < objet.getType().getEspaceInventaire()) {
                // Il y a de la place, on ajoute l'objet :
                inv.get(objet.getType()).addFirst(objet);
            } else {
                throw new InventairePleinException("Inventaire plein!");
            }
        }

        private void soigner(Potion p) {
            soignerJoueur(p.getSoin());
            jeu.controller.envoyerMessage(String.format("Vous vous soignez de %d points de vie!%n", p.getSoin()));
        }

        public void equiper(Objet objetSelectionne, int indObjetSelectionne) {
            if (objetSelectionne instanceof Arme armeSelectionnee) {
                Arme armeAncienne = armeEquipee;
                armeEquipee = armeSelectionnee;
                jeter(Type_Objet.ARMES, indObjetSelectionne);
                inv.get(Type_Objet.ARMES).addFirst(armeAncienne);
                att = attBase + armeEquipee.getDegats();
            }
            else if (objetSelectionne instanceof Armure armureSelectionnee) {
                Armure armureAncienne = armureEquipee;
                armureEquipee = armureSelectionnee;
                jeter(Type_Objet.ARMURES, indObjetSelectionne);
                inv.get(Type_Objet.ARMURES).addFirst(armureAncienne);
                ptsArmure = armureEquipee.getPtsArmure();
                FenetreAppController.getSingleton().equiperArmure(armureEquipee);
            }
        }

        public void utiliser(Objet objetSelectionne, int indObjetSelectionne) {
            if (objetSelectionne instanceof Potion p)
            {
                jeter(Type_Objet.POTIONS, indObjetSelectionne);
                soigner(p);
            }
            else if (objetSelectionne instanceof Divers obj) {
                try {
                    obj.utiliser();
                    jeter(Type_Objet.DIVERS, indObjetSelectionne);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            else
                System.out.println("Cet objet ne peut pas être 'utilise' !");
        }

        /**
         * Sert à savoir si la String en entree correspond à un nomObjet de la
         * classe typeObjet est dans l'iventaire.
         * @param typeObjet Le type Type_Objet de l'nomObjet
         * @param nomObjet Le nom de l'objet
         * @return l'index de l'objet trouve, si l'objet est équipé -1, sinon -2.
         */
        public int invContient(Type_Objet typeObjet, String nomObjet) {
            for (Objet obj: inv.get(typeObjet)) {
                if (obj != null && obj.getNom().equalsIgnoreCase(nomObjet))
                    return inv.get(typeObjet).indexOf(obj);
            }

            if ((typeObjet == Type_Objet.ARMES && armeEquipee != null && armeEquipee.getNom().equalsIgnoreCase(nomObjet)
                    || (typeObjet == Type_Objet.ARMURES && armureEquipee != null && armureEquipee.getNom().equalsIgnoreCase(nomObjet))))
                return -1;
            return -2;
        }

        /**
         * Sert à savoir si l'objet en entree correspond à un objet de la
         * classe typeObjet est dans l'iventaire.
         * @param objet L'objet à regarder
         * @return l'index de l'objet trouve, si l'objet est équipé -1, sinon -2.
         */
        public int invContient(Objet objet) {
            if (objet == null)
                return -2;

            Type_Objet typeObjet = switch (objet) {
                case Arme arme -> Type_Objet.ARMES;
                case Armure armure -> Type_Objet.ARMURES;
                case Potion potion -> Type_Objet.POTIONS;
                default -> Type_Objet.DIVERS;
            };

            for (Objet obj: inv.get(typeObjet)) {
                if (obj != null && objet.equals(obj))
                    return inv.get(typeObjet).indexOf(obj);
            }

            if ((typeObjet == Type_Objet.ARMES && armeEquipee != null && armeEquipee.equals(objet)
                    || (typeObjet == Type_Objet.ARMURES && armureEquipee != null && armureEquipee.equals(objet))))
                return -1;
            return -2;
        }

        public ArrayList<Objet> getListType(Type_Objet type)
        {
            return inv.get(type);
        }

        public void jeter(Type_Objet type, int index)
        {
            inv.get(type).remove(index);
        }
    }

    private void soignerJoueur(int soin) {
        if (ptsVie > vieRestante + soin)
            this.vieRestante += soin;
        else
            this.vieRestante = ptsVie;

        jeu.afficherSoin(this);
    }

}
