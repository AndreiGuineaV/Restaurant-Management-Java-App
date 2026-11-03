package entititati;

public class Produs {
    private final int id;
    private String denumire;
    private CategorieProdus categorieProdus;
    private double pret;

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

    public Produs setDenumire(String denumire) {
        this.denumire = denumire;
        return this;
    }

    public CategorieProdus getCategorieProdus() {
        return categorieProdus;
    }

    public Produs setCategorieProdus(CategorieProdus categorieProdus) {
        this.categorieProdus = categorieProdus;
        return this;
    }

    public double getPret() {
        return pret;
    }

    public Produs setPret(double pret) {
        this.pret = pret;
        return this;
    }
}
