package exceptii;

/**
 * Exceptie aruncata atunci cand datele introduse sau citite sunt invalide.
 */
public class DateInvalideException extends RuntimeException {

    /**
     * Constructor cu mesaj descriptiv.
     * @param message mesajul erorii
     */
    public DateInvalideException(String message) {
        super(message);
    }
}
