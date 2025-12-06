package view;

import model.*;
import repository.*;
import view.component.ForumPanel;
import view.panel.GuruMateriPanel;
import view.panel.GuruNilaiPanel;
import view.panel.GuruTugasPanel;
import view.panel.GuruUjianPanel;

import javax.swing.*;
import java.awt.*;

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

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Left side: Selectors
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        selectorPanel.add(new JLabel("Kelas:"));
        comboKelas = new JComboBox<>();
        for(Kelas k : guru.getDaftarKelas()) comboKelas.addItem(k);
        comboKelas.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Kelas) setText(((Kelas)value).getNamaKelas());
                return this;
            }
        });
        selectorPanel.add(comboKelas);

        selectorPanel.add(new JLabel("Mapel:"));
        comboMapel = new JComboBox<>();
        for(MataPelajaran m : guru.getMapelDiampu()) comboMapel.addItem(m);
        comboMapel.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MataPelajaran) setText(((MataPelajaran)value).getNamaMapel());
                return this;
            }
        });
        selectorPanel.add(comboMapel);

        JButton btnLoad = new JButton("Buka Kelas");
        selectorPanel.add(btnLoad);
        
        topPanel.add(selectorPanel, BorderLayout.WEST);

        // Right side: Profile and Logout Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnProfil = new JButton("Profil");
        buttonPanel.add(btnProfil);
        
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
        buttonPanel.add(btnLogout);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
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

        GuruUjianPanel ujianPanel = new GuruUjianPanel(this, guru, k, m, ujianRepo, soalRepo);
        GuruMateriPanel materiPanel = new GuruMateriPanel(guru, k, m, materiRepo);
        GuruTugasPanel tugasPanel = new GuruTugasPanel(guru, k, m, tugasRepo, ujianPanel);
        GuruNilaiPanel nilaiPanel = new GuruNilaiPanel(k, m, tugasRepo, ujianRepo, jawabanRepo, nilaiRepo);
        
        tabbedContent.addTab("Materi", materiPanel);
        tabbedContent.addTab("Tugas", tugasPanel);
        tabbedContent.addTab("Ujian & Kuis", ujianPanel);
        tabbedContent.addTab("Penilaian", nilaiPanel);
        tabbedContent.addTab("Forum", createForumPanel(k, m));
        
        tabbedContent.addChangeListener(e -> {
            if (tabbedContent.getSelectedComponent() == ujianPanel) ujianPanel.refreshTable();
            if (tabbedContent.getSelectedComponent() == materiPanel) materiPanel.refreshTable();
            if (tabbedContent.getSelectedComponent() == tugasPanel) tugasPanel.refreshTable();
            if (tabbedContent.getSelectedComponent() == nilaiPanel) nilaiPanel.refreshTable();
        });
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