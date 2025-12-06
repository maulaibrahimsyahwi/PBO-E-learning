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
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

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
        tabbedPane.addTab("Assignment Guru", createPanelAssignment());


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

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model); 
        table.setRowSorter(sorter); 
        
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10)); 
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        searchPanel.add(new JLabel(" Cari Guru: "), BorderLayout.WEST); 
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
        JButton btnAssign = new JButton("Assign ke Kelas");
        JButton btnDelete = new JButton("Hapus"); 
        
        Dimension btnSize = new Dimension(130, 35);
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
            // 1. Setup Siswa List (Multi-Select - JList)
            DefaultListModel<Siswa> siswaListModel = new DefaultListModel<>();
            for (Siswa s : userRepo.getAllSiswa()) siswaListModel.addElement(s);
            JList<Siswa> listSiswa = new JList<>(siswaListModel);
            listSiswa.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            listSiswa.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Siswa) {
                        Siswa s = (Siswa) value;
                        String kelasStr = (s.getKelas() != null) ? s.getKelas().getNamaKelas() : "-";
                        setText(s.getNamaLengkap() + " (" + s.getNis() + ") - Kelas: " + kelasStr);
                    }
                    return this;
                }
            });
            JScrollPane scrollSiswa = new JScrollPane(listSiswa);
            scrollSiswa.setPreferredSize(new Dimension(350, 200));

            // 2. Setup Kelas Dropdown (Single Select - JComboBox)
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

            // 3. Combine components in a dialog panel (BoxLayout for vertical alignment)
            JPanel panelAssign = new JPanel();
            panelAssign.setLayout(new BoxLayout(panelAssign, BoxLayout.Y_AXIS));
            panelAssign.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Kelas Panel (FlowLayout for single row)
            JPanel kelasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            kelasPanel.add(new JLabel("Pilih Kelas Tujuan:"));
            kelasPanel.add(comboKelas);
            kelasPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            panelAssign.add(kelasPanel);
            panelAssign.add(Box.createRigidArea(new Dimension(0, 10)));
            panelAssign.add(new JLabel("Pilih Siswa (Ctrl+Klik untuk multi-pilih):"));
            panelAssign.add(scrollSiswa);

            // Show Dialog
            int result = JOptionPane.showConfirmDialog(this, panelAssign, "Assign Siswa ke Kelas (Multi-Select)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                Kelas k = (Kelas) comboKelas.getSelectedItem();
                List<Siswa> selectedSiswa = listSiswa.getSelectedValuesList();
                
                if (k != null && !selectedSiswa.isEmpty()) {
                    int count = 0;
                    for (Siswa s : selectedSiswa) {
                        s.setKelas(k);
                        userRepo.updateSiswa(s); 
                        count++;
                    }
                    
                    refreshSiswaTable(model);
                    JOptionPane.showMessageDialog(this, "Berhasil assign " + count + " Siswa ke kelas " + k.getNamaKelas());
                } else if (k == null) {
                    JOptionPane.showMessageDialog(this, "Pilih Kelas Tujuan.");
                } else {
                    JOptionPane.showMessageDialog(this, "Pilih minimal 1 Siswa.");
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

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.add(new JLabel(" Cari Kelas: "), BorderLayout.WEST);
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

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.add(new JLabel(" Cari Mapel: "), BorderLayout.WEST);
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
                        kelasRepo.addMapelToKelas(k.getIdKelas(), m.getIdMapel());
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

        btnAssign.addActionListener(e -> {
            // 1. Setup Guru Dropdown (Single Select - JComboBox)
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
            
            // 2. Setup Mapel List (Multi-Select - JList)
            DefaultListModel<MataPelajaran> mapelListModel = new DefaultListModel<>();
            for(MataPelajaran m : mapelRepo.getAll()) mapelListModel.addElement(m);
            JList<MataPelajaran> listMapel = new JList<>(mapelListModel);
            listMapel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            listMapel.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof MataPelajaran) {
                        MataPelajaran mp = (MataPelajaran) value;
                        setText(mp.getNamaMapel() + " (Tingkat " + mp.getTingkat() + ")");
                    }
                    return this;
                }
            });
            JScrollPane scrollMapel = new JScrollPane(listMapel);
            scrollMapel.setPreferredSize(new Dimension(300, 150));

            // 3. Setup Kelas List (Multi-Select - JList)
            DefaultListModel<Kelas> kelasListModel = new DefaultListModel<>();
            for(Kelas k : kelasRepo.getAll()) kelasListModel.addElement(k);
            JList<Kelas> listKelas = new JList<>(kelasListModel);
            listKelas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            listKelas.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value instanceof Kelas) setText(((Kelas)value).getNamaKelas());
                    return this;
                }
            });
            JScrollPane scrollKelas = new JScrollPane(listKelas);
            scrollKelas.setPreferredSize(new Dimension(300, 150));

            // 4. Combine components in a dialog panel (BoxLayout for vertical alignment)
            JPanel panelAssign = new JPanel();
            panelAssign.setLayout(new BoxLayout(panelAssign, BoxLayout.Y_AXIS));
            panelAssign.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Guru Panel (FlowLayout for single row)
            JPanel guruPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            guruPanel.add(new JLabel("Pilih Guru:"));
            guruPanel.add(comboGuru);
            guruPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            panelAssign.add(guruPanel);
            panelAssign.add(Box.createRigidArea(new Dimension(0, 10)));
            panelAssign.add(new JLabel("Pilih Mapel (Ctrl+Klik untuk multi-pilih):"));
            panelAssign.add(scrollMapel);
            panelAssign.add(Box.createRigidArea(new Dimension(0, 10)));
            panelAssign.add(new JLabel("Pilih Kelas (Ctrl+Klik untuk multi-pilih):"));
            panelAssign.add(scrollKelas);

            // Show Dialog
            int result = JOptionPane.showConfirmDialog(this, panelAssign, "Assign Guru Mengajar (Multi-Select)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                Guru g = (Guru) comboGuru.getSelectedItem();
                List<MataPelajaran> selectedMapel = listMapel.getSelectedValuesList();
                List<Kelas> selectedKelas = listKelas.getSelectedValuesList();
                
                if (g != null && !selectedMapel.isEmpty() && !selectedKelas.isEmpty()) {
                    
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

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshMapelTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (MataPelajaran m : mapelRepo.getAll()) model.addRow(new Object[]{m.getIdMapel(), m.getNamaMapel(), m.getDeskripsi(), m.getTingkat()});
    }

private JPanel createPanelAssignment() {
    JPanel panel = new JPanel(new BorderLayout());
    
    String[] columns = {"Nama Guru", "NIP", "Mata Pelajaran yang Diajar", "Kelas yang Diajar"};
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    JTable table = new JTable(model);
    
    table.getColumnModel().getColumn(2).setPreferredWidth(250);
    table.getColumnModel().getColumn(3).setPreferredWidth(200);
    
    refreshAssignmentTable(model);
    
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    table.setRowSorter(sorter);
    
    JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
    searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    searchPanel.add(new JLabel(" Cari Guru: "), BorderLayout.WEST);
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
    
    btnRefresh.addActionListener(e -> refreshAssignmentTable(model));
    
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
            editGuruAssignment(guruSelected, model);
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
                refreshAssignmentTable(model);
                JOptionPane.showMessageDialog(this, "Assignment berhasil dihapus!");
            }
        }
    });
    
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    panel.add(btnPanel, BorderLayout.SOUTH);
    return panel;
}

