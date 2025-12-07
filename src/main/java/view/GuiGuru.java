package view;

import context.AppContext;
import model.*;
import view.component.ForumPanel;
import view.panel.GuruMateriPanel;
import view.panel.GuruNilaiPanel;
import view.panel.GuruTugasPanel;
import view.panel.GuruUjianPanel;

import javax.swing.*;
import java.awt.*;

public class GuiGuru extends JFrame {
    private final Guru guru;
    private final AppContext context;

    private JComboBox<Kelas> comboKelas;
    private JComboBox<MataPelajaran> comboMapel;
    private JTabbedPane tabbedContent;

    public GuiGuru(Guru guru, AppContext context) {
        this.guru = guru;
        this.context = context;

        setTitle("Dashboard Guru - " + guru.getNamaLengkap());
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnProfil = new JButton("Profil");
        buttonPanel.add(btnProfil);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(255, 100, 100));
        btnLogout.setForeground(Color.WHITE);
        
        // Perbaikan: Logout memanggil GuiLogin dengan context
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Logout", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                dispose();
                new GuiLogin(context).setVisible(true);
            }
        });
        buttonPanel.add(btnLogout);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        tabbedContent = new JTabbedPane();
        tabbedContent.putClientProperty("JTabbedPane.tabType", "card");
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

        // Menggunakan repo dari context untuk diserahkan ke panel
        GuruUjianPanel ujianPanel = new GuruUjianPanel(this, guru, k, m, context.getUjianRepo(), context.getSoalRepo());
        GuruMateriPanel materiPanel = new GuruMateriPanel(guru, k, m, context.getMateriRepo());
        GuruTugasPanel tugasPanel = new GuruTugasPanel(guru, k, m, context.getTugasRepo(), ujianPanel);
        GuruNilaiPanel nilaiPanel = new GuruNilaiPanel(k, m, context.getTugasRepo(), context.getUjianRepo(), context.getJawabanRepo(), context.getNilaiRepo(), context.getUserRepo());
        
        tabbedContent.addTab("Materi", materiPanel);
        tabbedContent.addTab("Tugas", tugasPanel);
        tabbedContent.addTab("Ujian & Kuis", ujianPanel);
        tabbedContent.addTab("Penilaian", nilaiPanel);
        tabbedContent.addTab("Forum", new ForumPanel(guru, k, m, context.getForumRepo()));
        
        tabbedContent.addChangeListener(e -> {
            if (tabbedContent.getSelectedComponent() == ujianPanel) ujianPanel.refreshTable();
            if (tabbedContent.getSelectedComponent() == materiPanel) materiPanel.refreshTable();
            if (tabbedContent.getSelectedComponent() == tugasPanel) tugasPanel.refreshTable();
            if (tabbedContent.getSelectedComponent() == nilaiPanel) nilaiPanel.refreshTable();
        });
    }

    private void showProfil() {
        String newPass = JOptionPane.showInputDialog("Ganti Password (Kosongkan jika batal):");
        if(newPass != null && !newPass.isBlank()) {
            guru.setPassword(newPass);
            context.getUserRepo().updateGuru(guru); // Update user repo call
            JOptionPane.showMessageDialog(this, "Password berhasil diubah.");
        }
    }
}