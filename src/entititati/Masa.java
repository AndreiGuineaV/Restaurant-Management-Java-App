package entititati;

import java.io.Serializable;
import java.util.Objects;

/**
 * Reprezinta o masa din cadrul restaurantului.
 */
public class Masa implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private int nrLocuri;
    private boolean esteOcupata;

    /**
     * Constructor pentru o masa.
     * @param id numarul de identificare al mesei
     * @param nrLocuri numarul de locuri disponibile la masa
     */
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

    public void setNrLocuri(int nrLocuri) {
        this.nrLocuri = nrLocuri;
    }

    public boolean isEsteOcupata() {
        return esteOcupata;
    }

    public void setEsteOcupata(boolean esteOcupata) {
        this.esteOcupata = esteOcupata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Masa masa = (Masa) o;
        return id == masa.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Masa #" + id + " (" + nrLocuri + " locuri) - " + (esteOcupata ? "Ocupata" : "Libera");
    }
}
