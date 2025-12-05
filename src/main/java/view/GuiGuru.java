package view;

import model.*;
import repository.*;
import utils.IdUtil;
import utils.DateUtil;
import view.component.ForumPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class GuiGuru extends JFrame {
    private Guru guru;
    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;
    private ForumRepository forumRepo;
    private UserRepository userRepo;
    private SoalRepository soalRepo;
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;
    private AbsensiRepository absensiRepo;

    private JComboBox<Kelas> comboKelas;
    private JComboBox<MataPelajaran> comboMapel;
    private JTabbedPane tabbedContent;

    public GuiGuru(Guru guru, MateriRepository mr, TugasRepository tr, UjianRepository ur,
                   JawabanRepository jr, NilaiRepository nr, KelasRepository kr, 
                   MapelRepository mapelRepo, ForumRepository fr, UserRepository uRepo,
                   SoalRepository soalRepo, AbsensiRepository absensiRepo) {
        this.guru = guru;
        this.materiRepo = mr;
        this.tugasRepo = tr;
        this.ujianRepo = ur;
        this.jawabanRepo = jr;
        this.nilaiRepo = nr;
        this.kelasRepo = kr;
        this.mapelRepo = mapelRepo;
        this.forumRepo = fr;
        this.userRepo = uRepo;
        this.soalRepo = soalRepo;
        this.absensiRepo = absensiRepo;

        setTitle("Dashboard Guru - " + guru.getNamaLengkap());
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        topPanel.add(new JLabel("Kelas:"));
        comboKelas = new JComboBox<>();
        for(Kelas k : guru.getDaftarKelas()) comboKelas.addItem(k);
        comboKelas.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Kelas) setText(((Kelas)value).getNamaKelas());
                return this;
            }
        });
        topPanel.add(comboKelas);

        topPanel.add(new JLabel("Mapel:"));
        comboMapel = new JComboBox<>();
        for(MataPelajaran m : guru.getMapelDiampu()) comboMapel.addItem(m);
        comboMapel.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MataPelajaran) setText(((MataPelajaran)value).getNamaMapel());
                return this;
            }
        });
        topPanel.add(comboMapel);

        JButton btnLoad = new JButton("Buka Kelas");
        topPanel.add(btnLoad);
        
        // Tombol logout dan profil di panel terpisah agar rapi di kanan (opsional, tapi di sini saya biarkan di flow kiri sesuai asli)
        JButton btnProfil = new JButton("Profil");
        topPanel.add(btnProfil);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(255, 100, 100));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Logout", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                dispose();
                new GuiLogin(userRepo, kelasRepo, mapelRepo, materiRepo, tugasRepo, 
                             ujianRepo, jawabanRepo, nilaiRepo, forumRepo, 
                             absensiRepo, soalRepo).setVisible(true);
            }
        });
        topPanel.add(btnLogout);

        add(topPanel, BorderLayout.NORTH);

        tabbedContent = new JTabbedPane();
        add(tabbedContent, BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadDashboard());
        btnProfil.addActionListener(e -> showProfil());
    }

    private void loadDashboard() {
        tabbedContent.removeAll();
        Kelas k = (Kelas) comboKelas.getSelectedItem();
        MataPelajaran m = (MataPelajaran) comboMapel.getSelectedItem();

        if (k == null || m == null) {
            JOptionPane.showMessageDialog(this, "Pilih Kelas dan Mapel dulu.");
            return;
        }

        tabbedContent.addTab("Materi", createMateriPanel(k, m));
        tabbedContent.addTab("Tugas", createTugasPanel(k, m));
        tabbedContent.addTab("Ujian & Kuis", createUjianPanel(k, m));
        tabbedContent.addTab("Penilaian", createNilaiPanel(k, m));
        tabbedContent.addTab("Forum", createForumPanel(k, m));
    }

    // --- PERBAIKAN TATA LETAK TOMBOL UJIAN ---
    private JPanel createUjianPanel(Kelas k, MataPelajaran m) {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nama", "Tipe", "Tanggal", "Info"}, 0);
        JTable table = new JTable(model);
        
        for(Ujian u : ujianRepo.getByMapelAndKelas(m, k)) {
            String info = u.getTipeUjian().equals("KUIS") ? 
                          u.getWaktuPerSoal() + "s/soal" : u.getDurasiTotal() + " menit";
            model.addRow(new Object[]{u.getIdUjian(), u.getNamaUjian(), u.getTipeUjian(), u.getTanggal(), info});
        }

        // Tata letak tombol diperbaiki
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton btnAdd = new JButton("Buat Ujian & Soal");
        btnAdd.setPreferredSize(new Dimension(150, 35));
        
        btnPanel.add(btnAdd);
        btnAdd.addActionListener(e -> tambahUjian(guru, k, m));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void tambahUjian(Guru guru, Kelas kelas, MataPelajaran mapel) {
        JDialog d = new JDialog(this, "Buat Ujian Baru", true);
        d.setSize(450, 550);
        d.setLayout(new GridLayout(8, 2, 10, 10));
        d.setLocationRelativeTo(this);

        JTextField txtNama = new JTextField();
        String[] types = {"Pilihan Ganda (PG)", "Essay", "Campuran (Hybrid)", "Kuis (Timer per Soal)"};
        JComboBox<String> comboTipe = new JComboBox<>(types);
        
        JTextField txtTgl = new JTextField(); 
        JTextField txtDurasi = new JTextField("60"); 
        JTextField txtWaktuPerSoal = new JTextField("0"); 
        JTextField txtMaxSoal = new JTextField("10"); 
        
        txtWaktuPerSoal.setEnabled(false);

        comboTipe.addActionListener(e -> {
            if (comboTipe.getSelectedIndex() == 3) { // Kuis
                txtWaktuPerSoal.setEnabled(true);
                txtDurasi.setEnabled(false); txtDurasi.setText("0");
            } else {
                txtWaktuPerSoal.setEnabled(false); txtWaktuPerSoal.setText("0");
                txtDurasi.setEnabled(true);
            }
        });

        d.add(new JLabel("Nama Ujian:")); d.add(txtNama);
        d.add(new JLabel("Tipe Ujian:")); d.add(comboTipe);
        d.add(new JLabel("Tanggal (yyyy-MM-dd):")); d.add(txtTgl);
        d.add(new JLabel("Durasi Total (menit):")); d.add(txtDurasi);
        d.add(new JLabel("Waktu Per Soal (detik - Kuis):")); d.add(txtWaktuPerSoal);
        d.add(new JLabel("Maksimal Soal:")); d.add(txtMaxSoal);

        JButton btnSimpan = new JButton("Simpan & Lanjut Buat Soal");
        btnSimpan.addActionListener(e -> {
            try {
                String idUjian = IdUtil.generate();
                String rawTipe = (String) comboTipe.getSelectedItem();
                String kodeTipe = "PG";
                if(rawTipe.contains("Essay")) kodeTipe = "ESSAY";
                else if(rawTipe.contains("Campuran")) kodeTipe = "HYBRID";
                else if(rawTipe.contains("Kuis")) kodeTipe = "KUIS";

                Ujian ujian = new Ujian(
                    idUjian, txtNama.getText(), kodeTipe,
                    DateUtil.parse(txtTgl.getText()), 
                    Integer.parseInt(txtDurasi.getText()),
                    Integer.parseInt(txtWaktuPerSoal.getText()),
                    Integer.parseInt(txtMaxSoal.getText())
                );
                ujian.setGuru(guru); ujian.setMapel(mapel); ujian.setKelas(kelas);

                ujianRepo.addUjian(ujian);
                d.dispose();
                kelolaSoal(ujian); 
                loadDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Format input salah: " + ex.getMessage());
            }
        });
        
        d.add(new JLabel("")); d.add(btnSimpan);
        d.setVisible(true);
    }

    private void kelolaSoal(Ujian u) {
        JDialog d = new JDialog(this, "Input Soal (" + u.getTipeUjian() + ")", true);
        d.setSize(600, 600);
        d.setLayout(new BorderLayout(10, 10));
        d.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JComboBox<String> comboJenisSoal = new JComboBox<>(new String[]{"PG", "ESSAY"});
        
        if(u.getTipeUjian().equals("PG") || u.getTipeUjian().equals("KUIS")) {
            comboJenisSoal.setSelectedItem("PG"); comboJenisSoal.setEnabled(false);
        } else if (u.getTipeUjian().equals("ESSAY")) {
            comboJenisSoal.setSelectedItem("ESSAY"); comboJenisSoal.setEnabled(false);
        }

        JTextField txtTanya = new JTextField();
        JTextField txtA = new JTextField();
        JTextField txtB = new JTextField();
        JTextField txtC = new JTextField();
        JTextField txtD = new JTextField();
        
        JPanel panelKunci = new JPanel(new CardLayout());
        JComboBox<String> comboKunciPG = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        JTextField txtKunciEssay = new JTextField();
        panelKunci.add(comboKunciPG, "PG");
        panelKunci.add(txtKunciEssay, "ESSAY");
        CardLayout cl = (CardLayout)(panelKunci.getLayout());

        comboJenisSoal.addActionListener(e -> {
            boolean isPG = comboJenisSoal.getSelectedItem().equals("PG");
            txtA.setEnabled(isPG); txtB.setEnabled(isPG);
            txtC.setEnabled(isPG); txtD.setEnabled(isPG);
            cl.show(panelKunci, isPG ? "PG" : "ESSAY");
        });
        
        if(u.getTipeUjian().equals("ESSAY")) {
            txtA.setEnabled(false); txtB.setEnabled(false); 
            txtC.setEnabled(false); txtD.setEnabled(false);
            cl.show(panelKunci, "ESSAY");
        }

        inputPanel.add(new JLabel("Tipe Soal:")); inputPanel.add(comboJenisSoal);
        inputPanel.add(new JLabel("Pertanyaan:")); inputPanel.add(txtTanya);
        inputPanel.add(new JLabel("Opsi A:")); inputPanel.add(txtA);
        inputPanel.add(new JLabel("Opsi B:")); inputPanel.add(txtB);
        inputPanel.add(new JLabel("Opsi C:")); inputPanel.add(txtC);
        inputPanel.add(new JLabel("Opsi D:")); inputPanel.add(txtD);
        inputPanel.add(new JLabel("Kunci Jawaban:")); inputPanel.add(panelKunci);

        JButton btnAdd = new JButton("Simpan Soal");
        JLabel lblInfo = new JLabel("Soal tersimpan: 0 / " + u.getMaxSoal());
        
        btnAdd.addActionListener(e -> {
            int currentCount = soalRepo.getByUjian(u.getIdUjian()).size();
            if (currentCount >= u.getMaxSoal()) {
                JOptionPane.showMessageDialog(d, "Maksimal soal tercapai!");
                return;
            }
            if(txtTanya.getText().isBlank()) return;
            
            String tipe = (String) comboJenisSoal.getSelectedItem();
            String kunci = tipe.equals("PG") ? (String) comboKunciPG.getSelectedItem() : txtKunciEssay.getText();
            
            Soal s = new Soal(IdUtil.generate(), u.getIdUjian(), tipe,
                              txtTanya.getText(), txtA.getText(), txtB.getText(), txtC.getText(), txtD.getText(), 
                              kunci);
            soalRepo.addSoal(s);
            
            txtTanya.setText(""); txtA.setText(""); txtB.setText(""); txtC.setText(""); txtD.setText(""); txtKunciEssay.setText("");
            lblInfo.setText("Soal tersimpan: " + (currentCount + 1) + " / " + u.getMaxSoal());
            JOptionPane.showMessageDialog(d, "Soal tersimpan!");
        });

        d.add(inputPanel, BorderLayout.CENTER);
        
        // Panel tombol dialog
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(lblInfo); 
        bottom.add(btnAdd);
        d.add(bottom, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // --- PERBAIKAN TATA LETAK TOMBOL MATERI ---
    private JPanel createMateriPanel(Kelas k, MataPelajaran m) {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Judul", "File"}, 0);
        JTable table = new JTable(model);
        for(Materi mat : materiRepo.getByMapelAndKelas(m, k)) 
            model.addRow(new Object[]{mat.getIdMateri(), mat.getJudul(), mat.getFileMateri()});
        
        // Tata letak tombol diperbaiki
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton btnAdd = new JButton("Tambah Materi (Upload)");
        btnAdd.setPreferredSize(new Dimension(180, 35));
        
        btnPanel.add(btnAdd);
        
        btnAdd.addActionListener(e -> {
            JTextField txtJudul = new JTextField();
            JTextField txtDesk = new JTextField();
            Object[] message = { "Judul Materi:", txtJudul, "Deskripsi:", txtDesk };

            if (JOptionPane.showConfirmDialog(this, message, "Tambah Materi", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File fileAsli = fileChooser.getSelectedFile();
                        String namaFile = fileAsli.getName();
                        
                        Materi mat = new Materi(IdUtil.generate(), txtJudul.getText(), txtDesk.getText(), namaFile);
                        mat.setGuru(guru); 
                        mat.setKelas(k); 
                        mat.setMapel(m);
                        
                        materiRepo.addMateri(mat, fileAsli); 
                        
                        loadDashboard();
                        JOptionPane.showMessageDialog(this, "Berhasil upload ke Database!");
                        
                    } catch (Exception ex) { 
                        JOptionPane.showMessageDialog(this, "Gagal upload: " + ex.getMessage()); 
                    }
                }
            }
        });
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- PERBAIKAN TATA LETAK TOMBOL TUGAS ---
    private JPanel createTugasPanel(Kelas k, MataPelajaran m) {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Judul", "Deadline"}, 0);
        JTable table = new JTable(model);
        for(Tugas t : tugasRepo.getByMapelAndKelas(m, k)) model.addRow(new Object[]{t.getIdTugas(), t.getJudul(), t.getDeadline()});
        
        // Tata letak tombol diperbaiki
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton btnAdd = new JButton("Buat Tugas");
        btnAdd.setPreferredSize(new Dimension(120, 35));
        
        btnPanel.add(btnAdd);
        
        btnAdd.addActionListener(e -> {
            String judul = JOptionPane.showInputDialog("Judul:");
            String desk = JOptionPane.showInputDialog("Deskripsi:");
            String tgl = JOptionPane.showInputDialog("Deadline (yyyy-MM-dd):");
            if (judul != null) {
                try {
                    Tugas t = new Tugas(IdUtil.generate(), judul, desk, DateUtil.parse(tgl));
                    t.setGuru(guru); t.setKelas(k); t.setMapel(m);
                    tugasRepo.addTugas(t);
                    loadDashboard();
                } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Format tanggal salah"); }
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- PERBAIKAN TATA LETAK TOMBOL PENILAIAN ---
    private JPanel createNilaiPanel(Kelas k, MataPelajaran m) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID Jawaban", "Tipe", "Judul Soal", "Siswa", "File Jawaban", "Nilai"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int row, int column) { return false; } };
        JTable table = new JTable(model);

        for(Tugas t : tugasRepo.getByMapelAndKelas(m, k)) {
            for(Jawaban j : jawabanRepo.findByTugas(t.getIdTugas())) {
                String nilaiStr = "Belum Dinilai";
                for(Nilai n : nilaiRepo.getAll()) {
                    if(n.getTugas() != null && n.getTugas().equals(t) && n.getSiswa().equals(j.getSiswa())) nilaiStr = String.valueOf(n.getNilaiAngka());
                }
                model.addRow(new Object[]{j.getIdJawaban(), "Tugas", t.getJudul(), j.getSiswa().getNamaLengkap(), j.getFileJawaban(), nilaiStr});
            }
        }
        for(Ujian u : ujianRepo.getByMapelAndKelas(m, k)) {
            for(Jawaban j : jawabanRepo.findByUjian(u.getIdUjian())) {
                String nilaiStr = "Belum Dinilai";
                for(Nilai n : nilaiRepo.getAll()) {
                    if(n.getUjian() != null && n.getUjian().equals(u) && n.getSiswa().equals(j.getSiswa())) nilaiStr = String.valueOf(n.getNilaiAngka());
                }
                model.addRow(new Object[]{j.getIdJawaban(), "Ujian", u.getNamaUjian(), j.getSiswa().getNamaLengkap(), j.getFileJawaban(), nilaiStr});
            }
        }

        // Tata letak tombol diperbaiki
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton btnNilai = new JButton("Beri Nilai Manual");
        btnNilai.setPreferredSize(new Dimension(150, 35));
        
        btnPanel.add(btnNilai);
        
        btnNilai.addActionListener(e -> {
             int row = table.getSelectedRow();
             if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih baris!"); return; }
             String idJawaban = (String) table.getValueAt(row, 0);
             Jawaban selectedJawab = jawabanRepo.getAll().stream().filter(j->j.getIdJawaban().equals(idJawaban)).findFirst().orElse(null);
             if (selectedJawab != null) {
                 String input = JOptionPane.showInputDialog(this, "Masukkan Nilai:");
                 if (input != null) {
                     try {
                         int val = Integer.parseInt(input);
                         Nilai n = (selectedJawab.getTugas()!=null) ? new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getTugas(), val, "Manual") 
                                                                    : new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getUjian(), val, "Manual");
                         nilaiRepo.addNilai(n);
                         selectedJawab.getSiswa().tambahNilai(n);
                         loadDashboard();
                     } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Input angka!"); }
                 }
             }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createForumPanel(Kelas k, MataPelajaran m) {
        return new ForumPanel(guru, k, m, forumRepo);
    }

    private void showProfil() {
        String newPass = JOptionPane.showInputDialog("Ganti Password (Kosongkan jika batal):");
        if(newPass != null && !newPass.isBlank()) {
            guru.setPassword(newPass);
            userRepo.saveToFile();
            JOptionPane.showMessageDialog(this, "Password berhasil diubah.");
        }
    }
}