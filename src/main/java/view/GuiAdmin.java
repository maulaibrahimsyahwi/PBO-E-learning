package view;

import model.*;
import repository.*;
import utils.IdUtil;
import utils.SecurityUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class GuiAdmin extends JFrame {
    private UserRepository userRepo;
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;
    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;
    private ForumRepository forumRepo;
    private AbsensiRepository absensiRepo;
    private SoalRepository soalRepo;

    public GuiAdmin(UserRepository userRepo, KelasRepository kelasRepo, MapelRepository mapelRepo,
                    MateriRepository materiRepo, TugasRepository tugasRepo, UjianRepository ujianRepo,
                    JawabanRepository jawabanRepo, NilaiRepository nilaiRepo, ForumRepository forumRepo,
                    AbsensiRepository absensiRepo, SoalRepository soalRepo) {
        
        this.userRepo = userRepo;
        this.kelasRepo = kelasRepo;
        this.mapelRepo = mapelRepo;
        this.materiRepo = materiRepo;
        this.tugasRepo = tugasRepo;
        this.ujianRepo = ujianRepo;
        this.jawabanRepo = jawabanRepo;
        this.nilaiRepo = nilaiRepo;
        this.forumRepo = forumRepo;
        this.absensiRepo = absensiRepo;
        this.soalRepo = soalRepo;

        setTitle("Dashboard Admin");
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Kelola Guru", createPanelGuru());
        tabbedPane.addTab("Kelola Siswa", createPanelSiswa());
        tabbedPane.addTab("Kelola Kelas", createPanelKelas());
        tabbedPane.addTab("Kelola Mapel", createPanelMapel());

        add(tabbedPane);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(255, 100, 100)); 
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            dispose();
            new GuiLogin(userRepo, kelasRepo, mapelRepo, materiRepo, tugasRepo, ujianRepo, 
                         jawabanRepo, nilaiRepo, forumRepo, absensiRepo, soalRepo).setVisible(true);
        });
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        long jumlahGuru = userRepo.getAll().stream().filter(u -> u instanceof Guru).count();
        long jumlahSiswa = userRepo.getAll().stream().filter(u -> u instanceof Siswa).count();
        int jumlahKelas = kelasRepo.getAll().size();
        int jumlahMapel = mapelRepo.getAll().size();

        panel.add(createStatCard("Total Guru", String.valueOf(jumlahGuru), new Color(255, 200, 100)));
        panel.add(createStatCard("Total Siswa", String.valueOf(jumlahSiswa), new Color(100, 200, 255)));
        panel.add(createStatCard("Jumlah Kelas", String.valueOf(jumlahKelas), new Color(100, 255, 100)));
        panel.add(createStatCard("Mata Pelajaran", String.valueOf(jumlahMapel), new Color(255, 100, 255)));

        return panel;
    }

    private JPanel createStatCard(String title, String count, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel lblCount = new JLabel(count, SwingConstants.CENTER);
        lblCount.setFont(new Font("Arial", Font.BOLD, 48));
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblCount, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPanelGuru() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Username", "Nama", "NIP", "Spesialisasi"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        refreshGuruTable(model);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); 
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); 
        
        JButton btnAdd = new JButton("Tambah Guru");
        JButton btnDelete = new JButton("Hapus"); 
        
        Dimension btnSize = new Dimension(120, 35);
        btnAdd.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setBackground(new Color(255, 150, 150)); 

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);

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
                String passHash = SecurityUtil.hashPassword(txtPass.getText());
                Guru g = new Guru(IdUtil.generate(), txtUser.getText(), passHash, 
                                  txtNama.getText(), txtEmail.getText(), txtNip.getText(), txtSpes.getText());
                userRepo.addUser(g);
                refreshGuruTable(model);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih guru yang akan dihapus!");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            String nama = (String) model.getValueAt(row, 2);
            
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus guru " + nama + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                userRepo.deleteUser(id);
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
        String[] columns = {"ID", "Username", "Nama", "NIS", "Kelas", "Angkatan", "Email"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        refreshSiswaTable(model);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton btnAdd = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit Data"); 
        JButton btnAssign = new JButton("Assign Kelas");
        JButton btnDelete = new JButton("Hapus"); 
        
        Dimension btnSize = new Dimension(110, 35);
        btnAdd.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnAssign.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setBackground(new Color(255, 150, 150));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnAssign);
        btnPanel.add(btnDelete);

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
                String passHash = SecurityUtil.hashPassword(txtPass.getText());
                Siswa s = new Siswa(IdUtil.generate(), txtUser.getText(), passHash, 
                                    txtNama.getText(), txtEmail.getText(), txtNis.getText(), txtAngkatan.getText());
                userRepo.addUser(s);
                refreshSiswaTable(model);
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih siswa dulu!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) model.getValueAt(modelRow, 0);
            String user = (String) model.getValueAt(modelRow, 1);
            String nama = (String) model.getValueAt(modelRow, 2);
            String nis = (String) model.getValueAt(modelRow, 3);
            String angk = (String) model.getValueAt(modelRow, 5);
            String email = (String) model.getValueAt(modelRow, 6);

            JTextField txtUser = new JTextField(user);
            JTextField txtNama = new JTextField(nama);
            JTextField txtNis = new JTextField(nis);
            JTextField txtAngk = new JTextField(angk);
            JTextField txtEmail = new JTextField(email);

            Object[] message = {
                "Username:", txtUser, "Nama Lengkap:", txtNama,
                "NIS:", txtNis, "Angkatan:", txtAngk, "Email:", txtEmail
            };

            int opt = JOptionPane.showConfirmDialog(this, message, "Edit Siswa", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                Siswa sBaru = new Siswa(id, txtUser.getText(), "", txtNama.getText(), txtEmail.getText(), txtNis.getText(), txtAngk.getText());
                userRepo.updateSiswa(sBaru);
                refreshSiswaTable(model);
                JOptionPane.showMessageDialog(this, "Data Siswa berhasil diupdate!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih siswa dulu!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) model.getValueAt(modelRow, 0);
            String nama = (String) model.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this, "Hapus siswa " + nama + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                userRepo.deleteUser(id);
                refreshSiswaTable(model);
            }
        });

        btnAssign.addActionListener(e -> {
            JPanel panelAssign = new JPanel(new GridLayout(2, 2, 10, 10));
            JComboBox<Siswa> comboSiswa = new JComboBox<>();
            
            for(Siswa s : userRepo.getAllSiswa()) {
                comboSiswa.addItem(s);
            }
            
            comboSiswa.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Siswa) setText(((Siswa)value).getNamaLengkap());
                    return this;
                }
            });
            JComboBox<Kelas> comboKelas = new JComboBox<>();
            for(Kelas k : kelasRepo.getAll()) comboKelas.addItem(k);
            comboKelas.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Kelas) setText(((Kelas)value).getNamaKelas());
                    return this;
                }
            });

            panelAssign.add(new JLabel("Pilih Siswa:")); panelAssign.add(comboSiswa);
            panelAssign.add(new JLabel("Pilih Kelas Tujuan:")); panelAssign.add(comboKelas);

            int result = JOptionPane.showConfirmDialog(this, panelAssign, "Assign Siswa ke Kelas", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Siswa s = (Siswa) comboSiswa.getSelectedItem();
                Kelas k = (Kelas) comboKelas.getSelectedItem();
                if (s != null && k != null) {
                    s.setKelas(k);
                    userRepo.updateSiswa(s); 
                    refreshSiswaTable(model);
                    JOptionPane.showMessageDialog(this, "Berhasil assign " + s.getNamaLengkap() + " ke kelas " + k.getNamaKelas());
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshSiswaTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Siswa s : userRepo.getAllSiswa()) {
            String kls = (s.getKelas() != null) ? s.getKelas().getNamaKelas() : "-";
            model.addRow(new Object[]{s.getIdUser(), s.getUsername(), s.getNamaLengkap(), s.getNis(), kls, s.getAngkatan(), s.getEmail()});
        }
    }

    private JPanel createPanelKelas() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Nama Kelas", "Tingkat"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        
        refreshKelasTable(model);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton btnAdd = new JButton("Tambah Kelas");
        JButton btnEdit = new JButton("Edit Kelas");
        JButton btnDelete = new JButton("Hapus"); 
        
        Dimension btnSize = new Dimension(120, 35);
        btnAdd.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setBackground(new Color(255, 150, 150));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        btnAdd.addActionListener(e -> {
            JTextField txtNama = new JTextField();
            JTextField txtTingkat = new JTextField();
            Object[] message = {"Nama Kelas:", txtNama, "Tingkat (10/11/12):", txtTingkat};

            if (JOptionPane.showConfirmDialog(this, message, "Tambah Kelas", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String idKelas = IdUtil.generate();
                Kelas kBaru = new Kelas(idKelas, txtNama.getText(), txtTingkat.getText());
                kelasRepo.addKelas(kBaru);
                
                // Distribusikan Mapel ke Kelas secara otomatis berdasarkan tingkat
                for (MataPelajaran m : mapelRepo.getAll()) {
                    if (m.getTingkat().equals(kBaru.getTingkat())) {
                        kelasRepo.addMapelToKelas(idKelas, m.getIdMapel());
                    }
                }
                
                refreshKelasTable(model);
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih kelas yang mau diedit!");
                return;
            }
            String idKelas = (String) model.getValueAt(row, 0);
            String namaLama = (String) model.getValueAt(row, 1);
            String tingkatLama = (String) model.getValueAt(row, 2);

            JTextField txtNama = new JTextField(namaLama);
            JTextField txtTingkat = new JTextField(tingkatLama);
            Object[] message = {"ID: " + idKelas, "Nama Kelas:", txtNama, "Tingkat (10/11/12):", txtTingkat};

            if (JOptionPane.showConfirmDialog(this, message, "Edit Kelas", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                Kelas kBaru = new Kelas(idKelas, txtNama.getText(), txtTingkat.getText());
                kelasRepo.updateKelas(kBaru);
                refreshKelasTable(model);
                JOptionPane.showMessageDialog(this, "Data Kelas berhasil diubah!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih kelas yang mau dihapus!");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            String nama = (String) model.getValueAt(row, 1);

            int confirm = JOptionPane.showConfirmDialog(this, "Hapus kelas " + nama + "? (Data siswa di kelas ini mungkin terpengaruh)", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                kelasRepo.deleteKelas(id);
                refreshKelasTable(model);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshKelasTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Kelas k : kelasRepo.getAll()) model.addRow(new Object[]{k.getIdKelas(), k.getNamaKelas(), k.getTingkat()});
    }

    private JPanel createPanelMapel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Nama Mapel", "Deskripsi", "Tingkat"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        
        refreshMapelTable(model);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton btnAdd = new JButton("Tambah Mapel");
        JButton btnEdit = new JButton("Edit Mapel");
        JButton btnAssign = new JButton("Assign Guru");
        JButton btnDelete = new JButton("Hapus"); 
        
        Dimension btnSize = new Dimension(120, 35);
        btnAdd.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnAssign.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setBackground(new Color(255, 150, 150));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnAssign);
        btnPanel.add(btnDelete);

        btnAdd.addActionListener(e -> {
            JTextField txtNama = new JTextField();
            JTextField txtDesk = new JTextField();
            JTextField txtTingkat = new JTextField();
            Object[] msg = {"Nama:", txtNama, "Deskripsi:", txtDesk, "Tingkat:", txtTingkat};
            
            if (JOptionPane.showConfirmDialog(this, msg, "Tambah Mapel", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                MataPelajaran m = new MataPelajaran(IdUtil.generate(), txtNama.getText(), txtDesk.getText(), txtTingkat.getText());
                mapelRepo.addMapel(m);
                
                int count = 0;
                for (Kelas k : kelasRepo.getAll()) {
                    if (k.getTingkat().equals(m.getTingkat())) {
                        k.tambahMapel(m); 
                        kelasRepo.addMapelToKelas(k.getIdKelas(), m.getIdMapel()); // Simpan ke DB
                        count++;
                    }
                }
                refreshMapelTable(model);
                JOptionPane.showMessageDialog(this, "Mapel ditambahkan dan didistribusikan ke " + count + " kelas.");
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih mapel yang mau diedit!");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            String nama = (String) model.getValueAt(row, 1);
            String desk = (String) model.getValueAt(row, 2);
            String tkt = (String) model.getValueAt(row, 3);

            JTextField txtNama = new JTextField(nama);
            JTextField txtDesk = new JTextField(desk);
            JTextField txtTingkat = new JTextField(tkt);
            Object[] msg = {"Nama:", txtNama, "Deskripsi:", txtDesk, "Tingkat:", txtTingkat};
            
            if (JOptionPane.showConfirmDialog(this, msg, "Edit Mapel", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                MataPelajaran mBaru = new MataPelajaran(id, txtNama.getText(), txtDesk.getText(), txtTingkat.getText());
                mapelRepo.updateMapel(mBaru);
                refreshMapelTable(model);
                JOptionPane.showMessageDialog(this, "Mapel berhasil diupdate!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih mapel yang mau dihapus!");
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            String nama = (String) model.getValueAt(row, 1);

            int confirm = JOptionPane.showConfirmDialog(this, "Hapus mapel " + nama + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mapelRepo.deleteMapel(id);
                refreshMapelTable(model);
            }
        });

        btnAssign.addActionListener(e -> {
            JPanel panelAssign = new JPanel(new GridLayout(3, 2, 10, 10));
            JComboBox<Guru> comboGuru = new JComboBox<>();
            for(User u : userRepo.getAll()) {
                if(u instanceof Guru) comboGuru.addItem((Guru) u);
            }
            comboGuru.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Guru) setText(((Guru)value).getNamaLengkap());
                    return this;
                }
            });
            JComboBox<MataPelajaran> comboMapel = new JComboBox<>();
            for(MataPelajaran m : mapelRepo.getAll()) comboMapel.addItem(m);
            comboMapel.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof MataPelajaran) {
                        MataPelajaran mp = (MataPelajaran) value;
                        setText(mp.getNamaMapel() + " (Tingkat " + mp.getTingkat() + ")");
                    }
                    return this;
                }
            });
            JComboBox<Kelas> comboKelas = new JComboBox<>();
            for(Kelas k : kelasRepo.getAll()) comboKelas.addItem(k);
            comboKelas.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Kelas) setText(((Kelas)value).getNamaKelas());
                    return this;
                }
            });

            panelAssign.add(new JLabel("Pilih Guru:")); panelAssign.add(comboGuru);
            panelAssign.add(new JLabel("Mengajar Mapel:")); panelAssign.add(comboMapel);
            panelAssign.add(new JLabel("Di Kelas:")); panelAssign.add(comboKelas);

            int result = JOptionPane.showConfirmDialog(this, panelAssign, "Assign Guru Mengajar", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Guru g = (Guru) comboGuru.getSelectedItem();
                MataPelajaran m = (MataPelajaran) comboMapel.getSelectedItem();
                Kelas k = (Kelas) comboKelas.getSelectedItem();
                if (g != null && m != null && k != null) {
                    g.tambahMapel(m);
                    g.tambahKelas(k);
                    
                    // FIX 1: Gunakan updateGuru agar data guru tersimpan (termasuk relasinya)
                    userRepo.updateGuru(g); 
                    
                    // FIX 2: Hubungkan Mapel ke Kelas agar muncul di Dashboard Siswa
                    kelasRepo.addMapelToKelas(k.getIdKelas(), m.getIdMapel());
                    
                    JOptionPane.showMessageDialog(this, "Sukses assign Guru " + g.getNamaLengkap());
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