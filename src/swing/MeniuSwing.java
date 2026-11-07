package swing;

import entititati.CategorieProdus;
import entititati.Comanda;
import entititati.ElementComanda;
import entititati.Masa;
import entititati.Produs;
import entititati.StareComanda;
import exceptii.DateInvalideException;
import exceptii.ProdusInexistentException;
import persistenta.FisierManager;
import servicii.CatalogService;
import servicii.ComandaService;
import servicii.RaportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

/**
 * Fereastra principala cu interfata grafica Swing a aplicatiei de restaurant.
 */
public class MeniuSwing extends JFrame {

    private final CatalogService catalogService;
    private final ComandaService comandaService;
    private final RaportService raportService;
    private final FisierManager fisierManager;

    private DefaultTableModel catalogTableModel;
    private DefaultTableModel istoricTableModel;
    private JComboBox<Produs> cmbSelectieProduseCos;
    private JComboBox<Masa> cmbSelectieMasa;
    private JTable tabelIstoric;
    private DefaultListModel<String> detaliiComandaListModel;

    /**
     * Constructor pentru interfata grafica Swing.
     * @param catalogService serviciul pentru catalogul de produse
     * @param comandaService serviciul pentru comenzi
     * @param raportService serviciul pentru generarea rapoartelor
     * @param fisierManager managerul de fisiere pentru salvare
     */
    public MeniuSwing(CatalogService catalogService,
                      ComandaService comandaService,
                      RaportService raportService,
                      FisierManager fisierManager) {
        super("Sistem Gestiune Restaurant - " + comandaService.getRestaurant().getDenumire());
        this.catalogService = catalogService;
        this.comandaService = comandaService;
        this.raportService = raportService;
        this.fisierManager = fisierManager;

        initUI();
    }

