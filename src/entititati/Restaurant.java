package entititati;

import java.io.Serializable;
import java.util.*;

/**
 * Clasa Singleton ce gestioneaza informatiile si mesele restaurantului Kimu.
 */
public class Restaurant implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Restaurant INSTANCE = new Restaurant();

    private String denumire;
    private final Map<Integer, Masa> mese;

    private Restaurant() {
        this.denumire = "Kimu";
        this.mese = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            int locuri = (i % 3 == 0) ? 6 : (i % 2 == 0 ? 4 : 2);
            mese.put(i, new Masa(i, locuri));
        }
    }

    /**
     * Returneaza instanta unica a clasei Restaurant.
     * @return instanta singleton
     */
    public static Restaurant getInstance() {
        return INSTANCE;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public int getNrMese() {
        return mese.size();
    }

    /**
     * Verifica daca o masa exista in restaurant.
     * @param idMasa numarul mesei verificate
     * @return true daca masa exista, false altfel
     */
    public boolean existaMasa(int idMasa) {
        return mese.containsKey(idMasa);
    }

    /**
     * Returneaza obiectul Masa corespunzator numarului indicat.
     * @param idMasa numarul mesei
     * @return obiectul Masa sau null daca nu exista
     */
    public Masa getMasa(int idMasa) {
        return mese.get(idMasa);
    }

    /**
     * Returneaza o lista cu toate mesele restaurantului.
     * @return lista de mese
     */
    public List<Masa> getToateMesele() {
        return new ArrayList<>(mese.values());
    }

    /**
     * Calculeaza capacitatea totala de locuri a restaurantului.
     * @return numarul total de locuri
     */
    public int getCapacitateTotala() {
        int total = 0;
        for (Masa m : mese.values()) {
            total += m.getNrLocuri();
        }
        return total;
    }

    /**
     * Actualizeaza starea de ocupare a meselor pe baza setului de id-uri ocupate.
     * @param idMeseOcupate multimea id-urilor meselor cu comenzi active
     */
    public void actualizeazaStareMese(Set<Integer> idMeseOcupate) {
        for (Masa masa : mese.values()) {
            masa.setEsteOcupata(idMeseOcupate != null && idMeseOcupate.contains(masa.getId()));
        }
    }

    @Override
    public String toString() {
        return denumire + " (" + getNrMese() + " mese, capacitate: " + getCapacitateTotala() + " locuri)";
    }
}
