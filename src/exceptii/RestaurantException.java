package exceptii;

/**
 * Exceptie generala pentru erorile din cadrul aplicatiei restaurantului.
 */
public class RestaurantException extends RuntimeException {

    /**
     * Constructor cu mesaj descriptiv.
     * @param message mesajul erorii
     */
    public RestaurantException(String message) {
        super(message);
    }
}
