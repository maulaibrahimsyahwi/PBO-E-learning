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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;

    private DefaultTableModel tugasModel;

    public GuiSiswa(Siswa s, MateriRepository mr, TugasRepository tr, UjianRepository ur,
                    JawabanRepository jr, NilaiRepository nr, ForumRepository fr, UserRepository uRepo,
                    AbsensiRepository absensiRepo, SoalRepository soalRepo,
                    KelasRepository kelasRepo, MapelRepository mapelRepo) {
        
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
        this.kelasRepo = kelasRepo;
        this.mapelRepo = mapelRepo;

        if (this.siswa.getKelas() != null) {
            Kelas kRefresh = kelasRepo.findById(this.siswa.getKelas().getIdKelas());
            if (kRefresh != null) {
                if (kRefresh.getDaftarMapel().isEmpty()) {
                    List<MataPelajaran> allMapel = mapelRepo.getAll();
                    for (MataPelajaran mp : allMapel) {
                        if (mp.getTingkat().equals(kRefresh.getTingkat()) || mp.getTingkat().equals("-")) {
                            kRefresh.tambahMapel(mp);
                        }
                    }
                }
                this.siswa.setKelas(kRefresh);
            }
        }

        setTitle("Dashboard Siswa - " + s.getNamaLengkap());
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Absensi", createAbsensiPanel());
        tabs.addTab("Materi", createMateriPanel());
        tabs.addTab("Tugas & Ujian", createTugasPanel());
        tabs.addTab("Nilai", createNilaiPanel());
        tabs.addTab("Forum", createForumPanel());
        
        add(tabs, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnProfil = new JButton("Profil");
        btnProfil.addActionListener(e -> showProfil());

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(255, 100, 100));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Logout", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION){
                this.dispose();
                new GuiLogin(userRepo, kelasRepo, mapelRepo, materiRepo, tugasRepo, 
                             ujianRepo, jawabanRepo, nilaiRepo, forumRepo, 
                             absensiRepo, soalRepo).setVisible(true);
            }
        });
        
        buttonPanel.add(btnProfil);
        buttonPanel.add(btnLogout);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
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
            btn.setBackground(new Color(144, 238, 144));
        });
        p.add(btn);
        return p;
    }

    private JPanel createMateriPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Mapel", "Judul", "Deskripsi", "File"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        if(siswa.getKelas() != null) {
            for(Materi m : materiRepo.getByKelas(siswa.getKelas())) {
                String namaMapel = (m.getMapel() != null) ? m.getMapel().getNamaMapel() : "-";
                model.addRow(new Object[]{m.getIdMateri(), namaMapel, m.getJudul(), m.getDeskripsi(), m.getFileMateri()});
            }
        }
        
        JButton btnOpen = new JButton("Buka File Materi");
        btnOpen.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String idMateri = (String) table.getValueAt(row, 0);
                String filename = (String) table.getValueAt(row, 4);
                
                File folderUploads = new File("data/uploads/");
                if (!folderUploads.exists()) folderUploads.mkdirs();
                
                File fileTujuan = new File(folderUploads, filename);
                
                if (!fileTujuan.exists()) {
                    boolean success = materiRepo.downloadFile(idMateri, fileTujuan);
                    if (!success) {
                        JOptionPane.showMessageDialog(this, "Gagal mengunduh file dari database atau file tidak ada.");
                        return;
                    }
                }
                
                try {
                    if (fileTujuan.exists()) Desktop.getDesktop().open(fileTujuan);
                    else JOptionPane.showMessageDialog(this, "File error.");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error membuka file: " + ex.getMessage()); }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih materi terlebih dahulu.");
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnOpen, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTugasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Tipe", "Mapel", "Judul", "Tanggal/Deadline", "Status"};
        tugasModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tugasModel);
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        refreshTugasTable(); 

        JButton btnSubmit = new JButton("Kerjakan / Submit");
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSubmit.setBackground(new Color(100, 200, 100)); 
        btnSubmit.setForeground(Color.WHITE);

        btnSubmit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih tugas atau ujian terlebih dahulu.");
                return;
            }

            String id = (String) table.getValueAt(row, 0);
            String tipe = (String) table.getValueAt(row, 1);
            String status = (String) table.getValueAt(row, 5);

            if (!status.equals("Belum Dikerjakan")) {
                JOptionPane.showMessageDialog(this, "Anda sudah mengerjakan/mengumpulkan ini!\nStatus: " + status, 
                                              "Sudah Selesai", JOptionPane.WARNING_MESSAGE);
                return;
            }

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

    private void refreshTugasTable() {
        tugasModel.setRowCount(0);
        if(siswa.getKelas() == null) return;

        for(Tugas t : tugasRepo.getByKelas(siswa.getKelas())) {
            String status = "Belum Dikerjakan";
            boolean submitted = jawabanRepo.findByTugas(t.getIdTugas()).stream()
                    .anyMatch(j -> j.getSiswa().getIdUser().equals(siswa.getIdUser()));
            
            if (submitted) {
                status = "Sudah Submit";
                Optional<Nilai> n = nilaiRepo.getAll().stream()
                    .filter(nil -> nil.getTugas() != null && nil.getTugas().getIdTugas().equals(t.getIdTugas()) 
                            && nil.getSiswa().getIdUser().equals(siswa.getIdUser()))
                    .findFirst();
                if(n.isPresent()) {
                    status = "Selesai (Nilai: " + n.get().getNilaiAngka() + ")";
                }
            }
            String namaMapel = (t.getMapel() != null) ? t.getMapel().getNamaMapel() : "-";
            tugasModel.addRow(new Object[]{t.getIdTugas(), "Tugas", namaMapel, t.getJudul(), t.getDeadline(), status});
        }

        for(Ujian u : ujianRepo.getByKelas(siswa.getKelas())) {
            String status = "Belum Dikerjakan";
            boolean submitted = jawabanRepo.findByUjian(u.getIdUjian()).stream()
                    .anyMatch(j -> j.getSiswa().getIdUser().equals(siswa.getIdUser()));
            
            if (submitted) {
                status = "Sudah Submit";
                Optional<Nilai> n = nilaiRepo.getAll().stream()
                    .filter(nil -> nil.getUjian() != null && nil.getUjian().getIdUjian().equals(u.getIdUjian()) 
                            && nil.getSiswa().getIdUser().equals(siswa.getIdUser()))
                    .findFirst();
                if(n.isPresent()) {
                    status = "Selesai (Nilai: " + n.get().getNilaiAngka() + ")";
                }
            }
            String namaMapel = (u.getMapel() != null) ? u.getMapel().getNamaMapel() : "-";
            tugasModel.addRow(new Object[]{u.getIdUjian(), "Ujian", namaMapel, u.getNamaUjian(), u.getTanggal(), status});
        }
    }

    private void submitTugasFile(Tugas t) {
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileAsli = fc.getSelectedFile();
            Jawaban j = new Jawaban(IdUtil.generate(), siswa, t, fileAsli.getName());
            jawabanRepo.addJawaban(j, fileAsli);
            JOptionPane.showMessageDialog(this, "Jawaban Tugas Terkirim!");
            refreshTugasTable(); 
        }
    }

    private void kerjakanUjian(Ujian u) {
        List<Soal> soalList = soalRepo.getByUjian(u.getIdUjian());
        
        if (soalList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Soal belum tersedia untuk ujian ini.");
            return;
        }

        JDialog d = new JDialog(this, u.getTipeUjian() + ": " + u.getNamaUjian(), true);
        d.setSize(800, 600);
        d.setLocationRelativeTo(this);
        
        JPanel cardPanel = new JPanel(new CardLayout());
        
        List<Object> inputComponents = new ArrayList<>();
        
        JLabel lblTimer = new JLabel("Waktu: -");
        lblTimer.setFont(new Font("Arial", Font.BOLD, 14));
        lblTimer.setForeground(Color.RED);

        for (int i = 0; i < soalList.size(); i++) {
            Soal s = soalList.get(i);
            JPanel pSoal = new JPanel(new BorderLayout(10, 10));
            pSoal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JTextArea txtTanya = new JTextArea("No " + (i+1) + ".\n" + s.getPertanyaan());
            txtTanya.setWrapStyleWord(true); 
            txtTanya.setLineWrap(true);
            txtTanya.setEditable(false); 
            txtTanya.setFont(new Font("SansSerif", Font.BOLD, 16));
            txtTanya.setBackground(new Color(240, 240, 240));
            pSoal.add(new JScrollPane(txtTanya), BorderLayout.NORTH);

            JPanel pJawab = new JPanel();
            
            if ("ESSAY".equals(s.getTipeSoal())) {
                JTextArea txtJawab = new JTextArea(10, 20);
                pJawab.setLayout(new BorderLayout());
                pJawab.add(new JLabel("Jawaban Anda:"), BorderLayout.NORTH);
                pJawab.add(new JScrollPane(txtJawab), BorderLayout.CENTER);
                inputComponents.add(txtJawab);
            } else {
                pJawab.setLayout(new GridLayout(4, 1, 5, 5));
                ButtonGroup bg = new ButtonGroup();
                String[] ops = {s.getPilA(), s.getPilB(), s.getPilC(), s.getPilD()};
                String[] keys = {"A", "B", "C", "D"};
                
                for(int k=0; k<4; k++) {
                    JRadioButton rb = new JRadioButton(keys[k] + ". " + ops[k]);
                    rb.setActionCommand(keys[k]);
                    rb.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    bg.add(rb); 
                    pJawab.add(rb);
                }
                inputComponents.add(bg);
            }
            pSoal.add(pJawab, BorderLayout.CENTER);
            cardPanel.add(pSoal, "SOAL_" + i);
        }

        JPanel navPanel = new JPanel(new FlowLayout());
        JButton btnNext = new JButton("Selanjutnya >");
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        
        final int[] currentSoalIndex = {0};
        final int[] timeLeft = {0}; 

        JButton btnFinish = new JButton("Selesai & Kumpulkan");
        btnFinish.setBackground(new Color(50, 200, 50));
        btnFinish.setForeground(Color.WHITE);
        btnFinish.setVisible(false); 
        
        btnNext.addActionListener(e -> {
            if (currentSoalIndex[0] < soalList.size() - 1) {
                currentSoalIndex[0]++;
                cl.show(cardPanel, "SOAL_" + currentSoalIndex[0]);
                
                if ("KUIS".equals(u.getTipeUjian())) {
                    timeLeft[0] = u.getWaktuPerSoal(); 
                }
                
                if (currentSoalIndex[0] == soalList.size() - 1) {
                    btnNext.setVisible(false);
                    btnFinish.setVisible(true);
                }
            }
        });

        Runnable submitAction = () -> {
            int score = hitungNilai(soalList, inputComponents);
            
            Nilai n = new Nilai(IdUtil.generate(), siswa, u, score, "Selesai");
            nilaiRepo.addNilai(n);
            siswa.tambahNilai(n);
            
            Jawaban j = new Jawaban(IdUtil.generate(), siswa, u, "Digital (Skor: "+score+")");
            jawabanRepo.addJawaban(j, null);

            d.dispose();
            JOptionPane.showMessageDialog(this, "Ujian Selesai! Nilai Anda: " + score);
            refreshTugasTable(); 
        };
        btnFinish.addActionListener(e -> submitAction.run());

        Timer timer;
        if ("KUIS".equals(u.getTipeUjian())) {
            timeLeft[0] = u.getWaktuPerSoal();
            timer = new Timer(1000, e -> {
                timeLeft[0]--;
                lblTimer.setText("Sisa Waktu Soal: " + timeLeft[0] + " detik");
                if (timeLeft[0] <= 0) {
                    if (currentSoalIndex[0] < soalList.size() - 1) {
                        currentSoalIndex[0]++;
                        cl.show(cardPanel, "SOAL_" + currentSoalIndex[0]);
                        timeLeft[0] = u.getWaktuPerSoal();
                        
                        if (currentSoalIndex[0] == soalList.size() - 1) {
                            btnNext.setVisible(false);
                            btnFinish.setVisible(true);
                        }
                    } else { 
                        ((Timer)e.getSource()).stop();
                        JOptionPane.showMessageDialog(d, "Waktu Habis!");
                        submitAction.run(); 
                    }
                }
            });
        } else {
            timeLeft[0] = u.getDurasiTotal() * 60;
            timer = new Timer(1000, e -> {
                timeLeft[0]--;
                long min = timeLeft[0] / 60;
                long sec = timeLeft[0] % 60;
                lblTimer.setText(String.format("Sisa Waktu: %02d:%02d", min, sec));
                
                if (timeLeft[0] <= 0) {
                    ((Timer)e.getSource()).stop();
                    JOptionPane.showMessageDialog(d, "Waktu Ujian Habis! Jawaban otomatis dikumpulkan.");
                    submitAction.run();
                }
            });
        }
        timer.start();

        JPanel topBar = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel(" " + u.getNamaUjian() + " (" + u.getTipeUjian() + ")");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(lblTimer, BorderLayout.EAST);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        navPanel.add(btnNext);
        navPanel.add(btnFinish);

        d.add(topBar, BorderLayout.NORTH);
        d.add(cardPanel, BorderLayout.CENTER);
        d.add(navPanel, BorderLayout.SOUTH);
        
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirm = JOptionPane.showConfirmDialog(d, 
                    "Jika keluar sekarang, jawaban akan dikumpulkan apa adanya. Yakin?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    timer.stop();
                    submitAction.run();
                }
            }
        });

        d.setVisible(true);
    }

    private int hitungNilai(List<Soal> soal, List<Object> inputs) {
        int benar = 0;
        int totalSoal = soal.size();
        
        for (int i = 0; i < totalSoal; i++) {
            Soal s = soal.get(i);
            Object comp = inputs.get(i);
            
            if ("ESSAY".equals(s.getTipeSoal())) {
                if (comp instanceof JTextArea) {
                    JTextArea area = (JTextArea) comp;
                    if(!area.getText().isBlank()) {
                       if(area.getText().trim().equalsIgnoreCase(s.getKunciJawaban())) {
                           benar++;
                       }
                    }
                }
            } else {
                if (comp instanceof ButtonGroup) {
                    ButtonGroup bg = (ButtonGroup) comp;
                    if (bg.getSelection() != null && bg.getSelection().getActionCommand().equals(s.getKunciJawaban())) {
                        benar++;
                    }
                }
            }
        }
        if (totalSoal == 0) return 0;
        return (benar * 100) / totalSoal;
    }

    private JPanel createNilaiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Tugas/Ujian", "Nilai", "Ket"}, 0);
        JTable table = new JTable(model);
        
        for(Nilai n : nilaiRepo.findBySiswa(siswa.getIdUser())) {
            String sumber = n.getTugas() != null ? "Tugas: " + n.getTugas().getJudul() : 
                            (n.getUjian() != null ? "Ujian: " + n.getUjian().getNamaUjian() : "Unknown");
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
            String msg = (siswa.getKelas() == null) ? "Anda belum masuk kelas manapun. Hubungi admin untuk assignment kelas." : "Belum ada mata pelajaran di kelas ini.";
            JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 14));
            panel.add(lbl, BorderLayout.CENTER);
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