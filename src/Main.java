import entititati.Comanda;
import entititati.ElementComanda;
import entititati.Produs;
import persistenta.FisiereText;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {

    FisiereText persistenta = new FisiereText();
    String caleProduse = "meniu.txt";
    String caleComenzi = "istoric_comenzi.txt";
    try {
        System.out.println("=== MENIU PRODUSE INCARCAT ===");
        Map<Integer, Produs> produse = persistenta.citesteProduse(caleProduse);
        produse.values().forEach(p ->
                System.out.printf("[%d] %-30s | %-13s | %6.2f RON%n",
                        p.getId(), p.getDenumire(), p.getCategorieProdus(), p.getPret())
        );
        System.out.println("\nTotal produse citite: " + produse.size());

        System.out.println("\n=== COMENZI INREGISTRATE ===");
        List<Comanda> comenzi = persistenta.citesteComenzi(caleComenzi, produse);
        for (Comanda c : comenzi) {
            System.out.printf("%nComanda #%d | Masa #%d | Data: %s%n",
                    c.getId(), c.getIdMasa(), c.getDataOra());
            System.out.println("Produse comandate:");
            for (ElementComanda el : c.getListaElemente()) {
                System.out.printf("  - %-25s x %d buc. = %6.2f RON%n",
                        el.getProdus().getDenumire(), el.getCantitate(), el.getSubtotal());
            }
            System.out.printf("VALOARE TOTALA: %.2f RON%n", c.getValoareTotala());
            System.out.println("-------------------------------------------------");
        }
        System.out.println("Total comenzi citite: " + comenzi.size());
    } catch (Exception e) {
        System.err.println("Eroare la parsarea fisierelor: " + e.getMessage());
        e.printStackTrace();
    }

}
