package view;

import model.*;
import repository.*;
import utils.IdUtil;
import utils.DateUtil;
// Hapus import SecurityUtil
import view.component.ForumPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List; // Import List

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
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Kelas:"));
        comboKelas = new JComboBox<>();
        for(Kelas k : guru.getDaftarKelas()) comboKelas.addItem(k);
        topPanel.add(comboKelas);

        topPanel.add(new JLabel("Mapel:"));
        comboMapel = new JComboBox<>();
        for(MataPelajaran m : guru.getMapelDiampu()) comboMapel.addItem(m);
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
        DefaultTableModel model = new DefaultTableModel(new String[]{"Siswa", "File Jawaban", "Nilai"}, 0);
        JTable table = new JTable(model);

        List<Tugas> listTugas = tugasRepo.getByMapelAndKelas(m, k);
        for(Tugas t : listTugas) {
            for(Jawaban j : jawabanRepo.findByTugas(t.getIdTugas())) {
                String nilaiStr = "-";
                for(Nilai n : nilaiRepo.getAll()) {
                    if(n.getTugas() != null && n.getTugas().equals(t) && n.getSiswa().equals(j.getSiswa())) 
                        nilaiStr = String.valueOf(n.getNilaiAngka());
                }
                model.addRow(new Object[]{j.getSiswa().getNamaLengkap(), j.getFileJawaban(), nilaiStr});
            }
        }

        JButton btnNilai = new JButton("Beri Nilai (via ID Siswa & ID Tugas)");
        btnNilai.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "Gunakan fitur ini di menu CLI untuk saat ini.");
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
            // HAPUS HASH: Simpan password biasa
            guru.setPassword(newPass);
            userRepo.saveToFile();
            JOptionPane.showMessageDialog(this, "Password berhasil diubah.");
        }
    }
}