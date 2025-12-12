package view;

import context.AppContext;
import view.panel.*;
import javax.swing.*;
import java.awt.*;

public class GuiAdmin extends JFrame {
    private final AppContext context;

    public GuiAdmin(AppContext context) {
        this.context = context;

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
            new GuiLogin(context).setVisible(true);
        });
        buttonPanel.add(btnLogout);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("JTabbedPane.tabType", "card");

        tabbedPane.addTab("Dashboard", new AdminDashboardPanel(context.getUserRepo(), context.getKelasRepo(), context.getMapelRepo()));
        tabbedPane.addTab("Kelola Guru", new GuruManagementPanel(context.getUserRepo()));
        tabbedPane.addTab("Kelola Siswa", new SiswaManagementPanel(context.getUserRepo(), context.getKelasRepo()));
        tabbedPane.addTab("Kelola Kelas", new KelasManagementPanel(context.getKelasRepo(), context.getMapelRepo()));
        tabbedPane.addTab("Kelola Mapel", new MapelManagementPanel(context.getMapelRepo(), context.getKelasRepo(), context.getUserRepo()));
        tabbedPane.addTab("Assignment Guru", new GuruAssignmentPanel(context.getUserRepo(), context.getMapelRepo(), context.getKelasRepo()));

        add(tabbedPane, BorderLayout.CENTER);
    }
}