package view;

import model.*;
import repository.*;
import utils.IdUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class GuiAdmin extends JFrame {
    private UserRepository userRepo;
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;

    public GuiAdmin(UserRepository userRepo, KelasRepository kelasRepo, MapelRepository mapelRepo) {
        this.userRepo = userRepo;
        this.kelasRepo = kelasRepo;
        this.mapelRepo = mapelRepo;

        setTitle("Dashboard Admin");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Kelola Guru", createPanelGuru());
        tabbedPane.addTab("Kelola Siswa", createPanelSiswa());
        tabbedPane.addTab("Kelola Kelas", createPanelKelas());
        tabbedPane.addTab("Kelola Mapel", createPanelMapel());

        add(tabbedPane);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            JOptionPane.showMessageDialog(this, "Anda telah logout.");
            new GuiLogin(userRepo, kelasRepo, mapelRepo, null, null, null, null, null, null).setVisible(true);
        });
        add(btnLogout, BorderLayout.SOUTH);
    }

    // ================== PANEL GURU ==================
    private JPanel createPanelGuru() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Username", "Nama", "NIP", "Spesialisasi"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        refreshGuruTable(model);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah Guru");
        btnPanel.add(btnAdd);

        btnAdd.addActionListener(e -> {
            JTextField txtUser = new JTextField();
            JTextField txtPass = new JTextField();
            JTextField txtNama = new JTextField();
            JTextField txtEmail = new JTextField();
            JTextField txtNip = new JTextField();
            JTextField txtSpes = new JTextField();
            
            Object[] message = {
                "Username:", txtUser, "Password:", txtPass, "Nama:", txtNama,
                "Email:", txtEmail, "NIP:", txtNip, "Spesialisasi:", txtSpes
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Tambah Guru", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                Guru g = new Guru(IdUtil.generate(), txtUser.getText(), txtPass.getText(), 
                                  txtNama.getText(), txtEmail.getText(), txtNip.getText(), txtSpes.getText());
                userRepo.addUser(g);
                refreshGuruTable(model);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshGuruTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (User u : userRepo.getAll()) {
            if (u instanceof Guru g) {
                model.addRow(new Object[]{g.getIdUser(), g.getUsername(), g.getNamaLengkap(), g.getNip(), g.getSpesialisasi()});
            }
        }
    }

    // ================== PANEL SISWA ==================
    private JPanel createPanelSiswa() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Username", "Nama", "NIS", "Kelas"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        refreshSiswaTable(model);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel(" Cari Siswa: "), BorderLayout.WEST);
        JTextField txtSearch = new JTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH);

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

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah Siswa");
        JButton btnAssign = new JButton("Assign ke Kelas");
        btnPanel.add(btnAdd);
        btnPanel.add(btnAssign);

        btnAdd.addActionListener(e -> {
            JTextField txtUser = new JTextField();
            JTextField txtPass = new JTextField();
            JTextField txtNama = new JTextField();
            JTextField txtEmail = new JTextField();
            JTextField txtNis = new JTextField();
            JTextField txtAngkatan = new JTextField();

            Object[] message = {
                "Username:", txtUser, "Password:", txtPass, "Nama:", txtNama,
                "Email:", txtEmail, "NIS:", txtNis, "Angkatan:", txtAngkatan
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Tambah Siswa", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                Siswa s = new Siswa(IdUtil.generate(), txtUser.getText(), txtPass.getText(), 
                                    txtNama.getText(), txtEmail.getText(), txtNis.getText(), txtAngkatan.getText());
                userRepo.addUser(s);
                refreshSiswaTable(model);
            }
        });

        // --- PERBAIKAN: GUNAKAN DROPDOWN UNTUK MEMILIH SISWA & KELAS ---
        btnAssign.addActionListener(e -> {
            JPanel panelAssign = new JPanel(new GridLayout(2, 2, 10, 10));
            
            // 1. Dropdown Siswa
            JComboBox<Siswa> comboSiswa = new JComboBox<>();
            for(User u : userRepo.getAll()) {
                if(u instanceof Siswa) comboSiswa.addItem((Siswa) u);
            }
            // Agar yang tampil adalah Nama Lengkap
            comboSiswa.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Siswa) setText(((Siswa)value).getNamaLengkap());
                    return this;
                }
            });

            // 2. Dropdown Kelas
            JComboBox<Kelas> comboKelas = new JComboBox<>();
            for(Kelas k : kelasRepo.getAll()) comboKelas.addItem(k);
            // Agar yang tampil adalah Nama Kelas
            comboKelas.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Kelas) setText(((Kelas)value).getNamaKelas());
                    return this;
                }
            });

            panelAssign.add(new JLabel("Pilih Siswa:"));
            panelAssign.add(comboSiswa);
            panelAssign.add(new JLabel("Pilih Kelas:"));
            panelAssign.add(comboKelas);

            int result = JOptionPane.showConfirmDialog(this, panelAssign, "Assign Siswa ke Kelas", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                Siswa s = (Siswa) comboSiswa.getSelectedItem();
                Kelas k = (Kelas) comboKelas.getSelectedItem();

                if (s != null && k != null) {
                    // Update data
                    s.setKelas(k);
                    k.tambahSiswa(s);
                    
                    userRepo.saveToFile(); // Simpan ke file
                    refreshSiswaTable(model); // Update tabel GUI
                    
                    JOptionPane.showMessageDialog(this, "Berhasil assign " + s.getNamaLengkap() + " ke kelas " + k.getNamaKelas());
                } else {
                    JOptionPane.showMessageDialog(this, "Data siswa atau kelas belum dipilih/kosong.");
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshSiswaTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (User u : userRepo.getAll()) {
            if (u instanceof Siswa s) {
                String kls = (s.getKelas() != null) ? s.getKelas().getNamaKelas() : "-";
                model.addRow(new Object[]{s.getIdUser(), s.getUsername(), s.getNamaLengkap(), s.getNis(), kls});
            }
        }
    }

    // ================== PANEL KELAS ==================
    private JPanel createPanelKelas() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Nama Kelas", "Tingkat"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        refreshKelasTable(model);

        JButton btnAdd = new JButton("Tambah Kelas");
        btnAdd.addActionListener(e -> {
            JTextField txtNama = new JTextField();
            JTextField txtTingkat = new JTextField();
            Object[] message = {"Nama Kelas:", txtNama, "Tingkat (10/11/12):", txtTingkat};

            if (JOptionPane.showConfirmDialog(this, message, "Tambah Kelas", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                kelasRepo.addKelas(new Kelas(IdUtil.generate(), txtNama.getText(), txtTingkat.getText()));
                refreshKelasTable(model);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshKelasTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Kelas k : kelasRepo.getAll()) model.addRow(new Object[]{k.getIdKelas(), k.getNamaKelas(), k.getTingkat()});
    }

    // ================== PANEL MAPEL ==================
    private JPanel createPanelMapel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Nama Mapel", "Deskripsi", "Tingkat"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        refreshMapelTable(model);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah Mapel");
        JButton btnAssign = new JButton("Assign Guru ke Mapel");
        btnPanel.add(btnAdd);
        btnPanel.add(btnAssign);

        btnAdd.addActionListener(e -> {
            JTextField txtNama = new JTextField();
            JTextField txtDesk = new JTextField();
            JTextField txtTingkat = new JTextField();
            Object[] msg = {"Nama:", txtNama, "Deskripsi:", txtDesk, "Tingkat:", txtTingkat};
            
            if (JOptionPane.showConfirmDialog(this, msg, "Tambah Mapel", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                MataPelajaran m = new MataPelajaran(IdUtil.generate(), txtNama.getText(), txtDesk.getText(), txtTingkat.getText());
                mapelRepo.addMapel(m);
                // Distribusi otomatis ke kelas yang sesuai tingkatnya
                for (Kelas k : kelasRepo.getAll()) {
                    if (k.getTingkat().equals(m.getTingkat())) k.tambahMapel(m);
                }
                refreshMapelTable(model);
            }
        });

        // --- PERBAIKAN: GUNAKAN DROPDOWN UNTUK MEMILIH GURU, MAPEL, KELAS ---
        btnAssign.addActionListener(e -> {
            JPanel panelAssign = new JPanel(new GridLayout(3, 2, 10, 10));

            // 1. Dropdown Guru
            JComboBox<Guru> comboGuru = new JComboBox<>();
            for(User u : userRepo.getAll()) {
                if(u instanceof Guru) comboGuru.addItem((Guru) u);
            }
            comboGuru.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Guru) setText(((Guru)value).getNamaLengkap());
                    return this;
                }
            });

            // 2. Dropdown Mapel
            JComboBox<MataPelajaran> comboMapel = new JComboBox<>();
            for(MataPelajaran m : mapelRepo.getAll()) comboMapel.addItem(m);
            comboMapel.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof MataPelajaran) setText(((MataPelajaran)value).getNamaMapel() + " (Kls " + ((MataPelajaran)value).getTingkat() + ")");
                    return this;
                }
            });

            // 3. Dropdown Kelas
            JComboBox<Kelas> comboKelas = new JComboBox<>();
            for(Kelas k : kelasRepo.getAll()) comboKelas.addItem(k);
            comboKelas.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Kelas) setText(((Kelas)value).getNamaKelas());
                    return this;
                }
            });

            panelAssign.add(new JLabel("Pilih Guru:"));
            panelAssign.add(comboGuru);
            panelAssign.add(new JLabel("Pilih Mapel:"));
            panelAssign.add(comboMapel);
            panelAssign.add(new JLabel("Pilih Kelas:"));
            panelAssign.add(comboKelas);

            int result = JOptionPane.showConfirmDialog(this, panelAssign, "Assign Guru Mengajar", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                Guru g = (Guru) comboGuru.getSelectedItem();
                MataPelajaran m = (MataPelajaran) comboMapel.getSelectedItem();
                Kelas k = (Kelas) comboKelas.getSelectedItem();

                if (g != null && m != null && k != null) {
                    // Update data
                    g.tambahMapel(m);
                    g.tambahKelas(k);
                    
                    userRepo.saveToFile(); // Simpan
                    JOptionPane.showMessageDialog(this, "Sukses assign Guru " + g.getNamaLengkap());
                } else {
                    JOptionPane.showMessageDialog(this, "Data belum lengkap dipilih.");
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshMapelTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (MataPelajaran m : mapelRepo.getAll()) model.addRow(new Object[]{m.getIdMapel(), m.getNamaMapel(), m.getDeskripsi(), m.getTingkat()});
    }
}