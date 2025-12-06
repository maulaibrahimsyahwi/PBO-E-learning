package app;

import repository.*;
import view.GuiLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import model.Admin;
import utils.SecurityUtil;

public class App {
    public static void main(String[] args) {
        // 1. Setup Tema Tampilan (FlatLaf)
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("TabbedPane.showTabSeparators", true);
        } catch (Exception ex) { 
            ex.printStackTrace();
        }

        // 2. Inisialisasi Semua Repository (Koneksi Database)
        UserRepository userRepo = new UserRepository();
        KelasRepository kelasRepo = new KelasRepository();
        MapelRepository mapelRepo = new MapelRepository();
        MateriRepository materiRepo = new MateriRepository();
        TugasRepository tugasRepo = new TugasRepository();
        UjianRepository ujianRepo = new UjianRepository();
        JawabanRepository jawabanRepo = new JawabanRepository();
        NilaiRepository nilaiRepo = new NilaiRepository();
        ForumRepository forumRepo = new ForumRepository();
        AbsensiRepository absensiRepo = new AbsensiRepository();
        SoalRepository soalRepo = new SoalRepository();

        // 3. Cek & Buat Admin Default jika belum ada di database
        if (userRepo.findByUsername("admin") == null) {
            System.out.println("Membuat akun admin default...");
            String passHash = SecurityUtil.hashPassword("admin");
            Admin defaultAdmin = new Admin("A001", "admin", passHash, "Administrator", "admin@lms.com");
            userRepo.addUser(defaultAdmin);
        }

        // 4. Jalankan GUI Login
        SwingUtilities.invokeLater(() -> {
            new GuiLogin(
                userRepo, kelasRepo, mapelRepo, materiRepo,
                tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, 
                forumRepo, absensiRepo, soalRepo
            ).setVisible(true);
        });
    }
}