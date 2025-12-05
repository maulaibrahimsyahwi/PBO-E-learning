package view;

import model.*;
import repository.*;
import utils.IdUtil;
import view.component.ForumPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

public class GuiSiswa extends JFrame {
    private Siswa siswa;
    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;
    private ForumRepository forumRepo;
    private UserRepository userRepo;
    private AbsensiRepository absensiRepo;
    private SoalRepository soalRepo;

    public GuiSiswa(Siswa s, MateriRepository mr, TugasRepository tr, UjianRepository ur,
                    JawabanRepository jr, NilaiRepository nr, ForumRepository fr, UserRepository uRepo,
                    AbsensiRepository absensiRepo, SoalRepository soalRepo) {
        this.siswa = s;
        this.materiRepo = mr;
        this.tugasRepo = tr;
        this.ujianRepo = ur;
        this.jawabanRepo = jr;
        this.nilaiRepo = nr;
        this.forumRepo = fr;
        this.userRepo = uRepo;
        this.absensiRepo = absensiRepo;
        this.soalRepo = soalRepo;

        setTitle("Dashboard Siswa - " + s.getNamaLengkap());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Absensi", createAbsensiPanel());
        tabs.addTab("Materi", createMateriPanel());
        tabs.addTab("Tugas & Ujian", createTugasPanel());
        tabs.addTab("Nilai", createNilaiPanel());
        tabs.addTab("Forum", createForumPanel());
        
        add(tabs, BorderLayout.CENTER);

        JButton btnProfil = new JButton("Profil / Ubah Password");
        btnProfil.addActionListener(e -> showProfil());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(btnProfil, BorderLayout.EAST);
        
        cekNotifikasi(topPanel);
        
        add(topPanel, BorderLayout.NORTH);
    }

    private void cekNotifikasi(JPanel container) {
        if(siswa.getKelas() == null) return;
        long count = tugasRepo.getByKelas(siswa.getKelas()).stream()
            .filter(t -> !jawabanRepo.findByTugas(t.getIdTugas()).stream()
                .anyMatch(j -> j.getSiswa().getIdUser().equals(siswa.getIdUser())))
            .count();
        
        if(count > 0) {
            JLabel lbl = new JLabel("🔔 Ada " + count + " tugas belum dikerjakan!");
            lbl.setForeground(Color.RED);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            container.add(lbl, BorderLayout.WEST);
        }
    }

    private JPanel createAbsensiPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        JButton btn = new JButton("Presensi Hari Ini");
        btn.setPreferredSize(new Dimension(200, 50));
        
        boolean done = absensiRepo.sudahAbsen(siswa, LocalDate.now());
        
        if(done) {
            btn.setEnabled(false);
            btn.setText("Sudah Hadir ✅");
            btn.setBackground(new Color(144, 238, 144));
        }

        btn.addActionListener(e -> {
            Absensi a = new Absensi(IdUtil.generate(), siswa, siswa.getKelas(), LocalDate.now(), LocalTime.now().toString());
            absensiRepo.addAbsensi(a);
            JOptionPane.showMessageDialog(this, "Berhasil Absen!");
            btn.setEnabled(false);
            btn.setText("Sudah Hadir ✅");
        });
        p.add(btn);
        return p;
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
            if (row != -1) {
                String filename = (String) table.getValueAt(row, 3);
                try {
                    File file = new File("data/uploads/" + filename);
                    if (file.exists()) Desktop.getDesktop().open(file);
                    else JOptionPane.showMessageDialog(this, "File tidak ditemukan.");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnOpen, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTugasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Tipe", "Mapel", "Judul", "Deadline/Tanggal"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        
        if(siswa.getKelas() != null) {
            for(Tugas t : tugasRepo.getByKelas(siswa.getKelas())) {
                model.addRow(new Object[]{t.getIdTugas(), "Tugas", t.getMapel().getNamaMapel(), t.getJudul(), t.getDeadline()});
            }
            for(Ujian u : ujianRepo.getByKelas(siswa.getKelas())) {
                model.addRow(new Object[]{u.getIdUjian(), "Ujian", u.getMapel().getNamaMapel(), u.getJenisUjian(), u.getTanggal()});
            }
        }

        JButton btnSubmit = new JButton("Kerjakan / Submit");
        btnSubmit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;

            String id = (String) table.getValueAt(row, 0);
            String tipe = (String) table.getValueAt(row, 1);

            if (tipe.equals("Tugas")) {
                Tugas tFound = tugasRepo.getAll().stream().filter(t->t.getIdTugas().equals(id)).findFirst().orElse(null);
                if(tFound != null) submitTugasFile(tFound);
            } else {
                Ujian uFound = ujianRepo.getAll().stream().filter(u->u.getIdUjian().equals(id)).findFirst().orElse(null);
                if(uFound != null) kerjakanUjian(uFound);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnSubmit, BorderLayout.SOUTH);
        return panel;
    }

    private void submitTugasFile(Tugas t) {
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Jawaban j = new Jawaban(IdUtil.generate(), siswa, t, fc.getSelectedFile().getName());
            jawabanRepo.addJawaban(j);
            JOptionPane.showMessageDialog(this, "Jawaban Terkirim!");
        }
    }

