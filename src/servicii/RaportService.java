package servicii;

import entititati.CategorieProdus;
import entititati.Comanda;
import entititati.ElementComanda;
import entititati.Produs;
import persistenta.FisierManager;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * Serviciu pentru prelucrarea si calcularea statisticilor si rapoartelor aplicatiei.
 */
public class RaportService {

    private final ComandaService comandaService;

    /**
     * Constructor care primeste serviciul de comenzi.
     * @param comandaService serviciul de comenzi
     */
    public RaportService(ComandaService comandaService) {
        this.comandaService = comandaService;
    }

    /**
     * Calculeaza distributia comenzilor pe cele 24 de ore ale zilei utilizand un vector int[24].
     * @return tablou cu numarul de comenzi pentru fiecare ora
     */
    public int[] getDistributieComenziPeOre() {
        int[] oreVanzari = new int[24];
        List<Comanda> comenzi = comandaService.getToateComenzile();

        for (Comanda c : comenzi) {
            int ora = c.getDataOra().getHour();
            if (ora >= 0 && ora < 24) {
                oreVanzari[ora]++;
            }
        }
        return oreVanzari;
    }

    /**
     * Determina ora din zi in care s-au inregistrat cele mai multe comenzi.
     * @return ora de varf
     */
    public int getOraDeVarf() {
        int[] oreVanzari = getDistributieComenziPeOre();
        int oraVarf = 0;
        int maxComenzi = oreVanzari[0];

        for (int i = 1; i < oreVanzari.length; i++) {
            if (oreVanzari[i] > maxComenzi) {
                maxComenzi = oreVanzari[i];
                oraVarf = i;
            }
        }
        return oraVarf;
    }

    /**
     * Extrage valorile totale ale comenzilor intr-un vector de tip double[].
     * @return tablou cu totalurile comenzilor
     */
    public double[] getVectorValoriComenzi() {
        List<Comanda> comenzi = comandaService.getToateComenzile();
        double[] valori = new double[comenzi.size()];

        for (int i = 0; i < comenzi.size(); i++) {
            valori[i] = comenzi.get(i).getValoareTotala();
        }
        return valori;
    }

    /**
     * Calculeaza valoarea medie a unei comenzi pe baza vectorului de totaluri.
     * @return valoarea medie in RON
     */
    public double getValoareMedieComanda() {
        double[] valori = getVectorValoriComenzi();
        if (valori.length == 0) {
            return 0.0;
        }

        double suma = 0.0;
        for (double v : valori) {
            suma += v;
        }
        return suma / valori.length;
    }

    /**
     * Construieste o matrice bidimensionala double[7][categorii] cu vanzarile pe zile si categorii.
     * @return matricea de vanzari
     */
    public double[][] getMatriceVanzariPeZileSiCategorii() {
        CategorieProdus[] categorii = CategorieProdus.values();
        double[][] matrice = new double[7][categorii.length];

        List<Comanda> comenzi = comandaService.getToateComenzile();

        for (Comanda c : comenzi) {
            DayOfWeek zi = c.getDataOra().getDayOfWeek();
            int indexZi = zi.getValue() - 1;

            for (ElementComanda elem : c.getListaElemente()) {
                if (elem.getProdus() != null && elem.getProdus().getCategorieProdus() != null) {
                    CategorieProdus cat = elem.getProdus().getCategorieProdus();
                    int indexCategorie = cat.ordinal();
                    matrice[indexZi][indexCategorie] += elem.getSubtotal();
                }
            }
        }
        return matrice;
    }

    /**
     * Calculeaza valoarea totala a vanzarilor dintr-o anumita zi.
     * @param data ziua dorita
     * @return totalul incasarilor
     */
    public double getTotalVanzariInZiua(LocalDate data) {
        if (data == null) return 0.0;

        double total = 0.0;
        for (Comanda c : comandaService.getToateComenzile()) {
            if (c.getDataOra().toLocalDate().isEqual(data)) {
                total += c.getValoareTotala();
            }
        }
        return total;
    }

    /**
     * Returneaza comenzile realizate intr-o anumita zi.
     * @param data ziua dorita
     * @return lista de comenzi
     */
    public List<Comanda> getComenziInZiua(LocalDate data) {
        return comandaService.getComenziInZiua(data);
    }

