package view;

import model.*;
import repository.*;
import utils.SecurityUtil; 
import javax.swing.*;
import java.awt.*;

public class GuiLogin extends JFrame {
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

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public GuiLogin(UserRepository userRepo, KelasRepository kelasRepo, MapelRepository mapelRepo,
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

        setTitle("Login LMS SMK Nusantara");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        txtUsername = new JTextField();
        txtUsername.putClientProperty("JTextField.placeholderText", "Username");
        
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JTextField.placeholderText", "Password");

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setBackground(new Color(60, 120, 200));
        btnLogin.setForeground(Color.WHITE);

        mainPanel.add(new JLabel("Selamat Datang di LMS", SwingConstants.CENTER));
        mainPanel.add(txtUsername);
        mainPanel.add(txtPassword);
        mainPanel.add(btnLogin);

        add(mainPanel);

        btnLogin.addActionListener(e -> prosesLogin());
    }

    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        User user = userRepo.findByUsername(username);
        String hashedInput = SecurityUtil.hashPassword(password);

        if (user != null && user.getPassword().equals(hashedInput)) {
            this.dispose();
            if (user instanceof Admin) {
                new GuiAdmin(userRepo, kelasRepo, mapelRepo, materiRepo, tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, forumRepo, absensiRepo, soalRepo).setVisible(true);
            } else if (user instanceof Guru) {
                new GuiGuru((Guru) user, materiRepo, tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, 
                            kelasRepo, mapelRepo, forumRepo, userRepo, soalRepo).setVisible(true);
            } else if (user instanceof Siswa) {
                new GuiSiswa((Siswa) user, materiRepo, tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, 
                             forumRepo, userRepo, absensiRepo, soalRepo).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}