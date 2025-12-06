package view;

import model.*;
import repository.*;
import view.component.ForumPanel;
import view.panel.SiswaAbsensiPanel;
import view.panel.SiswaMateriPanel;
import view.panel.SiswaNilaiPanel;
import view.panel.SiswaTugasUjianPanel;

import javax.swing.*;
import java.awt.*;
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
    private AbsensiRepository absensiRepo;
    private SoalRepository soalRepo;
    
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;

    private SiswaTugasUjianPanel tugasPanel;
    private SiswaMateriPanel materiPanel;
    private SiswaNilaiPanel nilaiPanel;

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

        materiPanel = new SiswaMateriPanel(this, siswa, materiRepo);
        tugasPanel = new SiswaTugasUjianPanel(this, siswa, tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, soalRepo);
        nilaiPanel = new SiswaNilaiPanel(siswa, nilaiRepo);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Absensi", new SiswaAbsensiPanel(siswa, absensiRepo));
        tabs.addTab("Materi", materiPanel);
        tabs.addTab("Tugas & Ujian", tugasPanel);
        tabs.addTab("Nilai", nilaiPanel);
        tabs.addTab("Forum", createForumPanel());
        
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == materiPanel) materiPanel.refreshTable();
            if (tabs.getSelectedComponent() == tugasPanel) tugasPanel.refreshTable();
            if (tabs.getSelectedComponent() == nilaiPanel) nilaiPanel.refreshTable();
        });
        
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