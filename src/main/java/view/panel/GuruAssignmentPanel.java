package view.panel;

import model.Guru;
import model.User;
import repository.KelasRepository;
import repository.MapelRepository;
import repository.UserRepository;
import view.dialog.EditGuruAssignmentDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.stream.Collectors;

public class GuruAssignmentPanel extends JPanel {
    private UserRepository userRepo;
    private MapelRepository mapelRepo;
    private KelasRepository kelasRepo;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public GuruAssignmentPanel(UserRepository userRepo, MapelRepository mapelRepo, KelasRepository kelasRepo) {
        this.userRepo = userRepo;
        this.mapelRepo = mapelRepo;
        this.kelasRepo = kelasRepo;
        setLayout(new BorderLayout());

        String[] columns = {"Nama Guru", "NIP", "Mata Pelajaran yang Diajar", "Kelas yang Diajar"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);

        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);

        refreshTable();
        setupLayout();
    }
    
    public void refreshTable() {
        model.setRowCount(0);

        for (User u : userRepo.getAll()) {
            if (u instanceof Guru) {
                Guru g = (Guru) u;

                String mapelStr = g.getMapelDiampu().isEmpty() ?
                    "-" :
                    g.getMapelDiampu().stream()
                        .map(m -> m.getNamaMapel() + " (Kls " + m.getTingkat() + ")")
                        .collect(Collectors.joining(", "));

                String kelasStr = g.getDaftarKelas().isEmpty() ?
                    "-" :
                    g.getDaftarKelas().stream()
                        .map(k -> k.getNamaKelas())
                        .collect(Collectors.joining(", "));

                model.addRow(new Object[]{
                    g.getNamaLengkap(),
                    g.getNip(),
                    mapelStr,
                    kelasStr
                });
            }
        }
    }
    
    private void setupLayout() {
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.add(new JLabel("Cari"), BorderLayout.WEST);
        JTextField txtSearch = new JTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton btnRefresh = new JButton("Refresh");
        JButton btnEdit = new JButton("Edit Assignment");
        JButton btnHapusAssignment = new JButton("Hapus Assignment");

        Dimension btnSize = new Dimension(150, 35);
        btnRefresh.setPreferredSize(new Dimension(100, 35));
        btnEdit.setPreferredSize(btnSize);
        btnHapusAssignment.setPreferredSize(btnSize);
        btnHapusAssignment.setBackground(new Color(255, 150, 150));

        btnPanel.add(btnRefresh);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapusAssignment);

        addListeners(btnRefresh, btnEdit, btnHapusAssignment);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void addListeners(JButton btnRefresh, JButton btnEdit, JButton btnHapusAssignment) {
        btnRefresh.addActionListener(e -> refreshTable());

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih guru yang assignment-nya ingin diedit!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String namaGuru = (String) model.getValueAt(modelRow, 0);

            Guru guruSelected = userRepo.getAll().stream()
                .filter(u -> u instanceof Guru && u.getNamaLengkap().equals(namaGuru))
                .map(u -> (Guru) u)
                .findFirst().orElse(null);

            if (guruSelected != null) {
                new EditGuruAssignmentDialog((JFrame) SwingUtilities.getWindowAncestor(this), guruSelected, userRepo, mapelRepo, kelasRepo, model).setVisible(true);
            }
        });

        btnHapusAssignment.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih guru yang assignment-nya ingin dihapus!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String namaGuru = (String) model.getValueAt(modelRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus SEMUA assignment untuk guru " + namaGuru + "?\n(Mapel & Kelas akan dikosongkan)",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Guru guruSelected = userRepo.getAll().stream()
                    .filter(u -> u instanceof Guru && u.getNamaLengkap().equals(namaGuru))
                    .map(u -> (Guru) u)
                    .findFirst().orElse(null);

                if (guruSelected != null) {
                    guruSelected.getMapelDiampu().clear();
                    guruSelected.getDaftarKelas().clear();
                    userRepo.updateGuru(guruSelected);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Assignment berhasil dihapus!");
                }
            }
        });
    }
}