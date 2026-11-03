package entititati;

import java.time.LocalDateTime;
import java.util.List;

public class Comanda {
    private final int id;
    LocalDateTime dataOra;
    private int idMasa;
    private List<ElementComanda> listaElemente;
    private StareComanda stareComanda;

    public Comanda(int id, LocalDateTime dataOra, int idMasa, List<ElementComanda> listaElemente) {
        this.id = id;
        this.dataOra = dataOra;
        this.idMasa = idMasa;
        this.listaElemente = listaElemente;
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

    public List<ElementComanda> getListaElemente() {
        return listaElemente;
    }

    public Comanda setListaElemente(List<ElementComanda> listaElemente) {
        this.listaElemente = listaElemente;
        return this;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public Comanda setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
        return this;
    }

    public double getValoareTotala()
    {
        if(this.listaElemente == null || this.listaElemente.isEmpty())
        {
            return 0.0;
        }
        return this.listaElemente.stream().mapToDouble(ElementComanda::getSubtotal).sum();
    }
}
