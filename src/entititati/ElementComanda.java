package entititati;

public class ElementComanda {
    private Produs produs;
    private int cantitate;
    private double subtotal;

    public ElementComanda(Produs produs, int cantitate) {
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public Produs getProdus() {
        return produs;
    }

    public ElementComanda setProdus(Produs produs) {
        this.produs = produs;
        return this;
    }

    public int getCantitate() {
        return cantitate;
    }

    public ElementComanda setCantitate(int cantitate) {
        this.cantitate = cantitate;
        return this;
    }

    public double getSubtotal(){
        return this.produs.getPret() * this.cantitate;
    }
}
