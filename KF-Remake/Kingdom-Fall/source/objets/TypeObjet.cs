namespace objets
{
    public enum TypeObjet
    {
        Armes,
        Armures,
        Potions,
        Divers
    }


    public static class TypeObjetExtensions
    {
        private static readonly Dictionary<TypeObjet, (int espaceInventaire, bool equipable)> _typeData = 
            new Dictionary<TypeObjet, (int, bool)>
            {
                { TypeObjet.Armes, (5, true) },
                { TypeObjet.Armures, (5, true) },
                { TypeObjet.Potions, (5, false) },
                { TypeObjet.Divers, (10, false) }
            };

        public static int GetEspaceInventaire(this TypeObjet type)
        {
            return _typeData[type].espaceInventaire;
        }

        public static bool IsEquipable(this TypeObjet type)
        {
            return _typeData[type].equipable;
        }
    }

}