    /**
     * Calculeaza numarul de comenzi inregistrate intr-un interval calendaristic.
     * @param deLa data de inceput
     * @param panaLa data de sfarsit
     * @return numarul de comenzi
     */
    public int getNumarComenziInPerioada(LocalDate deLa, LocalDate panaLa) {
        if (deLa == null || panaLa == null) return 0;

        int count = 0;
        for (Comanda c : comandaService.getToateComenzile()) {
            LocalDate dataComanda = c.getDataOra().toLocalDate();
            if (!dataComanda.isBefore(deLa) && !dataComanda.isAfter(panaLa)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returneaza clasamentul vanzarilor ordonat descrescator dupa numarul de bucati vandute.
     * @return lista de intrari produs - cantitate vanduta
     */
    public List<Map.Entry<Produs, Integer>> getClasamentProduseVandute() {
        Map<Produs, Integer> cantitatiPerProdus = new HashMap<>();

        for (Comanda c : comandaService.getToateComenzile()) {
            for (ElementComanda elem : c.getListaElemente()) {
                if (elem.getProdus() != null) {
                    cantitatiPerProdus.merge(elem.getProdus(), elem.getCantitate(), Integer::sum);
                }
            }
        }

        List<Map.Entry<Produs, Integer>> lista = new ArrayList<>(cantitatiPerProdus.entrySet());
        lista.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));
        return lista;
    }

    /**
     * Identifica produsul cu cel mai mare numar de bucati vandute.
     * @return produsul cel mai vandut sau null
     */
    public Produs getCelMaiVandutProdus() {
        List<Map.Entry<Produs, Integer>> clasament = getClasamentProduseVandute();
        if (clasament.isEmpty()) return null;
        return clasament.get(0).getKey();
    }

    /**
     * Calculeaza valoarea totala a vanzarilor generate exclusiv de un anumit produs.
     * @param idProdus identificatorul produsului
     * @return totalul incasarilor
     */
    public double getTotalVanzariPentruProdus(int idProdus) {
        double total = 0.0;
        for (Comanda c : comandaService.getToateComenzile()) {
            for (ElementComanda elem : c.getListaElemente()) {
                if (elem.getProdus() != null && elem.getProdus().getId() == idProdus) {
                    total += elem.getSubtotal();
                }
            }
        }
        return total;
    }

    /**
     * Genereaza textul complet si structurat al raportului general al restaurantului.
     * @return textul raportului
     */
    public String genereazaRaportComplet() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("                  RAPORT STATISTIC RESTAURANT                   \n");
        sb.append("=================================================================\n\n");

        double[] totaluri = getVectorValoriComenzi();
        sb.append(String.format("1. REZUMAT COMENZI:\n"));
        sb.append(String.format("   - Numar total comenzi inregistrate: %d\n", totaluri.length));
        sb.append(String.format("   - Valoare medie per comanda:       %.2f RON\n", getValoareMedieComanda()));

        int oraVarf = getOraDeVarf();
        int[] ore = getDistributieComenziPeOre();
        sb.append(String.format("   - Ora de varf a vanzarilor:        %02d:00 (%d comenzi)\n\n", oraVarf, ore[oraVarf]));

        sb.append("2. TOP PRODUSE VANDUTE (Dupa cantitate):\n");
        List<Map.Entry<Produs, Integer>> top = getClasamentProduseVandute();
        if (top.isEmpty()) {
            sb.append("   (Nu exista vanzari inregistrate)\n");
        } else {
            int poz = 1;
            for (Map.Entry<Produs, Integer> entry : top) {
                Produs p = entry.getKey();
                int buc = entry.getValue();
                double incasari = getTotalVanzariPentruProdus(p.getId());
                sb.append(String.format("   %2d. %-28s : %3d buc. | Incasari: %7.2f RON\n",
                        poz++, p.getDenumire(), buc, incasari));
            }
        }
        sb.append("\n");

        sb.append("3. MATRICE VANZARI PE ZILELE SAPTAMANII SI CATEGORII (RON):\n");
        sb.append(String.format("%-12s", "Zi / Cat."));
        for (CategorieProdus cat : CategorieProdus.values()) {
            sb.append(String.format("%14s", cat.name()));
        }
        sb.append(String.format("%14s\n", "TOTAL ZI"));
        sb.append("-".repeat(70)).append("\n");

        String[] zile = {"Luni", "Marti", "Miercuri", "Joi", "Vineri", "Sambata", "Duminica"};
        double[][] matrice = getMatriceVanzariPeZileSiCategorii();

        for (int i = 0; i < 7; i++) {
            sb.append(String.format("%-12s", zile[i]));
            double totalZi = 0.0;
            for (int j = 0; j < matrice[i].length; j++) {
                sb.append(String.format("%14.2f", matrice[i][j]));
                totalZi += matrice[i][j];
            }
            sb.append(String.format("%14.2f\n", totalZi));
        }
        sb.append("-".repeat(70)).append("\n\n");

        sb.append("=================================================================\n");
        return sb.toString();
    }

    /**
     * Salveaza raportul statistic pe disc intr-un fisier text.
     * @param caleFisier calea fisierului de destinatie
     * @param fisierManager managerul de fisiere
     * @throws IOException daca apar erori la scriere
     */
    public void salveazaRaportInFisier(String caleFisier, FisierManager fisierManager) throws IOException {
        String raport = genereazaRaportComplet();
        fisierManager.salveazaRaportText(caleFisier, raport);
    }
}
