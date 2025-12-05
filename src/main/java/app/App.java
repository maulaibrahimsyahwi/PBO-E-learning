package app;

import repository.*;
import service.DataReconstructor;
import view.GuiLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.Admin;
import com.formdev.flatlaf.FlatLightLaf;

public class App {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
        } catch (Exception ex) {
            System.err.println("Gagal load FlatLaf.");
        }

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

        DataReconstructor recon = new DataReconstructor(
            userRepo, kelasRepo, mapelRepo, materiRepo,
            tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, forumRepo
        );
        recon.reconstruct();

        if (userRepo.findByUsername("admin") == null) {
            String passHash = utils.SecurityUtil.hashPassword("admin");
            Admin defaultAdmin = new Admin(
                    "A001", "admin", passHash, "Administrator", "admin@lms.com"
            );
            userRepo.addUser(defaultAdmin);
        }

        SwingUtilities.invokeLater(() -> {
            new GuiLogin(
                userRepo, kelasRepo, mapelRepo, materiRepo,
                tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, 
                forumRepo, absensiRepo, soalRepo
            ).setVisible(true);
        });
    }
}