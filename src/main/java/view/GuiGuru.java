package view;

import model.*;
import repository.*;
import utils.IdUtil;
import view.component.ForumPanel;
import utils.DateUtil;
import view.component.ForumPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        
        for(Materi mat : materiRepo.getByMapelAndKelas(m, k)) model.addRow(new Object[]{mat.getIdMateri(), mat.getJudul(), mat.getFileMateri()});
        
        JButton btnAdd = new JButton("Tambah Materi");
        btnAdd.addActionListener(e -> {
            String judul = JOptionPane.showInputDialog("Judul:");
            String desk = JOptionPane.showInputDialog("Deskripsi:");
            String file = JOptionPane.showInputDialog("Filename:");
            if(judul != null) {
                Materi mat = new Materi(IdUtil.generate(), judul, desk, file);
                mat.setGuru(guru); mat.setKelas(k); mat.setMapel(m);
                materiRepo.addMateri(mat);
                loadDashboard();
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
        
        for(Tugas t : tugasRepo.getByMapelAndKelas(m, k)) model.addRow(new Object[]{t.getIdTugas(), t.getJudul(), t.getDeadline()});
        
        JButton btnAdd = new JButton("Buat Tugas");
        btnAdd.addActionListener(e -> {
            try {
                String judul = JOptionPane.showInputDialog("Judul:");
                String desk = JOptionPane.showInputDialog("Deskripsi:");
                String tgl = JOptionPane.showInputDialog("Deadline (yyyy-MM-dd):");
                Tugas t = new Tugas(IdUtil.generate(), judul, desk, DateUtil.parse(tgl));
                t.setGuru(guru); t.setKelas(k); t.setMapel(m);
                tugasRepo.addTugas(t);
                loadDashboard();
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
             JOptionPane.showMessageDialog(this, "Fitur input nilai detail tersedia di versi lengkap.");
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnNilai, BorderLayout.SOUTH);
        return panel;
    }

private JPanel createForumPanel(Kelas k, MataPelajaran m) {
    // Cukup 1 baris ini untuk memanggil fitur forum lengkap
    return new ForumPanel(guru, k, m, forumRepo);
}

    private void showProfil() {
        String newPass = JOptionPane.showInputDialog("Ganti Password (Kosongkan jika batal):");
        if(newPass != null && !newPass.isBlank()) {
            guru.setPassword(newPass);
            userRepo.saveToFile();
            JOptionPane.showMessageDialog(this, "Password diganti.");
        }
    }
}