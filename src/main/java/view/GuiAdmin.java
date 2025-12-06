package view;

import model.*;
import repository.*;
import view.panel.*;

import javax.swing.*;
import java.awt.*;

public class GuiAdmin extends JFrame {
    private UserRepository userRepo;
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;
    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;
    private ForumRepository forumRepo;
    private AbsensiRepository absensiRepo;
    private SoalRepository soalRepo;

    public GuiAdmin(UserRepository userRepo, KelasRepository kelasRepo, MapelRepository mapelRepo,
                    MateriRepository materiRepo, TugasRepository tugasRepo, UjianRepository ujianRepo,
                    JawabanRepository jawabanRepo, NilaiRepository nilaiRepo, ForumRepository forumRepo,
                    AbsensiRepository absensiRepo, SoalRepository soalRepo) {
        
        this.userRepo = userRepo;
        this.kelasRepo = kelasRepo;
        this.mapelRepo = mapelRepo;
        this.materiRepo = materiRepo;
        this.tugasRepo = tugasRepo;
        this.ujianRepo = ujianRepo;
        this.jawabanRepo = jawabanRepo;
        this.nilaiRepo = nilaiRepo;
        this.forumRepo = forumRepo;
        this.absensiRepo = absensiRepo;
        this.soalRepo = soalRepo;

        setTitle("Dashboard Admin");
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Memuat panel-panel modular
        tabbedPane.addTab("Dashboard", new AdminDashboardPanel(userRepo, kelasRepo, mapelRepo));
        tabbedPane.addTab("Kelola Guru", new GuruManagementPanel(userRepo));
        tabbedPane.addTab("Kelola Siswa", new SiswaManagementPanel(userRepo, kelasRepo));
        tabbedPane.addTab("Kelola Kelas", new KelasManagementPanel(kelasRepo, mapelRepo));
        tabbedPane.addTab("Kelola Mapel", new MapelManagementPanel(mapelRepo, kelasRepo, userRepo));
        tabbedPane.addTab("Assignment Guru", new GuruAssignmentPanel(userRepo, mapelRepo, kelasRepo));

        add(tabbedPane);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(255, 100, 100)); 
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            dispose();
            new GuiLogin(userRepo, kelasRepo, mapelRepo, materiRepo, tugasRepo, ujianRepo, 
                         jawabanRepo, nilaiRepo, forumRepo, absensiRepo, soalRepo).setVisible(true);
        });
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}