package view;

import context.AppContext;
import model.*;
import service.AuthService;
import javax.swing.*;
import java.awt.*;

public class GuiLogin extends JFrame {
    private final AppContext context;
    private final AuthService authService;

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public GuiLogin(AppContext context) {
        this.context = context;
        this.authService = context.getAuthService();

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
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);

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

        User user = authService.login(username, password);

        if (user != null) {
            this.dispose();
            if (user instanceof Admin) {
                // Perbaikan: Memanggil GuiAdmin hanya dengan context
                new GuiAdmin(context).setVisible(true);
            } else if (user instanceof Guru) {
                // Perbaikan: Memanggil GuiGuru dengan User dan context
                new GuiGuru((Guru) user, context).setVisible(true);
            } else if (user instanceof Siswa) {
                // Perbaikan: Memanggil GuiSiswa dengan User dan context
                new GuiSiswa((Siswa) user, context).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}