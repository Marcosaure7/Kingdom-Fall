using System.Collections;
using Avalonia.Controls;
using Avalonia.Media;

namespace objets 
{
    public abstract class Objet : Drops
    {
        internal TypeObjet type { get; private set; }
        internal string nom { get; private set; }
        internal double dropRate { get; private set; }
        protected string dropRateString { get { return _dropRateString; }}
        private string _dropRateString = "";

        public Objet(TypeObjet type, string nom, double dropRate)
        {
            this.type = type;
            this.nom = nom;
            this.dropRate = dropRate;
        }

        public Objet (Objet autre)
        {
            this.type = autre.type;
            this.nom = autre.nom;
            this.dropRate = autre.dropRate;
            this._dropRateString = autre.dropRateString;
        }

        public override string ToString()
        {
            return nom;
        }

        public abstract string GetDescription();
        public abstract StackPanel FormatComparedDescription(personnages.Joueur joueur);

        public static List<Objet> PondererDropRates(List<Objet> objets, string ennemi)
        {
            double totalDropRates = 0;
            foreach (Objet objet in objets)
            {
                totalDropRates += objet.dropRate;
            }
            foreach (Objet objet in objets)
            {
                objet.dropRate /= totalDropRates;
                objet._dropRateString = $"{(objet.dropRate * 100):F2}% ({ennemi})";
            }

            return objets;
        }

        public bool equals(Objet objet)
        {
            return nom.Equals(objet.nom);
        }

        protected TextBlock GetComparisonText(int newStat, int oldStat)
        {
            int diff = newStat - oldStat;
            if (diff == 0)
                return new TextBlock { Text = " -" };

            TextBlock retour = new TextBlock();
            if (diff > 0)
            {
                retour.Text = $" ⏶ {diff}";
                retour.Foreground = Brushes.Green;
            }
            else
            {
                diff = -diff;
                retour.Text = $" ⏷ {diff}";
                retour.Foreground = Brushes.Red;
            }

            return retour;
        }
    }
}
