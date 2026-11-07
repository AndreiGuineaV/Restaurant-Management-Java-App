package exceptii;

/**
 * Exceptie aruncata atunci cand un produs cautat nu exista in catalog.
 */
public class ProdusInexistentException extends Exception {

    /**
     * Constructor cu mesaj descriptiv.
     * @param message mesajul erorii
     */
    public ProdusInexistentException(String message) {
        super(message);
    }
}