    private void kerjakanUjian(Ujian u) {
        List<Soal> soal = soalRepo.getByUjian(u.getIdUjian());
        
        if (soal.isEmpty()) {
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                Jawaban j = new Jawaban(IdUtil.generate(), siswa, u, fc.getSelectedFile().getName());
                jawabanRepo.addJawaban(j);
                JOptionPane.showMessageDialog(this, "File Ujian Terkirim!");
            }
            return;
        }

        JDialog d = new JDialog(this, "Kuis: " + u.getJenisUjian(), true);
        d.setSize(600, 600);
        d.setLayout(new BoxLayout(d.getContentPane(), BoxLayout.Y_AXIS));
        d.setLocationRelativeTo(this);
        
        List<ButtonGroup> groups = new ArrayList<>();

        for(Soal s : soal) {
            JPanel p = new JPanel(new GridLayout(0, 1));
            p.setBorder(BorderFactory.createTitledBorder(s.getPertanyaan()));
            
            JRadioButton rA = new JRadioButton(s.getPilA()); rA.setActionCommand("A");
            JRadioButton rB = new JRadioButton(s.getPilB()); rB.setActionCommand("B");
            JRadioButton rC = new JRadioButton(s.getPilC()); rC.setActionCommand("C");
            JRadioButton rD = new JRadioButton(s.getPilD()); rD.setActionCommand("D");
            
            ButtonGroup bg = new ButtonGroup();
            bg.add(rA); bg.add(rB); bg.add(rC); bg.add(rD);
            groups.add(bg);
            
            p.add(rA); p.add(rB); p.add(rC); p.add(rD);
            d.add(p);
        }

        JButton btnFinish = new JButton("Kirim Jawaban");
        btnFinish.addActionListener(ev -> {
            int benar = 0;
            for(int i=0; i<soal.size(); i++) {
                if(groups.get(i).getSelection() != null && 
                   groups.get(i).getSelection().getActionCommand().equals(soal.get(i).getKunciJawaban())) {
                    benar++;
                }
            }
            int nilai = (benar * 100) / soal.size();
            
            Nilai n = new Nilai(IdUtil.generate(), siswa, u, nilai, "Auto");
            nilaiRepo.addNilai(n);
            siswa.tambahNilai(n);
            
            Jawaban j = new Jawaban(IdUtil.generate(), siswa, u, "Kuis Otomatis (Skor: "+nilai+")");
            jawabanRepo.addJawaban(j);

            JOptionPane.showMessageDialog(d, "Selesai! Nilai Anda: " + nilai);
            d.dispose();
        });
        
        d.add(btnFinish);
        d.setVisible(true);
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
        
        if (siswa.getKelas() != null && !siswa.getKelas().getDaftarMapel().isEmpty()) {
            JTabbedPane forumTabs = new JTabbedPane();
            for (MataPelajaran m : siswa.getKelas().getDaftarMapel()) {
                forumTabs.addTab(m.getNamaMapel(), new ForumPanel(siswa, siswa.getKelas(), m, forumRepo));
            }
            panel.add(forumTabs, BorderLayout.CENTER);
        } else {
            panel.add(new JLabel("Belum ada mata pelajaran.", SwingConstants.CENTER), BorderLayout.CENTER);
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