package view;

import context.AppContext;
import model.*;
import view.component.ForumPanel;
import view.panel.SiswaAbsensiPanel;
import view.panel.SiswaMateriPanel;
import view.panel.SiswaNilaiPanel;
import view.panel.SiswaTugasUjianPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GuiSiswa extends JFrame {
    private final Siswa siswa;
    private final AppContext context;

    private SiswaTugasUjianPanel tugasPanel;
    private SiswaMateriPanel materiPanel;
    private SiswaNilaiPanel nilaiPanel;
    
    // Perbaikan: Jadikan topPanel sebagai field class agar bisa diakses method lain
    private JPanel topPanel;
    private JLabel lblNotifikasiTugas;

    public GuiSiswa(Siswa s, AppContext context) {
        this.siswa = s;
        this.context = context;

        // 1. Refresh data kelas terbaru dari database
        if (this.siswa.getKelas() != null) {
            Kelas kRefresh = context.getKelasRepo().findById(this.siswa.getKelas().getIdKelas());
            if (kRefresh != null) {
                this.siswa.setKelas(kRefresh);
            }
        }

        // 2. Siapkan teks informasi kelas untuk Judul Window
        String infoKelas = "Belum Masuk Kelas";
        if (this.siswa.getKelas() != null) {
            infoKelas = "Kelas " + this.siswa.getKelas().getNamaKelas();
        }

        // 3. Update Title dengan Nama Siswa DAN Kelas
        setTitle("Dashboard Siswa - " + s.getNamaLengkap() + " [" + infoKelas + "]");
        
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        materiPanel = new SiswaMateriPanel(this, siswa, context.getMateriRepo());
        tugasPanel = new SiswaTugasUjianPanel(this, siswa, context.getTugasRepo(), context.getUjianRepo(), 
                                              context.getJawabanRepo(), context.getNilaiRepo(), context.getSoalRepo());
        nilaiPanel = new SiswaNilaiPanel(siswa, context.getNilaiRepo(), context.getTugasRepo(), context.getUjianRepo());
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty("JTabbedPane.tabType", "card");
        tabs.addTab("Absensi", new SiswaAbsensiPanel(siswa, context.getAbsensiRepo()));
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

        // Perbaikan: Inisialisasi field topPanel (jangan buat variabel lokal baru)
        topPanel = new JPanel(new BorderLayout());
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
                new GuiLogin(context).setVisible(true);
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
        
        long count = context.getTugasRepo().getByKelas(siswa.getKelas()).stream()
            .filter(t -> !context.getJawabanRepo().findByTugas(t.getIdTugas()).stream()
                .anyMatch(j -> j.getSiswa().getIdUser().equals(siswa.getIdUser())))
            .count();
        
        if(count > 0) {
            if (lblNotifikasiTugas == null) {
                lblNotifikasiTugas = new JLabel();
                lblNotifikasiTugas.setForeground(Color.RED);
                lblNotifikasiTugas.setFont(new Font("SansSerif", Font.BOLD, 14));
                lblNotifikasiTugas.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                container.add(lblNotifikasiTugas, BorderLayout.WEST); 
                container.revalidate();
                container.repaint();
            }
            lblNotifikasiTugas.setText("🔔 Ada " + count + " tugas belum dikerjakan!");
        } else {
            if (lblNotifikasiTugas != null) {
                container.remove(lblNotifikasiTugas);
                lblNotifikasiTugas = null;
                container.revalidate();
                container.repaint();
            }
        }
    }

    private JPanel createForumPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        if (siswa.getKelas() != null && !siswa.getKelas().getDaftarMapel().isEmpty()) {
            JTabbedPane forumTabs = new JTabbedPane();
            forumTabs.putClientProperty("JTabbedPane.tabType", "card");
            for (MataPelajaran m : siswa.getKelas().getDaftarMapel()) {
                forumTabs.addTab(m.getNamaMapel(), new ForumPanel(siswa, siswa.getKelas(), m, context.getForumRepo()));
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
            String hashed = utils.SecurityUtil.hashPassword(newPass);
            siswa.setPassword(hashed);
            context.getUserRepo().updateSiswa(siswa); 
            JOptionPane.showMessageDialog(this, "Password diubah.");
        }
    }
    
    public void refreshNotification() {
        // Perbaikan: Gunakan field topPanel langsung, jangan ambil dari getComponent(0)
        if (topPanel != null) {
            cekNotifikasi(topPanel);
        }
    }
}