    private void initUI() {
        setSize(960, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                executaSalvareSiInchidere();
            }
        });

        JMenuBar menuBar = new JMenuBar();
        JMenu menuFisier = new JMenu("Fisier");
        JMenuItem itemSalvare = new JMenuItem("Salveaza modificarile acum");
        itemSalvare.addActionListener(e -> salveazaDateManual());
        JMenuItem itemIesire = new JMenuItem("Iesire");
        itemIesire.addActionListener(e -> executaSalvareSiInchidere());
        menuFisier.add(itemSalvare);
        menuFisier.addSeparator();
        menuFisier.add(itemIesire);
        menuBar.add(menuFisier);
        setJMenuBar(menuBar);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Catalog Produse", creeazaPanouCatalog());
        tabbedPane.addTab("Plasare Comanda", creeazaPanouPlasareComanda());
        tabbedPane.addTab("Istoric Comenzi & Prelucrare", creeazaPanouIstoric());
        tabbedPane.addTab("Rapoarte Statistice", creeazaPanouRapoarte());

        add(tabbedPane);
    }

    private JPanel creeazaPanouCatalog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] coloane = {"ID", "Denumire", "Categorie", "Pret (RON)"};
        catalogTableModel = new DefaultTableModel(coloane, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(catalogTableModel);
        incarcaDateInTabelCatalog();
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnModificaPret = new JButton("Modifica Pret Produs Selectat");
        JButton btnStergeProdus = new JButton("Sterge Produs Selectat");

        btnModificaPret.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selectati un produs din tabel pentru a-i modifica pretul!", "Atentie", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idProdus = (int) catalogTableModel.getValueAt(selectedRow, 0);
            String input = JOptionPane.showInputDialog(this, "Introduceti noul pret (RON):");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    double pretNou = Double.parseDouble(input.trim());
                    catalogService.actualizeazaPret(idProdus, pretNou);
                    incarcaDateInTabelCatalog();
                    actualizeazaComboProduseCos();
                    JOptionPane.showMessageDialog(this, "Pretul a fost actualizat cu succes!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Pretul trebuie sa fie o valoare numerica valida!", "Eroare", JOptionPane.ERROR_MESSAGE);
                } catch (DateInvalideException | ProdusInexistentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Eroare", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnStergeProdus.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selectati un produs din tabel pentru a-l sterge!", "Atentie", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idProdus = (int) catalogTableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Sigur doriti sa stergeti produsul cu ID " + idProdus + "?", "Confirmare", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    catalogService.stergeProdus(idProdus);
                    incarcaDateInTabelCatalog();
                    actualizeazaComboProduseCos();
                    JOptionPane.showMessageDialog(this, "Produsul a fost sters!");
                } catch (ProdusInexistentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionsPanel.add(btnModificaPret);
        actionsPanel.add(btnStergeProdus);

        JPanel formPanel = new JPanel(new GridLayout(2, 5, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Adauga Produs Nou"));

        JTextField txtId = new JTextField();
        JTextField txtDenumire = new JTextField();
        JComboBox<CategorieProdus> cmbCategorie = new JComboBox<>(CategorieProdus.values());
        JTextField txtPret = new JTextField();
        JButton btnAdauga = new JButton("Adauga Produs");

        formPanel.add(new JLabel("ID:"));
        formPanel.add(new JLabel("Denumire:"));
        formPanel.add(new JLabel("Categorie:"));
        formPanel.add(new JLabel("Pret (RON):"));
        formPanel.add(new JLabel(""));

        formPanel.add(txtId);
        formPanel.add(txtDenumire);
        formPanel.add(cmbCategorie);
        formPanel.add(txtPret);
        formPanel.add(btnAdauga);

        btnAdauga.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String denumire = txtDenumire.getText().trim();
                CategorieProdus cat = (CategorieProdus) cmbCategorie.getSelectedItem();
                double pret = Double.parseDouble(txtPret.getText().trim());

                catalogService.adaugaProdus(id, denumire, cat, pret);
                incarcaDateInTabelCatalog();
                actualizeazaComboProduseCos();

                txtId.setText("");
                txtDenumire.setText("");
                txtPret.setText("");
                JOptionPane.showMessageDialog(this, "Produsul a fost adaugat in catalog!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID-ul si pretul trebuie sa fie valori numerice valide!", "Eroare Input", JOptionPane.ERROR_MESSAGE);
            } catch (DateInvalideException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Eroare Validare", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(actionsPanel, BorderLayout.NORTH);
        southPanel.add(formPanel, BorderLayout.SOUTH);

        panel.add(southPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void incarcaDateInTabelCatalog() {
        catalogTableModel.setRowCount(0);
        for (Produs p : catalogService.getToateProdusele()) {
            catalogTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getDenumire(),
                    p.getCategorieProdus(),
                    String.format("%.2f", p.getPret())
            });
        }
    }

    private JPanel creeazaPanouPlasareComanda() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        cmbSelectieMasa = new JComboBox<>();
        actualizeazaComboMese();

        topPanel.add(new JLabel("Selecteaza Masa:"));
        topPanel.add(cmbSelectieMasa);

        Map<Integer, Integer> cosProduse = new LinkedHashMap<>();
        DefaultListModel<String> listModelCos = new DefaultListModel<>();
        JList<String> listCos = new JList<>(listModelCos);

        JLabel lblTotalCos = new JLabel("Total curent cos: 0.00 RON");
        lblTotalCos.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel panelStangaCos = new JPanel(new BorderLayout(5, 5));
        panelStangaCos.setBorder(BorderFactory.createTitledBorder("Cos Produse"));
        panelStangaCos.add(new JScrollPane(listCos), BorderLayout.CENTER);

        JPanel panelButoaneCos = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGolesteCos = new JButton("Goleste Cosul");
        btnGolesteCos.addActionListener(e -> {
            cosProduse.clear();
            listModelCos.clear();
            lblTotalCos.setText("Total curent cos: 0.00 RON");
        });
        panelButoaneCos.add(lblTotalCos);
        panelButoaneCos.add(btnGolesteCos);
        panelStangaCos.add(panelButoaneCos, BorderLayout.SOUTH);

        middlePanel.add(panelStangaCos);

        JPanel addPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        addPanel.setBorder(BorderFactory.createTitledBorder("Adauga Produs in Comanda"));

        cmbSelectieProduseCos = new JComboBox<>();
        actualizeazaComboProduseCos();

        JTextField txtCantitate = new JTextField("1");
        JButton btnAdaugaInCos = new JButton("Adauga in Cos");
        JButton btnFinalizeazaComanda = new JButton("Plaseaza Comanda");

        addPanel.add(new JLabel("Alege Produs:"));
        addPanel.add(cmbSelectieProduseCos);
        addPanel.add(new JLabel("Cantitate:"));
        addPanel.add(txtCantitate);
        addPanel.add(new JLabel(""));
        addPanel.add(btnAdaugaInCos);
        addPanel.add(new JLabel(""));
        addPanel.add(new JLabel(""));
        addPanel.add(new JLabel(""));
        addPanel.add(btnFinalizeazaComanda);

        middlePanel.add(addPanel);

        btnAdaugaInCos.addActionListener(e -> {
            try {
                Produs p = (Produs) cmbSelectieProduseCos.getSelectedItem();
                if (p == null) {
                    JOptionPane.showMessageDialog(this, "Selectati un produs!", "Atentie", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int cantitate = Integer.parseInt(txtCantitate.getText().trim());
                if (cantitate <= 0) {
                    JOptionPane.showMessageDialog(this, "Cantitatea trebuie sa fie strict pozitiva!", "Eroare", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                cosProduse.merge(p.getId(), cantitate, Integer::sum);

                listModelCos.clear();
                double total = 0.0;
                for (Map.Entry<Integer, Integer> entry : cosProduse.entrySet()) {
                    Produs prod = catalogService.gasesteProdusDupaId(entry.getKey());
                    double subtotal = prod.getPret() * entry.getValue();
                    total += subtotal;
                    listModelCos.addElement(prod.getDenumire() + " x " + entry.getValue() + " buc. (" + String.format("%.2f", subtotal) + " RON)");
                }
                lblTotalCos.setText(String.format("Total curent cos: %.2f RON", total));
                txtCantitate.setText("1");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cantitatea trebuie sa fie un numar intreg!", "Eroare", JOptionPane.ERROR_MESSAGE);
            } catch (ProdusInexistentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnFinalizeazaComanda.addActionListener(e -> {
            try {
                Masa masaSelectata = (Masa) cmbSelectieMasa.getSelectedItem();
                if (masaSelectata == null) {
                    JOptionPane.showMessageDialog(this, "Selectati o masa din lista!", "Atentie", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int nrMasa = masaSelectata.getId();

                if (cosProduse.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Cosul este gol! Adaugati cel putin un produs.", "Atentie", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Comanda c = comandaService.inregistreazaComanda(nrMasa, cosProduse);
                JOptionPane.showMessageDialog(this, "Comanda #" + c.getId() + " a fost plasata cu succes la Masa " + nrMasa + "!\nTotal de plata: " + String.format("%.2f", c.getValoareTotala()) + " RON");

                cosProduse.clear();
                listModelCos.clear();
                lblTotalCos.setText("Total curent cos: 0.00 RON");
                actualizeazaComboMese();
                incarcaDateInTabelIstoric();

            } catch (DateInvalideException | ProdusInexistentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Eroare Plasare", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(middlePanel, BorderLayout.CENTER);
        return panel;
    }

    private void actualizeazaComboMese() {
        if (cmbSelectieMasa == null) return;
        cmbSelectieMasa.removeAllItems();
        for (Masa m : comandaService.getToateMesele()) {
            cmbSelectieMasa.addItem(m);
        }
    }

    private void actualizeazaComboProduseCos() {
        if (cmbSelectieProduseCos == null) return;
        cmbSelectieProduseCos.removeAllItems();
        for (Produs p : catalogService.getToateProdusele()) {
            cmbSelectieProduseCos.addItem(p);
        }
    }

    private JPanel creeazaPanouIstoric() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JTextField txtFiltruData = new JTextField(10);
        JButton btnFiltreazaData = new JButton("Filtreaza dupa Data (YYYY-MM-DD)");
        JButton btnReseteazaFiltru = new JButton("Arata Toate Comenzile");

        filterPanel.add(new JLabel("Data:"));
        filterPanel.add(txtFiltruData);
        filterPanel.add(btnFiltreazaData);
        filterPanel.add(btnReseteazaFiltru);

        String[] coloane = {"ID Comanda", "Masa", "Data & Ora", "Stare Comanda", "Numar Produse", "Total (RON)"};
        istoricTableModel = new DefaultTableModel(coloane, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelIstoric = new JTable(istoricTableModel);
        incarcaDateInTabelIstoric();

        detaliiComandaListModel = new DefaultListModel<>();
        JList<String> listDetalii = new JList<>(detaliiComandaListModel);
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Produse in Comanda Selectata"));
        detailsPanel.add(new JScrollPane(listDetalii), BorderLayout.CENTER);
        detailsPanel.setPreferredSize(new Dimension(300, 150));

        tabelIstoric.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                afiseazaDetaliiComandaSelectata();
            }
        });

        JButton btnMarcheazaAchitata = new JButton("Marcheaza Comanda Selectata ca ACHITATA");
        btnMarcheazaAchitata.addActionListener(e -> {
            int selectedRow = tabelIstoric.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selectati o comanda din tabel!", "Atentie", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idComanda = (int) istoricTableModel.getValueAt(selectedRow, 0);
            try {
                comandaService.marcheazaComandaAchitata(idComanda);
                incarcaDateInTabelIstoric();
                afiseazaDetaliiComandaSelectata();
                actualizeazaComboMese();
                JOptionPane.showMessageDialog(this, "Comanda #" + idComanda + " a fost marcata ca ACHITATA!\nStarea mesei a fost actualizata.");
            } catch (DateInvalideException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        detailsPanel.add(btnMarcheazaAchitata, BorderLayout.SOUTH);

        btnFiltreazaData.addActionListener(e -> {
            try {
                String text = txtFiltruData.getText().trim();
                LocalDate data = LocalDate.parse(text);
                List<Comanda> filtrate = comandaService.getComenziInZiua(data);
                populeazaTabelIstoric(filtrate);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format data invalid! Folositi formatul YYYY-MM-DD (ex: 2026-11-03).", "Eroare Format", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReseteazaFiltru.addActionListener(e -> {
            txtFiltruData.setText("");
            incarcaDateInTabelIstoric();
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tabelIstoric), detailsPanel);
        splitPane.setResizeWeight(0.65);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private void incarcaDateInTabelIstoric() {
        populeazaTabelIstoric(comandaService.getToateComenzile());
    }

    private void populeazaTabelIstoric(List<Comanda> comenzi) {
        istoricTableModel.setRowCount(0);
        for (Comanda c : comenzi) {
            istoricTableModel.addRow(new Object[]{
                    c.getId(),
                    c.getIdMasa(),
                    c.getDataOra().toString().replace("T", " "),
                    c.getStareComanda().name(),
                    c.getListaElemente().size(),
                    String.format("%.2f", c.getValoareTotala())
            });
        }
    }

    private void afiseazaDetaliiComandaSelectata() {
        detaliiComandaListModel.clear();
        int selectedRow = tabelIstoric.getSelectedRow();
        if (selectedRow == -1) return;

        int idComanda = (int) istoricTableModel.getValueAt(selectedRow, 0);
        Comanda comanda = comandaService.gasesteComandaDupaId(idComanda);
        if (comanda != null) {
            for (ElementComanda elem : comanda.getListaElemente()) {
                detaliiComandaListModel.addElement(elem.getProdus().getDenumire() +
                        " x " + elem.getCantitate() + " buc. = " +
                        String.format("%.2f", elem.getSubtotal()) + " RON");
            }
        }
    }

    private JPanel creeazaPanouRapoarte() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea txtRezultate = new JTextArea();
        txtRezultate.setEditable(false);
        txtRezultate.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        JButton btnRaportGeneral = new JButton("Raport General & Matrice");
        JButton btnFiltruZi = new JButton("Vanzari pe Zi");
        JButton btnFiltruPerioada = new JButton("Comenzi in Perioada");
        JButton btnVanzariProdus = new JButton("Vanzari Produs");
        JButton btnExportaRaport = new JButton("Exporta Raport in Fisier (.txt)");

        controlPanel.add(btnRaportGeneral);
        controlPanel.add(btnFiltruZi);
        controlPanel.add(btnFiltruPerioada);
        controlPanel.add(btnVanzariProdus);
        controlPanel.add(btnExportaRaport);

        btnRaportGeneral.addActionListener(e -> {
            txtRezultate.setText(raportService.genereazaRaportComplet());
        });

        btnFiltruZi.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Introduceti data (format YYYY-MM-DD):", "2026-11-03");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    LocalDate data = LocalDate.parse(input.trim());
                    double total = raportService.getTotalVanzariInZiua(data);
                    List<Comanda> comenziZi = raportService.getComenziInZiua(data);

                    StringBuilder sb = new StringBuilder();
                    sb.append("=== RAPORT VANZARI IN ZIUA: ").append(data).append(" ===\n\n");
                    sb.append(String.format("Valoare totala incasari: %.2f RON\n", total));
                    sb.append(String.format("Numar comenzi realizate: %d\n\n", comenziZi.size()));
                    sb.append("Comenzile din aceasta zi:\n");
                    for (Comanda c : comenziZi) {
                        sb.append(String.format("  - Comanda #%d | Masa %d | Total: %.2f RON | Stare: %s\n",
                                c.getId(), c.getIdMasa(), c.getValoareTotala(), c.getStareComanda()));
                    }
                    txtRezultate.setText(sb.toString());
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Format data invalid! Utilizati YYYY-MM-DD.", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnFiltruPerioada.addActionListener(e -> {
            JTextField fieldDeLa = new JTextField("2026-11-01", 10);
            JTextField fieldPanaLa = new JTextField("2026-11-06", 10);
            JPanel dialogPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            dialogPanel.add(new JLabel("De la data (YYYY-MM-DD):"));
            dialogPanel.add(fieldDeLa);
            dialogPanel.add(new JLabel("Pana la data (YYYY-MM-DD):"));
            dialogPanel.add(fieldPanaLa);

            int opt = JOptionPane.showConfirmDialog(this, dialogPanel, "Selectare Perioada", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                try {
                    LocalDate deLa = LocalDate.parse(fieldDeLa.getText().trim());
                    LocalDate panaLa = LocalDate.parse(fieldPanaLa.getText().trim());
                    int nr = raportService.getNumarComenziInPerioada(deLa, panaLa);

                    String text = String.format("=== RAPORT PERIOADA: %s - %s ===\n\nNumar total comenzi realizate in perioada: %d comenzi.\n",
                            deLa, panaLa, nr);
                    txtRezultate.setText(text);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Format data invalid!", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnVanzariProdus.addActionListener(e -> {
            List<Produs> produse = catalogService.getToateProdusele();
            if (produse.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nu exista produse in catalog!", "Atentie", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Produs ales = (Produs) JOptionPane.showInputDialog(
                    this,
                    "Alegeti produsul:",
                    "Vanzari per Produs",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    produse.toArray(),
                    produse.get(0)
            );
            if (ales != null) {
                double total = raportService.getTotalVanzariPentruProdus(ales.getId());
                String res = String.format("=== VANZARI PENTRU PRODUSUL: %s ===\n\n" +
                                "ID: %d\nCategorie: %s\nPret unitar: %.2f RON\n\n" +
                                "Total incasari realizate: %.2f RON\n",
                        ales.getDenumire(), ales.getId(), ales.getCategorieProdus(), ales.getPret(), total);
                txtRezultate.setText(res);
            }
        });

        btnExportaRaport.addActionListener(e -> {
            String cale = "fisiere_txt" + File.separator + "raport_statistic.txt";
            try {
                raportService.salveazaRaportInFisier(cale, fisierManager);
                JOptionPane.showMessageDialog(this, "Raportul complet a fost salvat cu succes in: " + cale, "Export Reusit", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la exportul raportului: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        txtRezultate.setText(raportService.genereazaRaportComplet());

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtRezultate), BorderLayout.CENTER);
        return panel;
    }

    private void salveazaDateManual() {
        try {
            fisierManager.salveazaToateDatele(
                    catalogService.getCatalogMap(),
                    comandaService.getComenziList()
            );
            JOptionPane.showMessageDialog(this, "Datele au fost salvate cu succes in fisierele text si de backup!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la salvare: " + ex.getMessage(), "Eroare Critica", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executaSalvareSiInchidere() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Doriti sa salvati modificarile in fisiere inainte de iesire?",
                "Salvare si Inchidere",
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                fisierManager.salveazaToateDatele(
                        catalogService.getCatalogMap(),
                        comandaService.getComenziList()
                );
                JOptionPane.showMessageDialog(this, "Datele au fost salvate cu succes!");
                dispose();
                System.exit(0);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la salvare: " + ex.getMessage(), "Eroare Critica", JOptionPane.ERROR_MESSAGE);
            }
        } else if (confirm == JOptionPane.NO_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}
