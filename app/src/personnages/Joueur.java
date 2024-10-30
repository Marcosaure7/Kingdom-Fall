package personnages;

import application.App;
import exceptions.InventairePleinException;
import objets.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Joueur extends Entite {

    private static final double XP_CAP_INC = 1.5;

    // La Map contient comme cle un type d'objet et comme valeur la liste des objets de ce type dans l'inventaire.
    private final Inventaire inventaire;
    private Arme armeEquipee;
    private Armure armureEquipee;
    private Exp xp;
    private int xpCap;
    private int att;
    private int PVCap;

    public Joueur(int ptsVie, int niveau, int attBase) {
        super("Joueur", ptsVie, niveau, attBase);
        PVCap = ptsVie;
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

    public String toString() {
        return String.format("Joueur\n\nNiv:%d\tPV:%d\tAtt:%d", niveau, ptsVie, attBase);
    }

    public void gainXp (Exp xp) {
        this.xp.gainExp(xp);
        System.out.print("XP: " + this.xp.getValeur() + "/" + this.xpCap);
        if (this.xp.getValeur() >= xpCap) {
            gainNiveau();
        }
        System.out.println();
    }

    private void gainNiveau() {
        // Le joueur est entièrement soigné et ses PV max augmente de 10 par niveau gagné.
        int ancienNiveau = niveau;
        int ancienPVCap = PVCap;

        while (xp.getValeur() >= xpCap) {
            this.xp = new Exp(this.xp.getValeur() - xpCap);
            xpCap = (int) (xpCap * XP_CAP_INC);
            niveau++; PVCap += 10;
        }

        System.out.printf("\tVous montez de niveau !\tNiv:%d -> %d\tPV:%d -> %d%n", ancienNiveau, niveau, ancienPVCap, PVCap);
        ptsVie = PVCap;


        System.out.printf("Prochain niveau : XP: %d/%d", this.xp.getValeur(), xpCap);


    }

    public int getAtt() {
        return att;
    }

    public EffetStatut getEffetArme() { return armeEquipee.getEffet(); }

    public class Inventaire {
        protected Map<Type_Objet, ArrayList<Objet>> inv = new HashMap<>();

        public Inventaire() {
            for (Type_Objet o: Type_Objet.values()) {
                inv.put(o, new ArrayList<>());
            }
        }

        public void ramasserObjet(@NotNull Objet objet) throws InventairePleinException {
            // Check si inventaire disponible
            if (inv.get(objet.getType()).size() < objet.getType().getEspaceInventaire()) {
                // Il y a de la place, on ajoute l'objet :
                inv.get(objet.getType()).addFirst(objet);
            } else {
                throw new InventairePleinException("Inventaire plein!");
            }
        }

        public void ouvrirInventaire(Scanner sc) {
            boolean sortir = false;
            while (!sortir) {
                sortir = ouvrirType(sc);
            }
        }

        private boolean ouvrirType(Scanner sc) {
            boolean quitter = false;
            String type;
            Type_Objet typeObjet = null;
            while (!quitter) {
                boolean actionReconnue = false;
                do {
                    afficherTypes();

                    // On regarde quelle section de l'inventaire ouvrir
                    type = sc.nextLine().toUpperCase();

                    if (checkQuitter(type) || type.equalsIgnoreCase("retour")) {
                        quitter = true;
                        break;
                    }

                    try {
                        typeObjet = Type_Objet.valueOf(type);
                        actionReconnue = true;

                    } catch (IllegalArgumentException e) {
                        System.out.println(App.CMD_INCONNUE);
                    }
                }
                while (!actionReconnue);
                if (quitter)
                    break;

                boolean[] traitement; // Traite l'instruction reçue par l'utilisateur (cmdReconnue?, retour?, quitter?)
                do {
                    printInventaireType(typeObjet);

                    StringBuilder commandes = new StringBuilder();
                    commandes.append("Info:info <objet>,\tJeter:jet <objet>,\tRetour:retour\tQuitter:quitter");

                    if (type.equalsIgnoreCase("ARMES") || type.equalsIgnoreCase("ARMURES"))
                        commandes.insert(0, "equiper:equip <objet>,\tDesequiper:dequip,\t");
                    else if (type.equalsIgnoreCase("POTIONS") || type.equalsIgnoreCase("DIVERS"))
                        commandes.insert(0, "Utiliser:use\t");

                    System.out.println(commandes);



                    String cmd = sc.nextLine().toLowerCase();
                    if (checkQuitter(cmd)) {
                        quitter = true;
                        break;
                    }
                    traitement = traiterCommandeDedansType(cmd, typeObjet);
                    quitter = traitement[2];

                } while (!quitter && !traitement[1]);
            }

            return quitter;
        }

        /**
         *
         * @param cmd Commande reçue
         * @param typeObjet Type d'objet ouvert dans l'inventaire
         * @return Un tableau booléen, où :
         * [0] : La commande a ete reconnue ou non ;
         * [1] : L'utilisateur a demandé un retour ;
         * [2] : L'utilisateur a demandé de quitter l'inventaire.
         */
        private boolean[] traiterCommandeDedansType(String cmd, Type_Objet typeObjet) {
            boolean retour = cmd.equalsIgnoreCase("retour");
            boolean quitter = cmd.equalsIgnoreCase("quitter");
            boolean cmdReconnue = false;

            if (!retour && !quitter) {
                ArrayList<String> commandesPossibles = new ArrayList<>(Arrays.asList("info", "jet"));

                if (typeObjet == Type_Objet.ARMES || typeObjet == Type_Objet.ARMURES) {
                    commandesPossibles.add("equip");
                    commandesPossibles.add("dequip");
                }
                else if (typeObjet == Type_Objet.POTIONS || typeObjet == Type_Objet.DIVERS) {
                    commandesPossibles.add("use");
                }

                String[] commandeDeveloppee = cmd.split(" ");
                String arg = "";
                for (int i = 1; i < commandeDeveloppee.length; i++)
                    arg += commandeDeveloppee[i] + " ";

                arg = arg.trim();


                if (commandeDeveloppee[0].equalsIgnoreCase("dequip")) {
                    if (typeObjet == Type_Objet.ARMES && armeEquipee != null) {
                        cmdReconnue = true;
                        for (int i = 0; i < Type_Objet.ARMES.getEspaceInventaire(); i++) {
                            if (inv.get(typeObjet).get(i) == null) {
                                inv.get(typeObjet).add(i, armeEquipee);
                                break;
                            }
                        }
                        armeEquipee = null;
                        att = attBase;
                    } else if (typeObjet == Type_Objet.ARMURES && armureEquipee != null) {
                        cmdReconnue = true;
                        for (int i = 0; i < Type_Objet.ARMURES.getEspaceInventaire(); i++) {
                            if (inv.get(typeObjet).get(i) == null) {
                                inv.get(typeObjet).add(i, armureEquipee);
                                break;
                            }
                        }
                        armureEquipee = null;

                    }
                    else {
                        System.out.println("Aucun objet equipe !");
                    }
                }

                else if (commandesPossibles.contains(commandeDeveloppee[0])) {
                    int indObjetSelectionne = invContient(typeObjet, arg);

                    if (indObjetSelectionne == -1) {
                        switch (commandeDeveloppee[0]) {
                            case "equip":
                                System.out.printf("Vous avez deja equipe %s%n", arg);
                                break;
                            case "info":
                                cmdReconnue = true;
                                if (typeObjet == Type_Objet.ARMES)
                                    System.out.printf("%s%n%s%n%s", App.LIGNE, armeEquipee.getDescription(), App.LIGNE);
                                else if (typeObjet == Type_Objet.ARMURES)
                                    System.out.printf("%s%n%s%n%s", App.LIGNE, armureEquipee.getDescription(), App.LIGNE);
                                try {
                                    Thread.sleep(1000);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                    else if (indObjetSelectionne >= 0) {
                        cmdReconnue = true;
                        Objet objetSelectionne = inv.get(typeObjet).get(indObjetSelectionne);

                        switch (commandeDeveloppee[0]) {
                            case "equip":
                                equiper(objetSelectionne, indObjetSelectionne);
                                break;
                            case "use":
                                if (objetSelectionne instanceof Potion p)
                                {
                                    soigner(p);
                                    inv.get(Type_Objet.POTIONS).remove(indObjetSelectionne);
                                }
                                else if (objetSelectionne instanceof Divers obj) {
                                    try {
                                        obj.utiliser();
                                        inv.get(Type_Objet.DIVERS).remove(indObjetSelectionne);
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                else
                                    System.out.println("Cet objet ne peut pas être 'utilise' !");
                                break;
                            case "info":
                                System.out.printf("%s%n%s%n%s", App.LIGNE, objetSelectionne.getDescription(), App.LIGNE);
                                try {
                                    Thread.sleep(1000);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "jet":
                                inv.get(typeObjet).remove(indObjetSelectionne);
                                System.out.printf("%s supprime%n", objetSelectionne.getNom());
                                break;
                        }
                    }
                    else {
                        System.out.println("L'objet entre n'est pas dans l'inventaire !");
                    }

                }

                else {
                    System.out.println("La commande n'a pas ete reconnue!");
                }
            }

            return new boolean[]{cmdReconnue, retour, quitter};
        }

        private void soigner(Potion p) {
            soignerJoueur(p.getSoin());
            System.out.printf("Vous vous soignez de %d points de vie!%n", p.getSoin());
        }

        private void equiper(Objet objetSelectionne, int indObjetSelectionne) {
            if (objetSelectionne instanceof Arme armeSelectionnee) {
                Arme armeAncienne = armeEquipee;
                armeEquipee = armeSelectionnee;
                inv.get(Type_Objet.ARMES).remove(indObjetSelectionne);
                inv.get(Type_Objet.ARMES).addFirst(armeAncienne);
                att = attBase + armeEquipee.getDegats();
            }
            else if (objetSelectionne instanceof Armure armureSelectionnee) {
                Armure armureAncienne = armureEquipee;
                armureEquipee = armureSelectionnee;
                inv.get(Type_Objet.ARMURES).remove(indObjetSelectionne);
                inv.get(Type_Objet.ARMURES).addFirst(armureAncienne);
            }

        }

        /**
         * Sert à savoir si la String en entree correspond à un objet de la
         * classe typeObjet est dans l'iventaire.
         * @param typeObjet Le type Type_Objet de l'objet
         * @param objet Le nom de l'objet
         * @return l'index de l'objet trouve, si l'objet est equipe -1, sinon -2
         */
        private int invContient(Type_Objet typeObjet, String objet) {
            for (Objet obj: inv.get(typeObjet)) {
                if (obj != null && obj.getNom().equalsIgnoreCase(objet))
                    return inv.get(typeObjet).indexOf(obj);
            }

            if ((typeObjet == Type_Objet.ARMES && armeEquipee != null && armeEquipee.getNom().equalsIgnoreCase(objet)
                    || (typeObjet == Type_Objet.ARMURES && armureEquipee != null && armureEquipee.getNom().equalsIgnoreCase(objet))))
                return -1;
            return -2;
        }

        /**
         * Sert à afficher les objets de la section de l'inventaire choisi.
         */
        private void printInventaireType(Type_Objet typeObjet) {
            StringBuilder inventaireDuType = new StringBuilder("\n");

            for (int i = 0; i < typeObjet.getEspaceInventaire(); i++) {
                String objetStr;
                if (i > inv.get(typeObjet).size() - 1 || inv.get(typeObjet).get(i) == null)
                    objetStr = "(Vide)";
                else
                    objetStr = inv.get(typeObjet).get(i).getNom();

                inventaireDuType.append(objetStr).append("\n");
            }
            switch (typeObjet) {
                case ARMES:
                    inventaireDuType.insert(1, String.format("Arme equipee : %s%n%n", armeEquipee == null ? "Aucun(e)" : armeEquipee.getNom()));
                    break;
                case ARMURES:
                    inventaireDuType.insert(1, String.format("Armure equipee : %s%n%n", armureEquipee == null ? "Aucun(e)" : armureEquipee.getNom()));
                    break;
            }
            System.out.println(inventaireDuType);
        }

        private void afficherTypes() {
            System.out.println();
            for (Type_Objet type : inv.keySet()) {
                System.out.println(type);
            }

            System.out.println("Taper le type à ouvrir ou sinon : Quitter:quitter");
        }

        private boolean checkQuitter(String arg) {
            return arg.equalsIgnoreCase("quitter");
        }
    }

    private void soignerJoueur(int soin) {
        if (PVCap < ptsVie + soin)
            this.ptsVie += soin;
        else
            this.ptsVie = PVCap;
    }

}
