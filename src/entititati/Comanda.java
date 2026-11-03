package entititati;

import java.util.List;

public class Comanda {
    private final int id;
    private int idMasa;
    private List<ElementComanda> listaProduse;
    private StareComanda stareComanda;

    public Comanda(int id, int idMasa, List<ElementComanda> listaProduse) {
        this.id = id;
        this.idMasa = idMasa;
        this.listaProduse = listaProduse;
        stareComanda = StareComanda.INREGISTRATA;
    }

    public int getId() {
        return id;
    }

    public int getIdMasa() {
        return idMasa;
    }

    public Comanda setIdMasa(int idMasa) {
        this.idMasa = idMasa;
        return this;
    }

    public List<ElementComanda> getListaProduse() {
        return listaProduse;
    }

    public Comanda setListaProduse(List<ElementComanda> listaProduse) {
        this.listaProduse = listaProduse;
        return this;
    }

    public StareComanda getStareComanda() {
        return stareComanda;
    }
}
