package consola;

import entititati.CategorieProdus;
import entititati.Comanda;
import entititati.ElementComanda;
import entititati.Masa;
import entititati.Produs;
import exceptii.DateInvalideException;
import exceptii.ProdusInexistentException;
import persistenta.FisierManager;
import servicii.CatalogService;
import servicii.ComandaService;
import servicii.RaportService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Interfata in mod consola ce permite interactiunea cu utilizatorul prin intermediul tastaturii.
 */
public class MeniuConsola {

    private final CatalogService catalogService;
    private final ComandaService comandaService;
    private final RaportService raportService;
    private final FisierManager fisierManager;
    private final Scanner scanner;

    /**
     * Constructor pentru meniul in consola.
     * @param catalogService serviciul pentru catalogul de produse
     * @param comandaService serviciul pentru comenzi
     * @param raportService serviciul pentru generarea rapoartelor
     * @param fisierManager managerul de fisiere pentru salvare
     */
    public MeniuConsola(CatalogService catalogService,
                        ComandaService comandaService,
                        RaportService raportService,
                        FisierManager fisierManager) {
        this.catalogService = catalogService;
        this.comandaService = comandaService;
        this.raportService = raportService;
        this.fisierManager = fisierManager;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Porneste bucla principala de interactiune din consola.
     */
    public void porneste() {
        boolean ruleaza = true;
        while (ruleaza) {
            afiseazaMeniu();
            System.out.print("Alegeti o optiune: ");
            String optiune = scanner.nextLine().trim();

            switch (optiune) {
                case "1" -> afiseazaCatalogProduse();
                case "2" -> adaugaProdusNou();
                case "3" -> actualizeazaPretProdus();
                case "4" -> plaseazaComandaNoua();
                case "5" -> afiseazaIstoricComenzi();
                case "6" -> marcheazaComandaAchitata();
                case "7" -> afiseazaStareMeseRestaurant();
                case "8" -> afiseazaRapoarteStatistice();
                case "9" -> exportaRaportInFisier();
                case "10" -> {
                    salveazaSiIesi();
                    ruleaza = false;
                }
                case "0" -> {
                    System.out.println("Iesire fara salvare.");
                    ruleaza = false;
                }
                default -> System.out.println("Optiune invalida! Incercati din nou.\n");
            }
        }
    }

    private void afiseazaMeniu() {
        System.out.println("\n==============================================");
        System.out.println("     SISTEM GESTIUNE RESTAURANT (CONSOLA)     ");
        System.out.println("   " + comandaService.getRestaurant().toString());
        System.out.println("==============================================");
        System.out.println("1.  Afisare meniu produse (Catalog)");
        System.out.println("2.  Adaugare produs nou in catalog");
        System.out.println("3.  Modificare pret produs");
        System.out.println("4.  Plasare comanda noua");
        System.out.println("5.  Vizualizare istoric comenzi");
        System.out.println("6.  Marcare comanda ca achitata");
        System.out.println("7.  Vizualizare situatie mese restaurant");
        System.out.println("8.  Vizualizare rapoarte si statistici");
        System.out.println("9.  Export raport statistic in fisier text");
        System.out.println("10. Salvare modificari si Iesire");
        System.out.println("0.  Iesire fara salvare");
        System.out.println("==============================================");
    }

    private void afiseazaCatalogProduse() {
        System.out.println("\n--- CATALOG PRODUSE DISPONIBILE ---");
        List<Produs> produse = catalogService.getToateProdusele();
        if (produse.isEmpty()) {
            System.out.println("Catalogul este gol.");
            return;
        }
        for (Produs p : produse) {
            System.out.printf("[%d] %-30s | %-14s | %7.2f RON%n",
                    p.getId(), p.getDenumire(), p.getCategorieProdus(), p.getPret());
        }
    }

    private void adaugaProdusNou() {
        System.out.println("\n--- ADAUGARE PRODUS NOU ---");
        try {
            System.out.print("Introduceti ID produs (numar intreg): ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Introduceti denumire produs: ");
            String denumire = scanner.nextLine().trim();

            System.out.println("Categorii posibile: " + Arrays.toString(CategorieProdus.values()));
            System.out.print("Introduceti categoria: ");
            CategorieProdus cat = CategorieProdus.valueOf(scanner.nextLine().trim().toUpperCase());

            System.out.print("Introduceti pretul (ex: 25.5): ");
            double pret = Double.parseDouble(scanner.nextLine().trim());

            catalogService.adaugaProdus(id, denumire, cat, pret);
            System.out.println(">> Produs adaugat cu succes in catalog!");

        } catch (NumberFormatException e) {
            System.err.println("Eroare de validare: ID-ul si pretul trebuie sa fie valori numerice valide!");
        } catch (IllegalArgumentException e) {
            System.err.println("Eroare de validare: Categorie necunoscuta!");
        } catch (DateInvalideException e) {
            System.err.println("Eroare de validare date: " + e.getMessage());
        }
    }

    private void actualizeazaPretProdus() {
        System.out.println("\n--- ACTUALIZARE PRET PRODUS ---");
        try {
            System.out.print("Introduceti ID-ul produsului: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Introduceti noul pret: ");
            double pretNou = Double.parseDouble(scanner.nextLine().trim());

            catalogService.actualizeazaPret(id, pretNou);
            System.out.println(">> Pretul a fost actualizat cu succes!");

        } catch (NumberFormatException e) {
            System.err.println("Eroare de validare: Valori numerice invalide!");
        } catch (ProdusInexistentException | DateInvalideException e) {
            System.err.println("Eroare: " + e.getMessage());
        }
    }

    private void plaseazaComandaNoua() {
        System.out.println("\n--- PLASARE COMANDA NOUA ---");
        try {
            System.out.println("Mese disponibile in restaurant: 1.." + comandaService.getRestaurant().getNrMese());
            System.out.print("Introduceti numarul mesei: ");
            int idMasa = Integer.parseInt(scanner.nextLine().trim());

            Map<Integer, Integer> cos = new LinkedHashMap<>();
            while (true) {
                System.out.print("Introduceti ID produs pentru adaugat in comanda (sau 0 pentru finalizare cos): ");
                int idProdus = Integer.parseInt(scanner.nextLine().trim());
                if (idProdus == 0) break;

                if (!catalogService.existaProdus(idProdus)) {
                    System.err.println("Produsul cu ID-ul " + idProdus + " nu exista!");
                    continue;
                }

                System.out.print("Introduceti cantitatea: ");
                int cantitate = Integer.parseInt(scanner.nextLine().trim());
                if (cantitate <= 0) {
                    System.err.println("Cantitatea trebuie sa fie > 0!");
                    continue;
                }

                cos.merge(idProdus, cantitate, Integer::sum);
                System.out.println(">> Adaugat in cos: ID " + idProdus + " x " + cantitate);
            }

            Comanda comanda = comandaService.inregistreazaComanda(idMasa, cos);
            System.out.printf(">> Comanda #%d plasata cu succes la Masa #%d! Total de plata: %.2f RON%n",
                    comanda.getId(), comanda.getIdMasa(), comanda.getValoareTotala());

        } catch (NumberFormatException e) {
            System.err.println("Eroare de validare: Datele introduse trebuie sa fie numerice!");
        } catch (DateInvalideException | ProdusInexistentException e) {
            System.err.println("Eroare la plasarea comenzii: " + e.getMessage());
        }
    }

    private void afiseazaIstoricComenzi() {
        System.out.println("\n--- ISTORIC COMENZI ---");
        List<Comanda> comenzi = comandaService.getToateComenzile();
        if (comenzi.isEmpty()) {
            System.out.println("Nu exista comenzi inregistrate.");
            return;
        }

        for (Comanda c : comenzi) {
            System.out.printf("Comanda #%-3d | Masa #%-2d | %s | Stare: %-12s | Total: %7.2f RON%n",
                    c.getId(), c.getIdMasa(), c.getDataOra().toString().replace("T", " "),
                    c.getStareComanda(), c.getValoareTotala());
            System.out.println("  Produse comandate:");
            for (ElementComanda elem : c.getListaElemente()) {
                System.out.printf("    - %-25s x %d buc. (%6.2f RON)%n",
                        elem.getProdus().getDenumire(), elem.getCantitate(), elem.getSubtotal());
            }
            System.out.println("-".repeat(60));
        }
    }

    private void marcheazaComandaAchitata() {
        System.out.println("\n--- ACHITARE COMANDA ---");
        try {
            System.out.print("Introduceti ID-ul comenzii de marcat ca achitata: ");
            int idComanda = Integer.parseInt(scanner.nextLine().trim());

            comandaService.marcheazaComandaAchitata(idComanda);
            System.out.println(">> Comanda #" + idComanda + " a fost marcata ca ACHITATA!");

        } catch (NumberFormatException e) {
            System.err.println("Eroare: ID-ul comenzii trebuie sa fie numeric!");
        } catch (DateInvalideException e) {
            System.err.println("Eroare: " + e.getMessage());
        }
    }

    private void afiseazaStareMeseRestaurant() {
        System.out.println("\n--- SITUATIE MESE RESTAURANT ---");
        for (Masa m : comandaService.getToateMesele()) {
            System.out.printf("Masa #%-2d | Capacitate: %d locuri | Stare: %s%n",
                    m.getId(), m.getNrLocuri(), m.isEsteOcupata() ? "OCUPATA (comanda activa)" : "LIBERA");
        }
    }

    private void afiseazaRapoarteStatistice() {
        System.out.println("\n" + raportService.genereazaRaportComplet());
    }

    private void exportaRaportInFisier() {
        String caleFisier = "fisiere_txt" + File.separator + "raport_statistic.txt";
        try {
            raportService.salveazaRaportInFisier(caleFisier, fisierManager);
            System.out.println(">> Raportul a fost exportat cu succes in fisierul: " + caleFisier);
        } catch (IOException e) {
            System.err.println("Eroare la exportul raportului: " + e.getMessage());
        }
    }

    private void salveazaSiIesi() {
        try {
            fisierManager.salveazaToateDatele(
                    catalogService.getCatalogMap(),
                    comandaService.getComenziList()
            );
            System.out.println(">> Toate datele au fost salvate cu succes in fisiere!");
        } catch (IOException e) {
            System.err.println("Eroare la salvarea datelor: " + e.getMessage());
        }
    }
}
