package servicii;

import entititati.CategorieProdus;
import entititati.Produs;
import exceptii.DateInvalideException;
import exceptii.ProdusInexistentException;

import java.util.*;

/**
 * Serviciu pentru gestionarea produselor din catalogul restaurantului.
 */
public class CatalogService {

    private final Map<Integer, Produs> catalog;

    /**
     * Constructor care populeaza catalogul cu produsele initiale.
     * @param produseInitiale map-ul initial de produse
     */
    public CatalogService(Map<Integer, Produs> produseInitiale) {
        this.catalog = (produseInitiale != null) ? new HashMap<>(produseInitiale) : new HashMap<>();
    }

    /**
     * Adauga un produs nou in catalog dupa validarea datelor.
     * @param id identificatorul unic al produsului
     * @param denumire denumirea produsului
     * @param categorie categoria produsului
     * @param pret pretul unitar
     * @throws DateInvalideException daca oricare dintre datele furnizate este incorecta
     */
    public void adaugaProdus(int id, String denumire, CategorieProdus categorie, double pret) throws DateInvalideException {
        if (id <= 0) {
            throw new DateInvalideException("ID-ul produsului trebuie sa fie pozitiv.");
        }
        if (catalog.containsKey(id)) {
            throw new DateInvalideException("Exista deja un produs cu ID-ul " + id);
        }
        if (denumire == null || denumire.trim().isEmpty()) {
            throw new DateInvalideException("Denumirea produsului nu poate fi vida.");
        }
        if (categorie == null) {
            throw new DateInvalideException("Categoria produsului este obligatorie.");
        }
        if (pret <= 0) {
            throw new DateInvalideException("Pretul produsului trebuie sa fie strict pozitiv.");
        }

        Produs produsNou = new Produs(id, denumire.trim(), categorie, pret);
        catalog.put(id, produsNou);
    }

    /**
     * Cauta un produs in catalog dupa ID.
     * @param id identificatorul produsului cautat
     * @return produsul gasit
     * @throws ProdusInexistentException daca produsul nu se afla in catalog
     */
    public Produs gasesteProdusDupaId(int id) throws ProdusInexistentException {
        Produs produs = catalog.get(id);
        if (produs == null) {
            throw new ProdusInexistentException("Produsul cu ID-ul " + id + " nu a fost gasit in catalog.");
        }
        return produs;
    }

    /**
     * Actualizeaza pretul unui produs existent.
     * @param id identificatorul produsului
     * @param pretNou noua valoare a pretului
     * @throws ProdusInexistentException daca produsul nu exista
     * @throws DateInvalideException daca pretul nou nu este strict pozitiv
     */
    public void actualizeazaPret(int id, double pretNou) throws ProdusInexistentException, DateInvalideException {
        if (pretNou <= 0) {
            throw new DateInvalideException("Noul pret trebuie sa fie strict pozitiv.");
        }
        Produs produs = gasesteProdusDupaId(id);
        produs.setPret(pretNou);
    }

    /**
     * Sterge un produs din catalog.
     * @param id identificatorul produsului de sters
     * @throws ProdusInexistentException daca produsul nu se afla in catalog
     */
    public void stergeProdus(int id) throws ProdusInexistentException {
        if (!catalog.containsKey(id)) {
            throw new ProdusInexistentException("Produsul cu ID-ul " + id + " nu a fost gasit in catalog.");
        }
        catalog.remove(id);
    }

    /**
     * Returneaza o lista needitabila cu toate produsele din catalog, ordonata dupa ID.
     * @return lista ordonata de produse
     */
    public List<Produs> getToateProdusele() {
        List<Produs> lista = new ArrayList<>(catalog.values());
        lista.sort(Comparator.comparingInt(Produs::getId));
        return Collections.unmodifiableList(lista);
    }

    /**
     * Returneaza referinta catre map-ul intern al catalogului.
     * @return map-ul de produse
     */
    public Map<Integer, Produs> getCatalogMap() {
        return catalog;
    }

    /**
     * Verifica daca un produs cu ID-ul specificat exista in catalog.
     * @param id identificatorul produsului
     * @return true daca produsul exista, false altfel
     */
    public boolean existaProdus(int id) {
        return catalog.containsKey(id);
    }
}
