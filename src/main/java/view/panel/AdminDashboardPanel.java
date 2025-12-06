package view.panel;

import model.Guru;
import model.Siswa;
import repository.KelasRepository;
import repository.MapelRepository;
import repository.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class AdminDashboardPanel extends JPanel {
    private UserRepository userRepo;
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;

    public AdminDashboardPanel(UserRepository userRepo, KelasRepository kelasRepo, MapelRepository mapelRepo) {
        this.userRepo = userRepo;
        this.kelasRepo = kelasRepo;
        this.mapelRepo = mapelRepo;
        
        setLayout(new GridLayout(2, 2, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadData();
    }

    private void loadData() {
        removeAll();
        long jumlahGuru = userRepo.getAll().stream().filter(u -> u instanceof Guru).count();
        long jumlahSiswa = userRepo.getAll().stream().filter(u -> u instanceof Siswa).count();
        int jumlahKelas = kelasRepo.getAll().size();
        int jumlahMapel = mapelRepo.getAll().size();

        add(createStatCard("Total Guru", String.valueOf(jumlahGuru), new Color(255, 200, 100)));
        add(createStatCard("Total Siswa", String.valueOf(jumlahSiswa), new Color(100, 200, 255)));
        add(createStatCard("Jumlah Kelas", String.valueOf(jumlahKelas), new Color(100, 255, 100)));
        add(createStatCard("Mata Pelajaran", String.valueOf(jumlahMapel), new Color(255, 100, 255)));
        
        revalidate();
        repaint();
    }

    private JPanel createStatCard(String title, String count, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel lblCount = new JLabel(count, SwingConstants.CENTER);
        lblCount.setFont(new Font("Arial", Font.BOLD, 48));
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblCount, BorderLayout.CENTER);
        return card;
    }
}