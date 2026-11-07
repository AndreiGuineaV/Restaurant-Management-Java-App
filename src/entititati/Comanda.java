package entititati;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reprezinta o comanda plasata de clienti la o masa din restaurant.
 */
public class Comanda implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private LocalDateTime dataOra;
    private int idMasa;
    private List<ElementComanda> listaElemente;
    private StareComanda stareComanda;

    /**
     * Constructor complet pentru initializarea unei comenzi.
     * @param id identificatorul unic al comenzii
     * @param dataOra data si ora plasarii comenzii
     * @param idMasa numarul mesei unde se livreaza comanda
     * @param listaElemente lista de produse si cantitati
     * @param stareComanda starea comenzii (inregistrata sau achitata)
     */
    public Comanda(int id, LocalDateTime dataOra, int idMasa, List<ElementComanda> listaElemente, StareComanda stareComanda) {
        this.id = id;
        this.dataOra = dataOra;
        this.idMasa = idMasa;
        this.listaElemente = (listaElemente != null) ? new ArrayList<>(listaElemente) : new ArrayList<>();
        this.stareComanda = (stareComanda != null) ? stareComanda : StareComanda.INREGISTRATA;
    }

    /**
     * Constructor secundar care seteaza implicit starea INREGISTRATA.
     * @param id identificatorul unic al comenzii
     * @param dataOra data si ora comenzii
     * @param idMasa numarul mesei
     * @param listaElemente lista de produse
     */
    public Comanda(int id, LocalDateTime dataOra, int idMasa, List<ElementComanda> listaElemente) {
        this(id, dataOra, idMasa, listaElemente, StareComanda.INREGISTRATA);
    }

    public int getId() {
        return id;
    }

    public int getIdMasa() {
        return idMasa;
    }

    public void setIdMasa(int idMasa) {
        this.idMasa = idMasa;
    }

    public List<ElementComanda> getListaElemente() {
        return Collections.unmodifiableList(listaElemente);
    }

    public void setListaElemente(List<ElementComanda> listaElemente) {
        this.listaElemente = (listaElemente != null) ? new ArrayList<>(listaElemente) : new ArrayList<>();
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public StareComanda getStareComanda() {
        return stareComanda;
    }

    public void setStareComanda(StareComanda stareComanda) {
        this.stareComanda = stareComanda;
    }

    /**
     * Calculeaza valoarea totala a comenzii prin insumarea subtotalurilor fiecarui produs.
     * @return valoarea totala de plata in RON
     */
    public double getValoareTotala() {
        if (this.listaElemente == null || this.listaElemente.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (ElementComanda elem : listaElemente) {
            total += elem.getSubtotal();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Comanda #" + id + " [Masa " + idMasa + "] - " + stareComanda + " - Total: " + String.format("%.2f", getValoareTotala()) + " RON";
    }
}
