namespace personnages
{
   public abstract class Entite {
        public string nom { get; protected set; }
        public int ptsVie { get; protected set; }
        public int vieRestante { get; protected set; }
        internal int niveau { get; set; }
        internal int attBase { get; }
        internal EffetStatut effetStatut { get; private set; }
        int dureeEffetStatut;
        int degatsEffetStatut;
        public int ptsArmure { get; protected set; }


        public Entite()
        {
            this.nom = "";
            this.ptsVie = 0;
            this.vieRestante = 0;
            this.niveau = 0;
            this.attBase = 0;
        }

        public Entite(string nom, int ptsVie, int niveau, int attBase)
        {
            this.nom = nom;
            this.niveau = niveau;
            this.ptsVie = ptsVie;
            this.vieRestante = ptsVie;
            this.attBase = attBase;
            effetStatut = EffetStatut.Aucun;
            this.dureeEffetStatut = 0;
            this.ptsArmure = 0;
            this.degatsEffetStatut = 0;
        }


        public Entite(Entite autre)
        {
            this.nom = autre.nom;
            this.niveau = autre.niveau;
            this.ptsVie = autre.ptsVie;
            this.vieRestante = autre.vieRestante;
            this.attBase = autre.attBase;
            this.effetStatut = autre.effetStatut;
            this.dureeEffetStatut = autre.dureeEffetStatut;
            this.degatsEffetStatut = autre.degatsEffetStatut;
            this.ptsArmure = autre.ptsArmure;
        }

        public string seFaitAttaquer(int attaqueRecue, EffetStatut effetStatutApplique)
        {
            string resultat = "";

            if (effetStatut == EffetStatut.Aucun)
            {
                effetStatut = effetStatutApplique;
                dureeEffetStatut = (int)effetStatut;
            }

            switch (effetStatut)
            {
                case EffetStatut.Saignement:
                    degatsEffetStatut = (int)(0.3*attaqueRecue); 
                    break;

                default:
                    degatsEffetStatut = 0;
                    break;
            }

            RecoitAttaqueSelonArmure(attaqueRecue);

            resultat += SubirEffetPeriodique();

            if (this.vieRestante <= 0)
                this.vieRestante = 0;

            return resultat;
        }

        public string SubirEffetPeriodique()
        {
            string message = "";
            if (dureeEffetStatut > 0)
            {
                switch (effetStatut)
                {
                    case EffetStatut.Saignement:
                        vieRestante -= degatsEffetStatut;
                        --dureeEffetStatut;
                        message += $"{nom} subit un effet de {effetStatut.ToString()} de {degatsEffetStatut} PV ce tour-ci !\tReste : {dureeEffetStatut} tours à subir {effetStatut.ToString()} !\n";
                        break;
                }
            }

            return message;
        }

        public bool estMort()
        {
            return vieRestante <= 0;
        }

        protected virtual void RecoitAttaqueSelonArmure(int attaqueRecue)
        {
            vieRestante -= attaqueRecue;
        }
    }
}

