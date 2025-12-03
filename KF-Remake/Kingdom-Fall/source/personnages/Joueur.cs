using controllers;
using objets;

namespace personnages;

public class Joueur : Entite
{
    private const double XpCapInc = 1.5;

    // La Map contient comme clé un type d'objet et comme valeur la liste d'objets associée
    internal Inventaire inventaire { get; private set; }
    private Arme armeEquipee;
    private Armure? armureEquipee;
    internal Exp xp { get; private set; }
    internal int xpCap { get; private set; }
    internal int att { get; private set; }
    private App.Jeu jeu;

    public Joueur(App.Jeu jeu, int ptsVie, int niveau, int attBase) : base("Joueur", ptsVie, niveau, attBase)
    {
        this.jeu = jeu;
        xp = new Exp(0);
        xpCap = 10;
        att = attBase;
        armeEquipee = new Arme("Vieille branche", "Un vieux bout de bois trouvé par terre", 0.0, 0, "Aucun");
        inventaire = new Inventaire(this);
    }

    public override string ToString()
    {
        return $"Joueur\n\nNiv:{niveau}\tPV:{ptsVie}\tAtt:{attBase}";
    }

    public void Renommer(string nom)
    {
        this.nom = nom;
    }

    public void GainXp(objets.Exp xp)
    {
        this.xp.GainXp(xp);
        if (this.xp.valeur >= xpCap) GainNiveau();
    }

    private void GainNiveau()
    {
        // Le joueur est entièrement soigné et ses PV max augmentent de 10

        while (xp.valeur >= xpCap)
        {
            this.xp = new objets.Exp(this.xp.valeur - xpCap);
            xpCap = (int)(xpCap * XpCapInc);
            niveau++; ptsVie += 10;
        }
        vieRestante = ptsVie;
    }

    public EffetStatut GetEffetArme()
    {
        return armeEquipee.effet;
    }

    public Objet? GetEquip(TypeObjet type)
    {
        return type == TypeObjet.Armes ? armeEquipee : armureEquipee;
    }

    protected override void RecoitAttaqueSelonArmure(int attaqueRecue)
    {
        if (armureEquipee != null)
        {
            attaqueRecue = armureEquipee.MangerAttaque(attaqueRecue);
            ptsArmure = armureEquipee.ptsArmure;
        }

        base.RecoitAttaqueSelonArmure(attaqueRecue);
    }

    /**
     * Reset l'armure du joueur
     */
    public void ennemiVaincu()
    {
        if (armureEquipee != null)
        {
            armureEquipee.ResetArmure();
            ptsArmure = armureEquipee.ptsArmure;
        }
        foreach (Objet armure in inventaire.GetListType(TypeObjet.Armures))
        {
            if (armure != null)
                ((Armure)armure).ResetArmure();
        }
    }


    public class Inventaire
    {
        protected Dictionary<TypeObjet, List<Objet>> inv = [];
        private Joueur joueur;

        public Inventaire(Joueur joueur)
        {
            this.joueur = joueur;

            foreach (TypeObjet type in Enum.GetValues(typeof(TypeObjet)))
            {
                inv[type] = new List<Objet>(TypeObjetExtensions.GetEspaceInventaire(type));
            }
        }

        public void RamasserObjet(Objet objet)
        {
            if (inv[objet.type].Count < TypeObjetExtensions.GetEspaceInventaire(objet.type))
            {
                // Il y a de la place pour ajouter l'objet
                inv[objet.type].Add(objet);
            }
            else
            {
                throw new exceptions.InventoryFullException($"L'inventaire est plein pour le type {objet.type}");
            }
        }

        private void Soigner(Potion p)
        {
            joueur.SoignerJoueur(p.soin);
            FenetreAppController.GetSingleton().EnvoyerMessage($"Vous vous soignez de {p.soin} points de vie !");
        }

        public void Equiper(Objet objetSelectionne, int indObjetSelectionne)
        {
            if (objetSelectionne is Arme armeSelectionnee)
            {
                Arme armeAncienne = joueur.armeEquipee;
                joueur.armeEquipee = armeSelectionnee;
                Jeter(TypeObjet.Armes, indObjetSelectionne);
                inv[TypeObjet.Armes].Insert(0, armeAncienne);
                joueur.att = joueur.attBase + armeSelectionnee.degats;
            }
            else if (objetSelectionne is Armure armureSelectionnee)
            {
                if (joueur.armureEquipee != null)
                {
                    Armure ancienneArmure = joueur.armureEquipee;
                    Jeter(TypeObjet.Armures, indObjetSelectionne);
                    inv[TypeObjet.Armures].Insert(0, ancienneArmure);
                }

                joueur.armureEquipee = armureSelectionnee;
                joueur.ptsArmure = armureSelectionnee.ptsArmure;
                FenetreAppController.GetSingleton().EquiperArmure(armureSelectionnee);
            }
        }

        public void Utiliser(Objet objetSelectionne, int indObjetSelectionne)
        {
            if (objetSelectionne is Potion p)
            {
                Jeter(TypeObjet.Potions, indObjetSelectionne);
                Soigner(p);
            }
            else if (objetSelectionne is Divers obj)
            {
                obj.utiliser();
                Jeter(TypeObjet.Divers, indObjetSelectionne);
            }
            else
                FenetreAppController.GetSingleton().EnvoyerMessage("Cet objet ne peut pas être utilisé.");
        }

        /// <summary>
        /// Sert à savoir si l'objet en entrée correspond à un objet de
        /// classe typeObjet est dans l'inventaire.
        /// </summary>
        /// <param name="objet">L'objet à vérifier</param>
        /// <returns>L'index de l'objet dans l'inventaire, -1 si l'objet est équipé, sinon -2.</returns>
        public (int index, bool equip) InvContient(Objet objet)
        {
            if (objet is null)
                return (-1, false);

            TypeObjet typeObjet = objet.type;

            foreach (Objet obj in inv[typeObjet])
            {
                if (obj.equals(objet))
                {
                    return (inv[typeObjet].IndexOf(obj), false);
                }
            }

            if (typeObjet == TypeObjet.Armes && joueur.armeEquipee.equals(objet)
                    || typeObjet == TypeObjet.Armures && joueur.armureEquipee != null && joueur.armureEquipee.equals(objet))
                return (-1, true); // est équipée

            return (-1, false); // n'est pas dans l'inventaire
        }

        public List<Objet> GetListType(TypeObjet type)
        {
            return inv[type];
        }

        public void Jeter(TypeObjet type, int index)
        {
            inv[type].RemoveAt(index);
        }
    }

    private void SoignerJoueur(int soin)
    {
        if (ptsVie > vieRestante + soin)
            this.vieRestante += soin;
        else
            this.vieRestante = ptsVie; // On ne peut pas dépasser les PV max

        jeu.AfficherSoin(this);
    }
}