private void refreshAssignmentTable(DefaultTableModel model) {
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

private void editGuruAssignment(Guru guru, DefaultTableModel model) {
    JDialog dialog = new JDialog(this, "Edit Assignment - " + guru.getNamaLengkap(), true);
    dialog.setSize(500, 400);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));
    
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Panel Mapel
    JPanel mapelPanel = new JPanel(new BorderLayout());
    mapelPanel.add(new JLabel("Mata Pelajaran yang Diajar:"), BorderLayout.NORTH);
    
    DefaultListModel<MataPelajaran> mapelListModel = new DefaultListModel<>();
    for (MataPelajaran m : mapelRepo.getAll()) {
        mapelListModel.addElement(m);
    }
    JList<MataPelajaran> listMapel = new JList<>(mapelListModel);
    listMapel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    listMapel.setCellRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof MataPelajaran) {
                MataPelajaran mp = (MataPelajaran) value;
                setText(mp.getNamaMapel() + " (Tingkat " + mp.getTingkat() + ")");
            }
            return this;
        }
    });
    
    List<Integer> selectedMapelIndices = new ArrayList<>();
    for (int i = 0; i < mapelListModel.size(); i++) {
        MataPelajaran mp = mapelListModel.get(i);
        if (guru.getMapelDiampu().stream().anyMatch(m -> m.getIdMapel().equals(mp.getIdMapel()))) {
            selectedMapelIndices.add(i);
        }
    }
    int[] arrMapel = selectedMapelIndices.stream().mapToInt(Integer::intValue).toArray();
    listMapel.setSelectedIndices(arrMapel);
    
    JScrollPane scrollMapel = new JScrollPane(listMapel);
    scrollMapel.setPreferredSize(new Dimension(400, 120));
    mapelPanel.add(scrollMapel, BorderLayout.CENTER);
    
    // Panel Kelas
    JPanel kelasPanel = new JPanel(new BorderLayout());
    kelasPanel.add(new JLabel("Kelas yang Diajar:"), BorderLayout.NORTH);
    
    DefaultListModel<Kelas> kelasListModel = new DefaultListModel<>();
    for (Kelas k : kelasRepo.getAll()) {
        kelasListModel.addElement(k);
    }
    JList<Kelas> listKelas = new JList<>(kelasListModel);
    listKelas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    listKelas.setCellRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Kelas) {
                setText(((Kelas) value).getNamaKelas());
            }
            return this;
        }
    });
    
    List<Integer> selectedKelasIndices = new ArrayList<>();
    for (int i = 0; i < kelasListModel.size(); i++) {
        Kelas k = kelasListModel.get(i);
        if (guru.getDaftarKelas().stream().anyMatch(kls -> kls.getIdKelas().equals(k.getIdKelas()))) {
            selectedKelasIndices.add(i);
        }
    }
    int[] arrKelas = selectedKelasIndices.stream().mapToInt(Integer::intValue).toArray();
    listKelas.setSelectedIndices(arrKelas);
    
    JScrollPane scrollKelas = new JScrollPane(listKelas);
    scrollKelas.setPreferredSize(new Dimension(400, 120));
    kelasPanel.add(scrollKelas, BorderLayout.CENTER);
    
    contentPanel.add(mapelPanel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    contentPanel.add(kelasPanel);
    
    // Button Panel
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnSimpan = new JButton("Simpan Perubahan");
    JButton btnBatal = new JButton("Batal");
    
    btnSimpan.addActionListener(e -> {
        guru.getMapelDiampu().clear();
        guru.getDaftarKelas().clear();
        
        List<MataPelajaran> selectedMapel = listMapel.getSelectedValuesList();
        List<Kelas> selectedKelas = listKelas.getSelectedValuesList();
        
        for (MataPelajaran m : selectedMapel) {
            guru.tambahMapel(m);
        }
        
        for (Kelas k : selectedKelas) {
            guru.tambahKelas(k);
            for (MataPelajaran m : selectedMapel) {
                kelasRepo.addMapelToKelas(k.getIdKelas(), m.getIdMapel());
            }
        }
        
        userRepo.updateGuru(guru);
        refreshAssignmentTable(model);
        dialog.dispose();
        JOptionPane.showMessageDialog(this, "Assignment berhasil diupdate!");
    });
    
    btnBatal.addActionListener(e -> dialog.dispose());
    
    btnPanel.add(btnSimpan);
    btnPanel.add(btnBatal);
    
    dialog.add(contentPanel, BorderLayout.CENTER);
    dialog.add(btnPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
}
}