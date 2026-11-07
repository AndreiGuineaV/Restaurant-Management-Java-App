package entititati;

import java.io.Serializable;
import java.util.Objects;

/**
 * Reprezinta un rand dintr-o comanda, format dintr-un produs si o cantitate.
 */
public class ElementComanda implements Serializable {
    private static final long serialVersionUID = 1L;

    private Produs produs;
    private int cantitate;

    /**
     * Constructor pentru un element de comanda.
     * @param produs produsul comandat
     * @param cantitate numarul de bucati comandate
     */
    public ElementComanda(Produs produs, int cantitate) {
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void setCantitate(int cantitate) {
        this.cantitate = cantitate;
    }

    /**
     * Calculeaza subtotalul pentru acest produs in functie de cantitate si pret unitar.
     * @return valoarea subtotalului
     */
    public double getSubtotal() {
        if (produs == null) return 0.0;
        return this.produs.getPret() * this.cantitate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementComanda that = (ElementComanda) o;
        return cantitate == that.cantitate && Objects.equals(produs, that.produs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produs, cantitate);
    }

    @Override
    public String toString() {
        return produs.getDenumire() + " x " + cantitate + " buc. = " + String.format("%.2f", getSubtotal()) + " RON";
    }
}
