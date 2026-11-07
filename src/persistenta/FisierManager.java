package persistenta;

import entititati.Comanda;
import entititati.Produs;
import exceptii.DateInvalideException;
import exceptii.ProdusInexistentException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestioneaza incarcarea si salvarea datelor aplicatiei atat in format text, cat si binar.
 */
public class FisierManager {

    private static final String FISIER_PRODUSE_TXT = "fisiere_txt" + File.separator + "kimu_menu.txt";
    private static final String FISIER_COMENZI_TXT = "fisiere_txt" + File.separator + "istoric_comenzi.txt";

    private static final String FISIER_PRODUSE_BIN = "fisiere_binare" + File.separator + "backup_meniu.dat";
    private static final String FISIER_COMENZI_BIN = "fisiere_binare" + File.separator + "backup_istoric_comenzi.dat";

    private final FisiereText persistentaText;
    private final FisiereBinare persistentaBinara;

    /**
     * Constructor care initializeaza obiectele de gestiune a fisierelor.
     */
    public FisierManager() {
        this.persistentaText = new FisiereText();
        this.persistentaBinara = new FisiereBinare();
    }

    /**
     * Incarca produsele si comenzile la pornirea aplicatiei.
     * Incearca fisierele text, iar daca acestea nu functioneaza, apeleaza fisierele binare.
     * @return un container cu produsele si comenzile incarcate
     */
    public DateAplicatie incarcaToateDatele() {
        Map<Integer, Produs> produse = new HashMap<>();
        List<Comanda> comenzi = new ArrayList<>();

        boolean produseIncarcate = false;

        File fProduseTxt = new File(FISIER_PRODUSE_TXT);
        if (fProduseTxt.exists()) {
            try {
                produse = persistentaText.citesteProduse(FISIER_PRODUSE_TXT);
                produseIncarcate = true;
                System.out.println("[FisierManager] Produse incarcate cu succes din " + FISIER_PRODUSE_TXT);
            } catch (IOException | DateInvalideException e) {
                System.err.println("[FisierManager] Eroare la citirea produselor din text: " + e.getMessage());
            }
        }

        if (produseIncarcate) {
            File fComenziTxt = new File(FISIER_COMENZI_TXT);
            if (fComenziTxt.exists()) {
                try {
                    comenzi = persistentaText.citesteComenzi(FISIER_COMENZI_TXT, produse);
                    System.out.println("[FisierManager] Comenzi incarcate cu succes din " + FISIER_COMENZI_TXT);
                } catch (IOException | DateInvalideException | ProdusInexistentException e) {
                    System.err.println("[FisierManager] Eroare la citirea comenzilor din text: " + e.getMessage());
                }
            }
        }

        if (!produseIncarcate || produse.isEmpty()) {
            File fProduseBin = new File(FISIER_PRODUSE_BIN);
            File fComenziBin = new File(FISIER_COMENZI_BIN);

            if (fProduseBin.exists()) {
                try {
                    produse = persistentaBinara.citesteProduseBinar(FISIER_PRODUSE_BIN);
                    if (fComenziBin.exists()) {
                        comenzi = persistentaBinara.citesteComenziBinar(FISIER_COMENZI_BIN);
                    }
                    System.out.println("[FisierManager] Date restaurate cu succes din backup-ul binar.");
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("[FisierManager] Backup-ul binar nu a putut fi citit: " + e.getMessage());
                }
            } else {
                System.out.println("[FisierManager] Nu s-au gasit fisiere existente. Aplicatia porneste cu date vide.");
            }
        }

        return new DateAplicatie(produse, comenzi);
    }

    /**
     * Salveaza produsele si comenzile simultan in fisierele text si in copiile de siguranta binare.
     * @param produse catalogul de produse
     * @param comenzi lista de comenzi
     * @throws IOException daca apar erori la scrierea pe disc
     */
    public void salveazaToateDatele(Map<Integer, Produs> produse, List<Comanda> comenzi) throws IOException {
        persistentaText.salveazaProduse(FISIER_PRODUSE_TXT, produse.values());
        persistentaText.salveazaComenzi(FISIER_COMENZI_TXT, comenzi);

        persistentaBinara.salveazaProduseBinar(FISIER_PRODUSE_BIN, produse);
        persistentaBinara.salveazaComenziBinar(FISIER_COMENZI_BIN, comenzi);

        System.out.println("[FisierManager] Toate datele au fost salvate (text + binar).");
    }

    /**
     * Salveaza continutul unui raport statistic intr-un fisier text.
     * @param caleFisier calea fisierului de destinatie
     * @param continutRaport textul raportului
     * @throws IOException daca apar erori la scriere
     */
    public void salveazaRaportText(String caleFisier, String continutRaport) throws IOException {
        persistentaText.exportaRaportText(caleFisier, continutRaport);
    }

    /**
     * Structura de date pentru transportul colectiilor incarcate la pornire.
     * @param produse map-ul de produse
     * @param comenzi lista de comenzi
     */
    public record DateAplicatie(Map<Integer, Produs> produse, List<Comanda> comenzi) {}
}
