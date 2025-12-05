package view;

import model.*;
import repository.*;
import utils.IdUtil;
import view.component.ForumPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class GuiSiswa extends JFrame {
    private Siswa siswa;
    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;
    private ForumRepository forumRepo;
    private UserRepository userRepo;

    public GuiSiswa(Siswa s, MateriRepository mr, TugasRepository tr, UjianRepository ur,
                    JawabanRepository jr, NilaiRepository nr, ForumRepository fr, UserRepository uRepo) {
        this.siswa = s;
        this.materiRepo = mr;
        this.tugasRepo = tr;
        this.ujianRepo = ur;
        this.jawabanRepo = jr;
        this.nilaiRepo = nr;
        this.forumRepo = fr;
        this.userRepo = uRepo;

        setTitle("Dashboard Siswa - " + s.getNamaLengkap());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Materi", createMateriPanel());
        tabs.addTab("Tugas", createTugasPanel());
        tabs.addTab("Nilai", createNilaiPanel());
        tabs.addTab("Forum", createForumPanel());
        add(tabs, BorderLayout.CENTER);

        JButton btnProfil = new JButton("Profil / Ubah Password");
        btnProfil.addActionListener(e -> showProfil());
        add(btnProfil, BorderLayout.NORTH);
    }

    private JPanel createMateriPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Mapel", "Judul", "Deskripsi", "File"}, 0);
        JTable table = new JTable(model);
        if(siswa.getKelas() != null) {
            for(Materi m : materiRepo.getByKelas(siswa.getKelas())) {
                model.addRow(new Object[]{m.getMapel().getNamaMapel(), m.getJudul(), m.getDeskripsi(), m.getFileMateri()});
            }
        }
        
        JButton btnOpen = new JButton("Buka File Materi");
        btnOpen.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih materi terlebih dahulu!");
                return;
            }
            String filename = (String) table.getValueAt(row, 3);
            try {
                File file = new File("src/main/java/data/uploads/" + filename);
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(this, "File tidak ditemukan: " + file.getAbsolutePath());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal membuka file: " + ex.getMessage());
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnOpen, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTugasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Kolom 0 adalah ID (Hidden logic bisa diterapkan, tapi disini ditampilkan saja)
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Mapel", "Judul", "Deadline"}, 0);
        JTable table = new JTable(model);
        if(siswa.getKelas() != null) {
            for(Tugas t : tugasRepo.getByKelas(siswa.getKelas())) {
                model.addRow(new Object[]{t.getIdTugas(), t.getMapel().getNamaMapel(), t.getJudul(), t.getDeadline()});
            }
        }

        // PERBAIKAN: SUBMIT DENGAN MEMILIH TABEL, BUKAN KETIK ID
        JButton btnSubmit = new JButton("Submit Jawaban (Pilih Tugas di Tabel)");
        btnSubmit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Silakan pilih tugas pada tabel terlebih dahulu!");
                return;
            }

            // Ambil ID Tugas dari kolom 0
            String idTugas = (String) table.getValueAt(row, 0);
            
            // Cari Object Tugas
            Tugas tFound = null;
            for(Tugas t : tugasRepo.getAll()) {
                if(t.getIdTugas().equals(idTugas)) { tFound = t; break; }
            }

            if(tFound != null) {
                // Gunakan File Chooser untuk memilih file jawaban
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Pilih File Jawaban");
                int option = fileChooser.showOpenDialog(this);
                
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String namaFile = file.getName(); // Simpan nama file saja untuk simulasi
                    
                    Jawaban j = new Jawaban(IdUtil.generate(), siswa, tFound, namaFile);
                    jawabanRepo.addJawaban(j);
                    JOptionPane.showMessageDialog(this, "Jawaban untuk tugas '" + tFound.getJudul() + "' berhasil dikirim!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: Tugas tidak ditemukan di database.");
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnSubmit, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createNilaiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Tugas/Ujian", "Nilai", "Ket"}, 0);
        JTable table = new JTable(model);
        
        for(Nilai n : nilaiRepo.findBySiswa(siswa.getIdUser())) {
            String sumber = n.getTugas() != null ? n.getTugas().getJudul() : n.getUjian().getJenisUjian();
            model.addRow(new Object[]{sumber, n.getNilaiAngka(), n.getKeterangan()});
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createForumPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        if (siswa.getKelas() != null && siswa.getKelas().getDaftarMapel().size() > 0) {
            JTabbedPane forumTabs = new JTabbedPane();
            for (MataPelajaran m : siswa.getKelas().getDaftarMapel()) {
                forumTabs.addTab(m.getNamaMapel(), new ForumPanel(siswa, siswa.getKelas(), m, forumRepo));
            }
            panel.add(forumTabs, BorderLayout.CENTER);
        } else {
            panel.add(new JLabel("Belum ada kelas atau mata pelajaran.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        
        return panel;
    }

    private void showProfil() {
        String newPass = JOptionPane.showInputDialog("Password Baru:");
        if(newPass != null && !newPass.isBlank()) {
            siswa.setPassword(newPass);
            userRepo.saveToFile();
            JOptionPane.showMessageDialog(this, "Password diubah.");
        }
    }
}