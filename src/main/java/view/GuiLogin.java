package view;

import model.*;
import repository.*;
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

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public GuiLogin(UserRepository userRepo, KelasRepository kelasRepo, MapelRepository mapelRepo,
                    MateriRepository materiRepo, TugasRepository tugasRepo, UjianRepository ujianRepo,
                    JawabanRepository jawabanRepo, NilaiRepository nilaiRepo, ForumRepository forumRepo) {
        this.userRepo = userRepo;
        this.kelasRepo = kelasRepo;
        this.mapelRepo = mapelRepo;
        this.materiRepo = materiRepo;
        this.tugasRepo = tugasRepo;
        this.ujianRepo = ujianRepo;
        this.jawabanRepo = jawabanRepo;
        this.nilaiRepo = nilaiRepo;
        this.forumRepo = forumRepo;

        setTitle("Login LMS SMK Nusantara");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:", SwingConstants.CENTER));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Password:", SwingConstants.CENTER));
        txtPassword = new JPasswordField();
        add(txtPassword);

        JButton btnLogin = new JButton("Login");
        add(new JLabel(""));
        add(btnLogin);

        btnLogin.addActionListener(e -> prosesLogin());
    }

    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        User user = userRepo.findByUsername(username);

        if (user != null && user.login(username, password)) {
            JOptionPane.showMessageDialog(this, "Selamat datang, " + user.getNamaLengkap());
            this.dispose();

            if (user instanceof Admin) {
                new GuiAdmin(userRepo, kelasRepo, mapelRepo).setVisible(true);
            } else if (user instanceof Guru) {
                new GuiGuru((Guru) user, materiRepo, tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, kelasRepo, mapelRepo, forumRepo, userRepo).setVisible(true);
            } else if (user instanceof Siswa) {
                new GuiSiswa((Siswa) user, materiRepo, tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, forumRepo, userRepo).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}