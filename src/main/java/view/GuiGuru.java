package view;

import model.*;
import repository.*;
import utils.IdUtil;
import utils.DateUtil;
import view.component.ForumPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

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

    private JComboBox<Kelas> comboKelas;
    private JComboBox<MataPelajaran> comboMapel;
    private JTabbedPane tabbedContent;

    public GuiGuru(Guru guru, MateriRepository mr, TugasRepository tr, UjianRepository ur,
                   JawabanRepository jr, NilaiRepository nr, KelasRepository kr, 
                   MapelRepository mapelRepo, ForumRepository fr, UserRepository uRepo,
                   SoalRepository soalRepo) {
        this.guru = guru;
        this.materiRepo = mr;
        this.tugasRepo = tr;
        this.ujianRepo = ur;
        this.jawabanRepo = jr;
        this.nilaiRepo = nr;
        this.forumRepo = fr;
        this.userRepo = uRepo;
        this.soalRepo = soalRepo;

        setTitle("Dashboard Guru - " + guru.getNamaLengkap());
        setSize(1000, 650); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Kelas:"));
        comboKelas = new JComboBox<>();
        for(Kelas k : guru.getDaftarKelas()) comboKelas.addItem(k);
        
        comboKelas.setRenderer(new DefaultListCellRenderer() {
            @Override
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
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MataPelajaran) setText(((MataPelajaran)value).getNamaMapel());
                return this;
            }
        });
        topPanel.add(comboMapel);

        JButton btnLoad = new JButton("Buka Kelas");
        topPanel.add(btnLoad);
        
        JButton btnProfil = new JButton("Profil");
        topPanel.add(btnProfil);
        
        JButton btnLogout = new JButton("Logout");
        topPanel.add(btnLogout);

        add(topPanel, BorderLayout.NORTH);

        tabbedContent = new JTabbedPane();
        add(tabbedContent, BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadDashboard());
        btnProfil.addActionListener(e -> showProfil());
        
        btnLogout.addActionListener(e -> {
            dispose();
            JOptionPane.showMessageDialog(this, "Silakan jalankan ulang aplikasi untuk login kembali.");
            System.exit(0); 
        });
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

    private JPanel createMateriPanel(Kelas k, MataPelajaran m) {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Judul", "File"}, 0);
        JTable table = new JTable(model);
        
        for(Materi mat : materiRepo.getByMapelAndKelas(m, k)) 
            model.addRow(new Object[]{mat.getIdMateri(), mat.getJudul(), mat.getFileMateri()});
        
        JButton btnAdd = new JButton("Tambah Materi (Upload)");
        btnAdd.addActionListener(e -> {
            JTextField txtJudul = new JTextField();
            JTextField txtDesk = new JTextField();
            Object[] message = { "Judul Materi:", txtJudul, "Deskripsi:", txtDesk };

            int option = JOptionPane.showConfirmDialog(this, message, "Tambah Materi", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                int select = fileChooser.showOpenDialog(this);
                
                if (select == JFileChooser.APPROVE_OPTION) {
                    try {
                        File fileAsli = fileChooser.getSelectedFile();
                        String namaFileBaru = System.currentTimeMillis() + "_" + fileAsli.getName();
                        
                        File folder = new File("data/uploads");
                        if (!folder.exists()) folder.mkdirs();
                        
                        File dest = new File(folder, namaFileBaru);
                        Files.copy(fileAsli.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        
                        Materi mat = new Materi(IdUtil.generate(), txtJudul.getText(), txtDesk.getText(), namaFileBaru);
                        mat.setGuru(guru); mat.setKelas(k); mat.setMapel(m);
                        materiRepo.addMateri(mat);
                        
                        JOptionPane.showMessageDialog(this, "Upload Berhasil!");
                        loadDashboard();
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Gagal upload: " + ex.getMessage());
                    }
                }
            }
        });
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTugasPanel(Kelas k, MataPelajaran m) {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Judul", "Deadline"}, 0);
        JTable table = new JTable(model);
        
        List<Tugas> listTugas = tugasRepo.getByMapelAndKelas(m, k);
        for(Tugas t : listTugas) 
            model.addRow(new Object[]{t.getIdTugas(), t.getJudul(), t.getDeadline()});
        
        JButton btnAdd = new JButton("Buat Tugas");
        btnAdd.addActionListener(e -> {
            try {
                String judul = JOptionPane.showInputDialog("Judul:");
                String desk = JOptionPane.showInputDialog("Deskripsi:");
                String tgl = JOptionPane.showInputDialog("Deadline (yyyy-MM-dd):");
                if (judul != null) {
                    Tugas t = new Tugas(IdUtil.generate(), judul, desk, DateUtil.parse(tgl));
                    t.setGuru(guru); t.setKelas(k); t.setMapel(m);
                    tugasRepo.addTugas(t);
                    loadDashboard();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Format salah."); }
        });
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createUjianPanel(Kelas k, MataPelajaran m) {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Jenis", "Tanggal", "Durasi"}, 0);
        JTable table = new JTable(model);
        
        for(Ujian u : ujianRepo.getByMapelAndKelas(m, k)) {
            model.addRow(new Object[]{u.getIdUjian(), u.getJenisUjian(), u.getTanggal(), u.getDurasi()});
        }

        JButton btnAdd = new JButton("Buat Ujian & Soal");
        btnAdd.addActionListener(e -> tambahUjian(guru, k, m));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);
        return panel;
    }

    private void tambahUjian(Guru guru, Kelas kelas, MataPelajaran mapel) {
        JDialog d = new JDialog(this, "Buat Ujian Baru", true);
        d.setSize(400, 350);
        d.setLayout(new GridLayout(6, 2, 10, 10));
        d.setLocationRelativeTo(this);

        JTextField txtJenis = new JTextField();
        JTextField txtTgl = new JTextField();
        JTextField txtDurasi = new JTextField();
        
        txtDurasi.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validate(); }
            public void removeUpdate(DocumentEvent e) { validate(); }
            public void changedUpdate(DocumentEvent e) { validate(); }
            void validate() {
                if (!txtDurasi.getText().matches("\\d+")) {
                    txtDurasi.putClientProperty("JComponent.outline", "error");
                    txtDurasi.setToolTipText("Hanya angka!");
                } else {
                    txtDurasi.putClientProperty("JComponent.outline", null);
                }
            }
        });

        d.add(new JLabel("Jenis (UTS/UAS/Kuis):")); d.add(txtJenis);
        d.add(new JLabel("Tanggal (yyyy-MM-dd):")); d.add(txtTgl);
        d.add(new JLabel("Durasi (menit):")); d.add(txtDurasi);

        JButton btnSimpan = new JButton("Simpan & Lanjut Buat Soal");
        btnSimpan.addActionListener(e -> {
            try {
                String idUjian = IdUtil.generate();
                Ujian ujian = new Ujian(idUjian, txtJenis.getText(), DateUtil.parse(txtTgl.getText()), Integer.parseInt(txtDurasi.getText()));
                ujian.setGuru(guru);
                ujian.setMapel(mapel);
                ujian.setKelas(kelas);

                ujianRepo.addUjian(ujian);
                
                d.dispose();
                kelolaSoal(idUjian); 
                loadDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Format input salah: " + ex.getMessage());
            }
        });
        
        d.add(new JLabel("")); d.add(btnSimpan);
        d.setVisible(true);
    }

    private void kelolaSoal(String idUjian) {
        JDialog d = new JDialog(this, "Input Soal Pilihan Ganda", true);
        d.setSize(500, 500);
        d.setLayout(new GridLayout(8, 2, 5, 5));
        d.setLocationRelativeTo(this);

        JTextField txtTanya = new JTextField();
        JTextField txtA = new JTextField();
        JTextField txtB = new JTextField();
        JTextField txtC = new JTextField();
        JTextField txtD = new JTextField();
        JComboBox<String> comboKunci = new JComboBox<>(new String[]{"A", "B", "C", "D"});

        JButton btnAdd = new JButton("Simpan Soal");
        btnAdd.addActionListener(e -> {
            if(txtTanya.getText().isBlank()) return;
            
            Soal s = new Soal(IdUtil.generate(), idUjian, 
                              txtTanya.getText(), txtA.getText(), txtB.getText(), txtC.getText(), txtD.getText(), 
                              (String)comboKunci.getSelectedItem());
            soalRepo.addSoal(s);
            JOptionPane.showMessageDialog(d, "Soal Tersimpan!");
            
            txtTanya.setText(""); txtA.setText(""); txtB.setText(""); txtC.setText(""); txtD.setText("");
        });

        d.add(new JLabel("Pertanyaan:")); d.add(txtTanya);
        d.add(new JLabel("Opsi A:")); d.add(txtA);
        d.add(new JLabel("Opsi B:")); d.add(txtB);
        d.add(new JLabel("Opsi C:")); d.add(txtC);
        d.add(new JLabel("Opsi D:")); d.add(txtD);
        d.add(new JLabel("Kunci Jawaban:")); d.add(comboKunci);
        d.add(new JLabel("")); d.add(btnAdd);
        
        d.setVisible(true);
    }

    private JPanel createNilaiPanel(Kelas k, MataPelajaran m) {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID Jawaban", "Tipe", "Judul Soal", "Siswa", "File Jawaban", "Nilai Saat Ini"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);

        List<Tugas> listTugas = tugasRepo.getByMapelAndKelas(m, k);
        for(Tugas t : listTugas) {
            for(Jawaban j : jawabanRepo.findByTugas(t.getIdTugas())) {
                String nilaiStr = "Belum Dinilai";
                for(Nilai n : nilaiRepo.getAll()) {
                    if(n.getTugas() != null && n.getTugas().equals(t) && n.getSiswa().equals(j.getSiswa())) 
                        nilaiStr = String.valueOf(n.getNilaiAngka());
                }
                model.addRow(new Object[]{j.getIdJawaban(), "Tugas", t.getJudul(), j.getSiswa().getNamaLengkap(), j.getFileJawaban(), nilaiStr});
            }
        }

        List<Ujian> listUjian = ujianRepo.getByMapelAndKelas(m, k);
        for(Ujian u : listUjian) {
            for(Jawaban j : jawabanRepo.findByUjian(u.getIdUjian())) {
                String nilaiStr = "Belum Dinilai";
                for(Nilai n : nilaiRepo.getAll()) {
                    if(n.getUjian() != null && n.getUjian().equals(u) && n.getSiswa().equals(j.getSiswa()))
                        nilaiStr = String.valueOf(n.getNilaiAngka());
                }
                model.addRow(new Object[]{j.getIdJawaban(), "Ujian", u.getJenisUjian(), j.getSiswa().getNamaLengkap(), j.getFileJawaban(), nilaiStr});
            }
        }

        JButton btnNilai = new JButton("Beri Nilai Manual");
        btnNilai.addActionListener(e -> {
             int row = table.getSelectedRow();
             if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih baris siswa!"); return; }

             String idJawaban = (String) table.getValueAt(row, 0);
             Jawaban selectedJawab = jawabanRepo.getAll().stream().filter(j->j.getIdJawaban().equals(idJawaban)).findFirst().orElse(null);

             if (selectedJawab != null) {
                 String input = JOptionPane.showInputDialog(this, "Masukkan Nilai:");
                 if (input != null) {
                     try {
                         int nilaiAngka = Integer.parseInt(input);
                         Nilai n = (selectedJawab.getTugas() != null) ? 
                             new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getTugas(), nilaiAngka, "Manual") :
                             new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getUjian(), nilaiAngka, "Manual");
                         
                         nilaiRepo.addNilai(n);
                         selectedJawab.getSiswa().tambahNilai(n);
                         JOptionPane.showMessageDialog(this, "Tersimpan!");
                         loadDashboard();
                     } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Input harus angka!"); }
                 }
             }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnNilai, BorderLayout.SOUTH);
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