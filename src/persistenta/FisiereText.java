package persistenta;

import entititati.CategorieProdus;
import entititati.Comanda;
import entititati.ElementComanda;
import entititati.Produs;
import entititati.StareComanda;
import exceptii.DateInvalideException;
import exceptii.ProdusInexistentException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Gestioneaza citirea si salvarea datelor in format text delimitat prin punct si virgula.
 */
public class FisiereText {

    private static final String SEPARATOR_CAMPURI = ";";
    private static final String SEPARATOR_ELEMENTE = ",";
    private static final String SEPARATOR_PRODUS_CANTITATE = ":";

    /**
     * Constructor implicit.
     */
    public FisiereText() {
    }

    /**
     * Citeste produsele din fisierul text specificat.
     * @param caleFisier calea catre fisierul cu produse
     * @return un map cu produsele indexate dupa ID
     * @throws IOException daca apar erori la citirea fisierului
     * @throws DateInvalideException daca formatul datelor este incorect
     */
    public Map<Integer, Produs> citesteProduse(String caleFisier) throws IOException, DateInvalideException {
        Map<Integer, Produs> catalog = new HashMap<>();
        File fisier = new File(caleFisier);

        if (!fisier.exists()) {
            return catalog;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fisier))) {
            String linie;
            int numarLinie = 0;

            while ((linie = reader.readLine()) != null) {
                numarLinie++;
                linie = linie.trim();
                if (linie.isEmpty() || linie.startsWith("#")) {
                    continue;
                }

                String[] parti = linie.split(SEPARATOR_CAMPURI);
                if (parti.length < 4) {
                    throw new DateInvalideException("Linie incompleta la linia " + numarLinie + " in " + caleFisier);
                }

                if (parti[0].trim().equalsIgnoreCase("id")) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(parti[0].trim());
                    String denumire = parti[1].trim();
                    CategorieProdus categorie = CategorieProdus.valueOf(parti[2].trim().toUpperCase());
                    double pret = Double.parseDouble(parti[3].trim());

                    if (pret <= 0) {
                        throw new DateInvalideException("Pretul trebuie sa fie pozitiv la linia " + numarLinie);
                    }

                    catalog.put(id, new Produs(id, denumire, categorie, pret));
                } catch (NumberFormatException e) {
                    throw new DateInvalideException("Format numeric invalid la linia " + numarLinie + ": " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    throw new DateInvalideException("Categorie invalida la linia " + numarLinie + ": " + parti[2]);
                }
            }
        }
        return catalog;
    }

    /**
     * Salveaza colectia de produse in fisierul text specificat.
     * @param caleFisier calea catre fisier
     * @param produse colectia de produse
     * @throws IOException daca apar erori la scriere
     */
    public void salveazaProduse(String caleFisier, Collection<Produs> produse) throws IOException {
        File fisier = new File(caleFisier);
        File parinte = fisier.getParentFile();
        if (parinte != null && !parinte.exists()) {
            parinte.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fisier))) {
            for (Produs p : produse) {
                String linie = String.join(SEPARATOR_CAMPURI,
                        String.valueOf(p.getId()),
                        p.getDenumire(),
                        p.getCategorieProdus().name(),
                        String.valueOf(p.getPret())
                );
                writer.write(linie);
                writer.newLine();
            }
        }
    }

    /**
     * Citeste comenzile dintr-un fisier text si le asociaza cu produsele existente in catalog.
     * @param caleFisier calea catre fisierul cu comenzi
     * @param catalogProduse catalogul de produse incarcat in memorie
     * @return lista de comenzi citite
     * @throws IOException daca apar erori la citire
     * @throws DateInvalideException daca formatul datelor este incorect
     * @throws ProdusInexistentException daca o comanda face referire la un produs inexistent
     */
    public List<Comanda> citesteComenzi(String caleFisier, Map<Integer, Produs> catalogProduse)
            throws IOException, DateInvalideException, ProdusInexistentException {

        List<Comanda> comenzi = new ArrayList<>();
        File fisier = new File(caleFisier);

        if (!fisier.exists()) {
            return comenzi;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fisier))) {
            String linie;
            int numarLinie = 0;

            while ((linie = reader.readLine()) != null) {
                numarLinie++;
                linie = linie.trim();
                if (linie.isEmpty() || linie.startsWith("#")) {
                    continue;
                }

                String[] parti = linie.split(SEPARATOR_CAMPURI);
                if (parti.length < 4) {
                    throw new DateInvalideException("Format invalid comanda la linia " + numarLinie);
                }

                if (parti[0].trim().equalsIgnoreCase("id")) {
                    continue;
                }

                try {
                    int idComanda = Integer.parseInt(parti[0].trim());
                    LocalDateTime dataOra = LocalDateTime.parse(parti[1].trim());
                    int idMasa = Integer.parseInt(parti[2].trim());

                    List<ElementComanda> elemente = new ArrayList<>();
                    String[] tokeniElemente = parti[3].trim().split(SEPARATOR_ELEMENTE);

                    for (String token : tokeniElemente) {
                        token = token.trim();
                        if (token.isEmpty()) continue;

                        String[] pereche = token.split(SEPARATOR_PRODUS_CANTITATE);
                        if (pereche.length != 2) {
                            throw new DateInvalideException("Format invalid element comanda: " + token);
                        }

                        int idProdus = Integer.parseInt(pereche[0].trim());
                        int cantitate = Integer.parseInt(pereche[1].trim());

                        Produs produs = catalogProduse.get(idProdus);
                        if (produs == null) {
                            throw new ProdusInexistentException("Produsul cu ID-ul " + idProdus + " nu exista in catalog!");
                        }

                        elemente.add(new ElementComanda(produs, cantitate));
                    }

                    StareComanda stare = StareComanda.INREGISTRATA;
                    if (parti.length >= 6) {
                        try {
                            stare = StareComanda.valueOf(parti[5].trim().toUpperCase());
                        } catch (IllegalArgumentException ignored) {
                            stare = StareComanda.INREGISTRATA;
                        }
                    }

                    comenzi.add(new Comanda(idComanda, dataOra, idMasa, elemente, stare));

                } catch (NumberFormatException | DateTimeParseException e) {
                    throw new DateInvalideException("Eroare de conversie date comanda la linia " + numarLinie + ": " + e.getMessage());
                }
            }
        }
        return comenzi;
    }

    /**
     * Salveaza lista de comenzi in fisierul text specificat.
     * @param caleFisier calea catre fisier
     * @param comenzi lista de comenzi de salvat
     * @throws IOException daca apar erori la scriere
     */
    public void salveazaComenzi(String caleFisier, List<Comanda> comenzi) throws IOException {
        File fisier = new File(caleFisier);
        File parinte = fisier.getParentFile();
        if (parinte != null && !parinte.exists()) {
            parinte.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fisier))) {
            for (Comanda c : comenzi) {
                StringBuilder sbElemente = new StringBuilder();
                List<ElementComanda> elemente = c.getListaElemente();
                for (int i = 0; i < elemente.size(); i++) {
                    ElementComanda elem = elemente.get(i);
                    sbElemente.append(elem.getProdus().getId())
                            .append(SEPARATOR_PRODUS_CANTITATE)
                            .append(elem.getCantitate());
                    if (i < elemente.size() - 1) {
                        sbElemente.append(SEPARATOR_ELEMENTE);
                    }
                }

                String linie = String.join(SEPARATOR_CAMPURI,
                        String.valueOf(c.getId()),
                        c.getDataOra().toString(),
                        String.valueOf(c.getIdMasa()),
                        sbElemente.toString(),
                        String.valueOf(c.getValoareTotala()),
                        c.getStareComanda().name()
                );
                writer.write(linie);
                writer.newLine();
            }
        }
    }

    /**
     * Exporta un text complet de raport intr-un fisier specificat.
     * @param caleFisier calea catre fisierul de destinatie
     * @param continutRaport textul raportului
     * @throws IOException daca apar erori la scriere
     */
    public void exportaRaportText(String caleFisier, String continutRaport) throws IOException {
        File fisier = new File(caleFisier);
        File parinte = fisier.getParentFile();
        if (parinte != null && !parinte.exists()) {
            parinte.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fisier))) {
            writer.write(continutRaport);
        }
    }
}
