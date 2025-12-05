package view;

import model.*;
import repository.*;
import utils.IdUtil;
// Hapus import SecurityUtil

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
        setSize(800, 600);
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
            // Kembali ke login screen (pastikan parameter null aman atau sesuaikan dengan App.java)
            new GuiLogin(userRepo, kelasRepo, mapelRepo, null, null, null, null, null, null).setVisible(true);
        });
        add(btnLogout, BorderLayout.SOUTH);
    }

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
                // HAPUS HASH: Simpan password langsung
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

    private JPanel createPanelSiswa() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Username", "Nama", "NIS", "Kelas"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        // Fitur Search (Tetap Ada)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        refreshSiswaTable(model);

        // Panel Search
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
                // HAPUS HASH: Simpan password langsung
                Siswa s = new Siswa(IdUtil.generate(), txtUser.getText(), txtPass.getText(), 
                                    txtNama.getText(), txtEmail.getText(), txtNis.getText(), txtAngkatan.getText());
                userRepo.addUser(s);
                refreshSiswaTable(model);
            }
        });

        btnAssign.addActionListener(e -> {
            String idSiswa = JOptionPane.showInputDialog("Masukkan ID Siswa:");
            String idKelas = JOptionPane.showInputDialog("Masukkan ID Kelas:");
            
            User u = null;
            for(User usr : userRepo.getAll()) if(usr.getIdUser().equals(idSiswa)) u = usr;
            Kelas k = kelasRepo.findById(idKelas);

            if (u instanceof Siswa s && k != null) {
                s.setKelas(k);
                k.tambahSiswa(s);
                userRepo.saveToFile();
                refreshSiswaTable(model);
                JOptionPane.showMessageDialog(this, "Berhasil assign siswa.");
            } else {
                JOptionPane.showMessageDialog(this, "ID Siswa/Kelas tidak valid.");
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
                for (Kelas k : kelasRepo.getAll()) {
                    if (k.getTingkat().equals(m.getTingkat())) k.tambahMapel(m);
                }
                refreshMapelTable(model);
            }
        });

        btnAssign.addActionListener(e -> {
            String idGuru = JOptionPane.showInputDialog("ID Guru:");
            String idMapel = JOptionPane.showInputDialog("ID Mapel:");
            String idKelas = JOptionPane.showInputDialog("ID Kelas:");

            User u = null;
            for(User usr : userRepo.getAll()) if(usr.getIdUser().equals(idGuru)) u = usr;
            MataPelajaran m = mapelRepo.findById(idMapel);
            Kelas k = kelasRepo.findById(idKelas);

            if (u instanceof Guru g && m != null && k != null) {
                g.tambahMapel(m);
                g.tambahKelas(k);
                userRepo.saveToFile();
                JOptionPane.showMessageDialog(this, "Sukses assign Guru.");
            } else {
                JOptionPane.showMessageDialog(this, "Data ID tidak valid.");
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