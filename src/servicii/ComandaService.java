package servicii;

import entititati.Comanda;
import entititati.ElementComanda;
import entititati.Masa;
import entititati.Produs;
import entititati.Restaurant;
import entititati.StareComanda;
import exceptii.DateInvalideException;
import exceptii.ProdusInexistentException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Serviciu pentru prelucrarea si inregistrarea comenzilor restaurantului.
 */
public class ComandaService {

    private final List<Comanda> comenzi;
    private final CatalogService catalogService;
    private final Restaurant restaurant;

    /**
     * Constructor pentru initializarea serviciului de comenzi.
     * @param comenziInitiale lista de comenzi incarcate din fisiere
     * @param catalogService serviciul de catalog pentru verificarea produselor
     */
    public ComandaService(List<Comanda> comenziInitiale, CatalogService catalogService) {
        this.comenzi = (comenziInitiale != null) ? new ArrayList<>(comenziInitiale) : new ArrayList<>();
        this.catalogService = catalogService;
        this.restaurant = Restaurant.getInstance();
        actualizeazaStareMese();
    }

    /**
     * Genereaza automat urmatorul ID incremental pentru o noua comanda.
     * @return urmatorul id disponibil
     */
    public int genereazaIdNou() {
        return comenzi.stream()
                .mapToInt(Comanda::getId)
                .max()
                .orElse(100) + 1;
    }

    /**
     * Inregistreaza o comanda noua dupa validarea mesei, a produselor si cantitatilor.
     * @param idMasa numarul mesei unde se plaseaza comanda
     * @param produseSiCantitati map cu perechi de forma idProdus - cantitate
     * @return comanda creata si adaugata in memorie
     * @throws DateInvalideException daca masa nu exista sau cantitatile sunt incorecte
     * @throws ProdusInexistentException daca un produs comandat nu este gasit in catalog
     */
    public Comanda inregistreazaComanda(int idMasa, Map<Integer, Integer> produseSiCantitati)
            throws DateInvalideException, ProdusInexistentException {

        if (!restaurant.existaMasa(idMasa)) {
            throw new DateInvalideException("Masa cu numarul " + idMasa + " nu exista in restaurant! " +
                    "Restaurantul '" + restaurant.getDenumire() + "' dispune doar de mesele 1.." + restaurant.getNrMese() + ".");
        }

        if (produseSiCantitati == null || produseSiCantitati.isEmpty()) {
            throw new DateInvalideException("Comanda trebuie sa contina cel putin un produs.");
        }

        List<ElementComanda> elemente = new ArrayList<>();

        for (Map.Entry<Integer, Integer> intrare : produseSiCantitati.entrySet()) {
            int idProdus = intrare.getKey();
            int cantitate = intrare.getValue();

            if (cantitate <= 0) {
                throw new DateInvalideException("Cantitatea pentru produsul cu ID " + idProdus + " trebuie sa fie > 0.");
            }

            Produs produs = catalogService.gasesteProdusDupaId(idProdus);
            elemente.add(new ElementComanda(produs, cantitate));
        }

        int idComanda = genereazaIdNou();
        Comanda comandaNoua = new Comanda(idComanda, LocalDateTime.now(), idMasa, elemente, StareComanda.INREGISTRATA);
        comenzi.add(comandaNoua);

        actualizeazaStareMese();
        return comandaNoua;
    }

    /**
     * Marcheaza o comanda existenta ca fiind achitata si elibereaza masa daca nu mai sunt comenzi active.
     * @param idComanda identificatorul comenzii de achitat
     * @throws DateInvalideException daca comanda cu acel id nu exista
     */
    public void marcheazaComandaAchitata(int idComanda) throws DateInvalideException {
        Comanda comanda = gasesteComandaDupaId(idComanda);
        if (comanda == null) {
            throw new DateInvalideException("Comanda cu ID-ul " + idComanda + " nu a fost gasita.");
        }
        comanda.setStareComanda(StareComanda.ACHITATA);
        actualizeazaStareMese();
    }

    /**
     * Actualizeaza starea meselor din restaurant in functie de comenzile aflate in starea INREGISTRATA.
     */
    private void actualizeazaStareMese() {
        Set<Integer> meseOcupate = getMeseOcupate();
        restaurant.actualizeazaStareMese(meseOcupate);
    }

    /**
     * Returneaza multimea identificatorilor meselor care au cel putin o comanda neachitata.
     * @return un Set cu id-urile meselor ocupate
     */
    public Set<Integer> getMeseOcupate() {
        Set<Integer> ocupate = new HashSet<>();
        for (Comanda c : comenzi) {
            if (c.getStareComanda() == StareComanda.INREGISTRATA) {
                ocupate.add(c.getIdMasa());
            }
        }
        return ocupate;
    }

    /**
     * Cauta o comanda in lista dupa ID.
     * @param id identificatorul comenzii
     * @return comanda gasita sau null
     */
    public Comanda gasesteComandaDupaId(int id) {
        for (Comanda c : comenzi) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * Returneaza comenzile plasate intr-o anumita zi calendaristica.
     * @param data ziua cautata
     * @return lista comenzilor din acea zi
     */
    public List<Comanda> getComenziInZiua(LocalDate data) {
        if (data == null) return Collections.emptyList();
        List<Comanda> rezultat = new ArrayList<>();
        for (Comanda c : comenzi) {
            if (c.getDataOra().toLocalDate().isEqual(data)) {
                rezultat.add(c);
            }
        }
        return rezultat;
    }

    /**
     * Returneaza o lista needitabila cu toate comenzile inregistrate.
     * @return lista de comenzi
     */
    public List<Comanda> getToateComenzile() {
        return Collections.unmodifiableList(comenzi);
    }

    /**
     * Returneaza referinta directa catre lista de comenzi pentru salvare pe disc.
     * @return lista de comenzi
     */
    public List<Comanda> getComenziList() {
        return comenzi;
    }

    /**
     * Returneaza instanta restaurantului gestionat.
     * @return restaurantul singleton
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     * Returneaza lista meselor din restaurant.
     * @return lista de mese
     */
    public List<Masa> getToateMesele() {
        return restaurant.getToateMesele();
    }
}
