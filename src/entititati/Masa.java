package entititati;

public class Masa {
    private final int id;
    private int nrLocuri;
    private boolean esteOcupata;

    public Masa(int id, int nrLocuri) {
        this.id = id;
        this.nrLocuri = nrLocuri;
        this.esteOcupata = false;
    }

    public int getId() {
        return id;
    }

    public int getNrLocuri() {
        return nrLocuri;
    }

    public Masa setNrLocuri(int nrLocuri) {
        this.nrLocuri = nrLocuri;
        return this;
    }

    public boolean isEsteOcupata() {
        return esteOcupata;
    }

    public Masa setEsteOcupata(boolean esteOcupata) {
        this.esteOcupata = esteOcupata;
        return this;
    }
}
