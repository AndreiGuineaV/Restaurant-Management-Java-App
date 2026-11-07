package entititati;

import java.io.Serializable;
import java.util.Objects;

/**
 * Reprezinta un produs din meniul restaurantului.
 */
public class Produs implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private String denumire;
    private CategorieProdus categorieProdus;
    private double pret;

    /**
     * Constructor pentru initializarea unui produs nou.
     * @param id identificatorul unic al produsului
     * @param denumire numele produsului
     * @param categorieProdus categoria din care face parte produsul
     * @param pret pretul unitar in RON
     */
    public Produs(int id, String denumire, CategorieProdus categorieProdus, double pret) {
        this.id = id;
        this.denumire = denumire;
        this.categorieProdus = categorieProdus;
        this.pret = pret;
    }

    public int getId() {
        return id;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public CategorieProdus getCategorieProdus() {
        return categorieProdus;
    }

    public void setCategorieProdus(CategorieProdus categorieProdus) {
        this.categorieProdus = categorieProdus;
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produs produs = (Produs) o;
        return id == produs.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + denumire + " (" + String.format("%.2f", pret) + " RON)";
    }
}
