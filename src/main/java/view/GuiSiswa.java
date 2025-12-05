package view;

import model.*;
import repository.*;
import utils.IdUtil;
import view.component.ForumPanel; // Import ini PENTING

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GuiSiswa extends JFrame {
    private Siswa siswa;
    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo; // Field ini boleh dihapus jika warning mengganggu
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

    // ... (createMateriPanel, createTugasPanel, createNilaiPanel TETAP SAMA) ...

    private JPanel createMateriPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Mapel", "Judul", "Deskripsi"}, 0);
        JTable table = new JTable(model);
        if(siswa.getKelas() != null) {
            for(Materi m : materiRepo.getByKelas(siswa.getKelas())) {
                model.addRow(new Object[]{m.getMapel().getNamaMapel(), m.getJudul(), m.getDeskripsi()});
            }
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTugasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Mapel", "Judul", "Deadline"}, 0);
        JTable table = new JTable(model);
        if(siswa.getKelas() != null) {
            for(Tugas t : tugasRepo.getByKelas(siswa.getKelas())) {
                model.addRow(new Object[]{t.getIdTugas(), t.getMapel().getNamaMapel(), t.getJudul(), t.getDeadline()});
            }
        }

        JButton btnSubmit = new JButton("Submit Jawaban");
        btnSubmit.addActionListener(e -> {
            String idTugas = JOptionPane.showInputDialog("Masukkan ID Tugas:");
            String file = JOptionPane.showInputDialog("Nama File Jawaban:");
            
            Tugas tFound = null;
            for(Tugas t : tugasRepo.getAll()) if(t.getIdTugas().equals(idTugas)) tFound = t;

            if(tFound != null && file != null) {
                Jawaban j = new Jawaban(IdUtil.generate(), siswa, tFound, file);
                jawabanRepo.addJawaban(j);
                JOptionPane.showMessageDialog(this, "Terkirim!");
            } else {
                JOptionPane.showMessageDialog(this, "Tugas tidak ditemukan.");
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

    // PERBAIKAN DI SINI:
    private JPanel createForumPanel() {
        JPanel panel = new JPanel(new BorderLayout()); // Bungkus utama
        
        if (siswa.getKelas() != null && siswa.getKelas().getDaftarMapel().size() > 0) {
            JTabbedPane forumTabs = new JTabbedPane();
            for (MataPelajaran m : siswa.getKelas().getDaftarMapel()) {
                forumTabs.addTab(m.getNamaMapel(), new ForumPanel(siswa, siswa.getKelas(), m, forumRepo));
            }
            panel.add(forumTabs, BorderLayout.CENTER);
        } else {
            panel.add(new JLabel("Belum ada kelas atau mata pelajaran.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        
        return panel; // Kembalikan JPanel, bukan JTabbedPane
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