package personnages;

import exceptions.InventairePleinException;
import objets.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Joueur extends Entite {

    // La Map contient comme clé un type d'objet et comme valeur la liste des objets de ce type dans l'inventaire.
    private final Inventaire inventaire;
    private Arme armeEquipee;
    private Exp xp;
    private int xpCap;

    public Joueur(int ptsVie, int niveau, int attBase) {
        super(ptsVie, niveau, attBase);
        inventaire = new Inventaire();
        xp = new Exp(0);
        xpCap = 10;
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
            System.out.println(String.format("\tVous montez de niveau !\tNiv:%d -> %d", niveau, ++niveau));
            this.xp = new Exp(this.xp.getValeur() - xpCap);
            xpCap = (int) (xpCap * 1.5);
            System.out.println(String.format("Prochain niveau : XP: %d/%d", this.xp.getValeur(), xpCap));
        }
    }

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
                inv.get(objet.getType()).add(objet);
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

                    if (checkQuitter(type)) {
                        quitter = true;
                        break;
                    }

                    try {
                        typeObjet = Type_Objet.valueOf(type);
                        actionReconnue = true;

                    } catch (IllegalArgumentException e) {
                        System.out.println("La commande n'a pas été reconnue !");
                    }
                }
                while (!actionReconnue);
                if (quitter)
                    break;

                boolean[] traitement; // Traite l'instruction reçue par l'utilisateur (cmdReconnue?, retour?, quitter?)
                do {
                    printInventaireType(typeObjet);

                    StringBuilder commandes = new StringBuilder();
                    commandes.append("Info:info,\tJeter:jet,\tRetour:retour\tQuitter:quitter");

                    if (type.equalsIgnoreCase("ARMES") || type.equalsIgnoreCase("ARMURES"))
                        commandes.insert(0, "Équiper:equip,\t");
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

                } while (!quitter && traitement[0] && !traitement[1]);
            }

            return quitter;
        }

        /**
         *
         * @param cmd Commande reçue
         * @param typeObjet Type d'objet ouvert dans l'inventaire
         * @return Un tableau booléen, où :
         * [0] : La commande a été reconnue ou non;
         * [1] : L'utilisateur a demandé un retour;
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
                }
                else if (typeObjet == Type_Objet.POTIONS || typeObjet == Type_Objet.DIVERS) {
                    commandesPossibles.add("use");
                }

                String[] commandeDeveloppee = cmd.split(" ");

                if (commandeDeveloppee.length != 2) {
                    System.out.println("La commande doit se composer de 2 mots ou moins!");

                } else if (commandesPossibles.contains(commandeDeveloppee[0])) {
                    int indObjetSelectionne = invContient(typeObjet, commandeDeveloppee[1]);

                    if (indObjetSelectionne >= 0) {
                        System.out.println("La commande a été enregistrée.");
                        cmdReconnue = true;

                        Objet objetSelectionne = inv.get(typeObjet).get(indObjetSelectionne);

                        switch (commandeDeveloppee[0]) {
                            case "equip":
                                if (typeObjet == Type_Objet.ARMES) {
                                    Arme armeAncienne = armeEquipee;
                                    armeEquipee = (Arme) objetSelectionne;
                                    inv.get(Type_Objet.ARMES).remove(indObjetSelectionne);
                                    inv.get(Type_Objet.ARMES).add(armeAncienne);
                                }
                                else {
                                    // TODO Si armure (pas codé encore)
                                }
                                break;
                            case "use":
                                // TODO Utiliser un objet divers ou une potion
                                break;
                            case "info":
                                System.out.println(objetSelectionne.getDescription());
                                break;
                            case "jet":
                                inv.get(typeObjet).remove(indObjetSelectionne);
                                System.out.printf("%s supprimé%n", objetSelectionne.getNom());
                                break;
                        }
                    }

                }

                else {
                    System.out.println("La commande n'a pas été reconnue!");
                }
            }

            return new boolean[]{cmdReconnue, retour, quitter};
        }

        private int invContient(Type_Objet typeObjet, String objet) {
            for (Objet obj: inv.get(typeObjet)) {
                if (obj != null && obj.getNom().equalsIgnoreCase(objet))
                    return inv.get(typeObjet).indexOf(obj);
            }
            return -1;
        }

        /**
         * Sert à afficher les objets de la section de l'inventaire choisi.
         */
        private void printInventaireType(Type_Objet typeObjet) {
            StringBuilder inventaireDuType = new StringBuilder();

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
                    System.out.printf("%nArme équipée : %s%n%n", armeEquipee == null ? "Aucun(e)" : armeEquipee.getNom());
                    break;
                case ARMURES:
                    System.out.printf("%nArmure équipée : %s%n%n", "ARMURE_EQUIPEE");
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

}
