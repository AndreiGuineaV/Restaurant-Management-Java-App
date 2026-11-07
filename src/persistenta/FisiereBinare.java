package persistenta;

import entititati.Comanda;
import entititati.Produs;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestioneaza serializarea si deserializarea binara a produselor si comenzilor.
 */
public class FisiereBinare {

    /**
     * Constructor implicit.
     */
    public FisiereBinare() {
    }

    /**
     * Salveaza catalogul de produse intr-un fisier binar.
     * @param caleFisier calea fisierului binar
     * @param catalog map-ul de produse
     * @throws IOException daca apar erori la scriere
     */
    public void salveazaProduseBinar(String caleFisier, Map<Integer, Produs> catalog) throws IOException {
        File fisier = new File(caleFisier);
        File parinte = fisier.getParentFile();
        if (parinte != null && !parinte.exists()) {
            parinte.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fisier))) {
            oos.writeObject(catalog);
        }
    }

    /**
     * Citeste catalogul de produse dintr-un fisier binar.
     * @param caleFisier calea fisierului binar
     * @return map-ul de produse restaurat
     * @throws IOException daca apar erori de citire
     * @throws ClassNotFoundException daca structura clasei nu este recunoscuta
     */
    @SuppressWarnings("unchecked")
    public Map<Integer, Produs> citesteProduseBinar(String caleFisier) throws IOException, ClassNotFoundException {
        File fisier = new File(caleFisier);
        if (!fisier.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fisier))) {
            return (Map<Integer, Produs>) ois.readObject();
        }
    }

    /**
     * Salveaza lista de comenzi intr-un fisier binar.
     * @param caleFisier calea fisierului binar
     * @param comenzi lista de comenzi
     * @throws IOException daca apar erori la scriere
     */
    public void salveazaComenziBinar(String caleFisier, List<Comanda> comenzi) throws IOException {
        File fisier = new File(caleFisier);
        File parinte = fisier.getParentFile();
        if (parinte != null && !parinte.exists()) {
            parinte.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fisier))) {
            oos.writeObject(comenzi);
        }
    }

    /**
     * Citeste lista de comenzi dintr-un fisier binar.
     * @param caleFisier calea fisierului binar
     * @return lista de comenzi restaurata
     * @throws IOException daca apar erori de citire
     * @throws ClassNotFoundException daca structura clasei nu este recunoscuta
     */
    @SuppressWarnings("unchecked")
    public List<Comanda> citesteComenziBinar(String caleFisier) throws IOException, ClassNotFoundException {
        File fisier = new File(caleFisier);
        if (!fisier.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fisier))) {
            return (List<Comanda>) ois.readObject();
        }
    }
}
