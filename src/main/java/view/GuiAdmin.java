// File: src/main/java/view/GuiAdmin.java

package view;

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

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(255, 100, 100)); 
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            dispose();
            new GuiLogin(userRepo, kelasRepo, mapelRepo, materiRepo, tugasRepo, ujianRepo, 
                         jawabanRepo, nilaiRepo, forumRepo, absensiRepo, soalRepo).setVisible(true);
        });
        buttonPanel.add(btnLogout);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Dashboard", new AdminDashboardPanel(userRepo, kelasRepo, mapelRepo));
        tabbedPane.addTab("Kelola Guru", new GuruManagementPanel(userRepo));
        tabbedPane.addTab("Kelola Siswa", new SiswaManagementPanel(userRepo, kelasRepo));
        tabbedPane.addTab("Kelola Kelas", new KelasManagementPanel(kelasRepo, mapelRepo));
        tabbedPane.addTab("Kelola Mapel", new MapelManagementPanel(mapelRepo, kelasRepo, userRepo));
        tabbedPane.addTab("Assignment Guru", new GuruAssignmentPanel(userRepo, mapelRepo, kelasRepo));

        add(tabbedPane, BorderLayout.CENTER);
    }
}