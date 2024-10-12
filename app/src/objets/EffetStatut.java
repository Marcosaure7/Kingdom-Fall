package objets;

public enum EffetStatut {

        AUCUN(0), SAIGNEMENT(3), POISON(5), GELE(1), REGEN(3), FORCE(5), FORTIFIE(5);

        final int duree;

        EffetStatut(int duree)
        {
                this.duree = duree;
        }

        public int getDuree () {
                return duree;
        }
}
