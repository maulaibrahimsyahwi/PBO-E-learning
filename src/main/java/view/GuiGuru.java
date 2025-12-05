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
import java.io.FileWriter;
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

    private JComboBox<Kelas> comboKelas;
    private JComboBox<MataPelajaran> comboMapel;
    private JTabbedPane tabbedContent;

    public GuiGuru(Guru guru, MateriRepository mr, TugasRepository tr, UjianRepository ur,
                   JawabanRepository jr, NilaiRepository nr, KelasRepository kr, 
                   MapelRepository mapelRepo, ForumRepository fr, UserRepository uRepo) {
        this.guru = guru;
        this.materiRepo = mr;
        this.tugasRepo = tr;
        this.ujianRepo = ur;
        this.jawabanRepo = jr;
        this.nilaiRepo = nr;
        this.forumRepo = fr;
        this.userRepo = uRepo;

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
                        
                        File folder = new File("src/main/java/data/uploads");
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

    private JPanel createNilaiPanel(Kelas k, MataPelajaran m) {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID Jawaban", "Tipe", "Judul Soal", "Siswa", "File Jawaban", "Nilai Saat Ini"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
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
                model.addRow(new Object[]{
                    j.getIdJawaban(), 
                    "Tugas", 
                    t.getJudul(), 
                    j.getSiswa().getNamaLengkap(), 
                    j.getFileJawaban(), 
                    nilaiStr
                });
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
                model.addRow(new Object[]{
                    j.getIdJawaban(),
                    "Ujian",
                    u.getJenisUjian(),
                    j.getSiswa().getNamaLengkap(),
                    j.getFileJawaban(),
                    nilaiStr
                });
            }
        }

        JPanel btnPanel = new JPanel();
        
        JButton btnNilai = new JButton("Beri Nilai (Pilih Baris)");
        btnNilai.addActionListener(e -> {
             int row = table.getSelectedRow();
             if (row == -1) {
                 JOptionPane.showMessageDialog(this, "Pilih baris siswa yang ingin dinilai terlebih dahulu!");
                 return;
             }

             String idJawaban = (String) table.getValueAt(row, 0);
             String namaSiswa = (String) table.getValueAt(row, 3);
             String judulSoal = (String) table.getValueAt(row, 2);

             Jawaban selectedJawab = null;
             for(Jawaban j : jawabanRepo.getAll()) {
                 if(j.getIdJawaban().equals(idJawaban)) {
                     selectedJawab = j;
                     break;
                 }
             }

             if (selectedJawab != null) {
                 String input = JOptionPane.showInputDialog(this, "Masukkan Nilai untuk " + namaSiswa + " (" + judulSoal + "):");
                 if (input != null && !input.isBlank()) {
                     try {
                         int nilaiAngka = Integer.parseInt(input);
                         String ket = (nilaiAngka >= 75) ? "Lulus" : "Remidi";
                         
                         Nilai n;
                         if (selectedJawab.getTugas() != null) {
                             n = new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getTugas(), nilaiAngka, ket);
                         } else {
                             n = new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getUjian(), nilaiAngka, ket);
                         }
                         
                         nilaiRepo.addNilai(n);
                         selectedJawab.getSiswa().tambahNilai(n); 
                         
                         JOptionPane.showMessageDialog(this, "Nilai berhasil disimpan!");
                         loadDashboard(); 
                     } catch (NumberFormatException ex) {
                         JOptionPane.showMessageDialog(this, "Nilai harus angka!");
                     }
                 }
             }
        });

        JButton btnExport = new JButton("Export ke CSV");
        btnExport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan Rekap Nilai");
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
                }
                
                try (FileWriter fw = new FileWriter(fileToSave)) {
                    fw.write("ID Jawaban;Tipe;Judul Soal;Nama Siswa;Nilai;Keterangan\n");
                    
                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            Object val = table.getValueAt(i, j);
                            fw.write((val != null ? val.toString() : "-") + ";");
                        }
                        fw.write("\n");
                    }
                    JOptionPane.showMessageDialog(this, "Data berhasil diexport ke: " + fileToSave.getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error export: " + ex.getMessage());
                }
            }
        });

        btnPanel.add(btnNilai);
        btnPanel.add(btnExport);

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