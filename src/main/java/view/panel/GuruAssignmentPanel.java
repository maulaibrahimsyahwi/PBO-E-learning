package view.panel;

import model.Guru;
import model.Kelas;
import model.MataPelajaran;
import model.User;
import repository.KelasRepository;
import repository.MapelRepository;
import repository.UserRepository;
import view.dialog.EditGuruAssignmentDialog;
import view.renderer.GuruListRenderer;
import view.renderer.KelasListRenderer;
import view.renderer.MapelAssignmentRenderer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
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
        JButton btnAssign = new JButton("Assign Guru"); // Tombol Baru (Pindahan)
        JButton btnEdit = new JButton("Edit Assignment");
        JButton btnHapusAssignment = new JButton("Hapus Assignment");

        Dimension btnSize = new Dimension(150, 35);
        btnRefresh.setPreferredSize(new Dimension(100, 35));
        btnAssign.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnHapusAssignment.setPreferredSize(btnSize);
        btnHapusAssignment.setBackground(new Color(255, 150, 150));

        btnPanel.add(btnRefresh);
        btnPanel.add(btnAssign); // Ditambahkan di sebelah kiri Edit
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapusAssignment);

        addListeners(btnRefresh, btnAssign, btnEdit, btnHapusAssignment);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void addListeners(JButton btnRefresh, JButton btnAssign, JButton btnEdit, JButton btnHapusAssignment) {
        btnRefresh.addActionListener(e -> refreshTable());

        // --- Logika Pindahan dari MapelManagementPanel ---
        btnAssign.addActionListener(e -> {
            JComboBox<User> comboGuru = new JComboBox<>();
            for(User u : userRepo.getAll()) {
                if(u instanceof Guru) comboGuru.addItem(u);
            }
            comboGuru.setRenderer(new GuruListRenderer());
            
            DefaultListModel<MataPelajaran> mapelListModel = new DefaultListModel<>();
            List<MataPelajaran> allMapel = mapelRepo.getAll();
            allMapel.sort(Comparator.comparing(MataPelajaran::getTingkat)
                    .thenComparing(MataPelajaran::getNamaMapel));
            
            for(MataPelajaran m : allMapel) mapelListModel.addElement(m);
            
            JList<MataPelajaran> listMapel = new JList<>(mapelListModel);
            listMapel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            listMapel.setCellRenderer(new MapelAssignmentRenderer());
            JScrollPane scrollMapel = new JScrollPane(listMapel);
            scrollMapel.setPreferredSize(new Dimension(300, 150));

            DefaultListModel<Kelas> kelasListModel = new DefaultListModel<>();
            List<Kelas> allKelas = kelasRepo.getAll();
            allKelas.sort(Comparator.comparing(Kelas::getTingkat)
                    .thenComparing(Kelas::getNamaKelas));
            
            for(Kelas k : allKelas) kelasListModel.addElement(k);
            
            JList<Kelas> listKelas = new JList<>(kelasListModel);
            listKelas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            listKelas.setCellRenderer(new KelasListRenderer());
            JScrollPane scrollKelas = new JScrollPane(listKelas);
            scrollKelas.setPreferredSize(new Dimension(300, 150));

            JPanel panelAssign = new JPanel();
            panelAssign.setLayout(new BoxLayout(panelAssign, BoxLayout.Y_AXIS));
            panelAssign.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel guruPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            guruPanel.add(new JLabel("Pilih Guru:"));
            guruPanel.add(comboGuru);
            guruPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            panelAssign.add(guruPanel);
            panelAssign.add(Box.createRigidArea(new Dimension(0, 10)));
            panelAssign.add(new JLabel("Pilih Mapel (Sorted by Tingkat)"));
            panelAssign.add(scrollMapel);
            panelAssign.add(Box.createRigidArea(new Dimension(0, 10)));
            panelAssign.add(new JLabel("Pilih Kelas (Sorted by Tingkat)"));
            panelAssign.add(scrollKelas);

            int result = JOptionPane.showConfirmDialog(this, panelAssign, "Assign Guru Mengajar (Multi-Select)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                Guru g = (Guru) comboGuru.getSelectedItem();
                List<MataPelajaran> selectedMapel = listMapel.getSelectedValuesList();
                List<Kelas> selectedKelas = listKelas.getSelectedValuesList();
                
                if (g != null && !selectedMapel.isEmpty() && !selectedKelas.isEmpty()) {
                    
                    // --- VALIDASI TINGKAT ---
                    StringBuilder mismatchWarning = new StringBuilder();
                    boolean hasMismatch = false;

                    for (MataPelajaran m : selectedMapel) {
                        for (Kelas k : selectedKelas) {
                            if (!m.getTingkat().equals(k.getTingkat()) && !m.getTingkat().equals("-")) {
                                hasMismatch = true;
                                mismatchWarning.append("• ")
                                    .append(m.getNamaMapel()).append(" (Tingkat ").append(m.getTingkat()).append(")")
                                    .append(" ➜ ")
                                    .append(k.getNamaKelas()).append(" (Tingkat ").append(k.getTingkat()).append(")")
                                    .append("\n");
                            }
                        }
                    }

                    if (hasMismatch) {
                        String msg = "PERINGATAN: Terdapat ketidakcocokan Tingkat antara Mapel dan Kelas yang dipilih:\n\n" +
                                     mismatchWarning.toString() +
                                     "\nApakah Anda yakin ingin tetap melanjutkan assignment ini?";
                        
                        int confirmMismatch = JOptionPane.showConfirmDialog(this, msg, "Peringatan Mismatch Tingkat", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        
                        if (confirmMismatch != JOptionPane.YES_OPTION) {
                            return; 
                        }
                    }
                    // -----------------------

                    int mapelCount = 0;
                    int kelasCount = 0;
                    
                    for (MataPelajaran m : selectedMapel) {
                        g.tambahMapel(m);
                        mapelCount++;
                    }

                    for (Kelas k : selectedKelas) {
                        g.tambahKelas(k);
                        kelasCount++;
                        
                        for (MataPelajaran m : selectedMapel) {
                            kelasRepo.addMapelToKelas(k.getIdKelas(), m.getIdMapel());
                        }
                    }
                    
                    userRepo.updateGuru(g); 
                    refreshTable(); // Refresh tabel assignment
                    
                    JOptionPane.showMessageDialog(this, 
                        "Sukses assign Guru " + g.getNamaLengkap() + "\n" +
                        "Mengajar " + mapelCount + " Mapel di " + kelasCount + " Kelas."
                    );
                } else if (g == null) {
                    JOptionPane.showMessageDialog(this, "Pilih Guru yang akan di-assign.");
                } else {
                    JOptionPane.showMessageDialog(this, "Pilih minimal 1 Mapel dan 1 Kelas.");
                }
            }
        });
        // ---------------------------------------------

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