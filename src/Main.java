import consola.MeniuConsola;
import persistenta.FisierManager;
import servicii.CatalogService;
import servicii.ComandaService;
import servicii.RaportService;
import swing.MeniuSwing;

import javax.swing.SwingUtilities;

/**
 * Punctul principal de intrare in aplicatia de gestiune a restaurantului.
 */
public class Main {

    private Main() {
    }

    /**
     * Metoda main care initializeaza serviciile si porneste interfata utilizator.
     * @param args argumente din linia de comanda (--consola pentru modul text)
     */
    public static void main(String[] args) {
        FisierManager fisierManager = new FisierManager();
        FisierManager.DateAplicatie dateInitiale = fisierManager.incarcaToateDatele();

        CatalogService catalogService = new CatalogService(dateInitiale.produse());
        ComandaService comandaService = new ComandaService(dateInitiale.comenzi(), catalogService);
        RaportService raportService = new RaportService(comandaService);

        if (args.length > 0 && (args[0].equalsIgnoreCase("--consola") || args[0].equalsIgnoreCase("-c"))) {
            MeniuConsola consola = new MeniuConsola(catalogService, comandaService, raportService, fisierManager);
            consola.porneste();
        } else {
            SwingUtilities.invokeLater(() -> {
                MeniuSwing gui = new MeniuSwing(catalogService, comandaService, raportService, fisierManager);
                gui.setVisible(true);
            });
        }
    }
}
