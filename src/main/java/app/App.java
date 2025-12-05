package app;

import repository.*;
import service.DataReconstructor;
import view.GuiLogin;
import javax.swing.SwingUtilities;
import model.Admin;

public class App {

    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository();
        KelasRepository kelasRepo = new KelasRepository();
        MapelRepository mapelRepo = new MapelRepository();
        MateriRepository materiRepo = new MateriRepository();
        TugasRepository tugasRepo = new TugasRepository();
        UjianRepository ujianRepo = new UjianRepository();
        JawabanRepository jawabanRepo = new JawabanRepository();
        NilaiRepository nilaiRepo = new NilaiRepository();
        ForumRepository forumRepo = new ForumRepository();

        DataReconstructor recon = new DataReconstructor(
            userRepo, kelasRepo, mapelRepo, materiRepo,
            tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, forumRepo
        );
        recon.reconstruct();

        // KEMBALI KE PASSWORD BIASA
        if (userRepo.findByUsername("admin") == null) {
            Admin defaultAdmin = new Admin(
                    "A001", 
                    "admin", 
                    "admin", // Password kembali ke "admin"
                    "Administrator", 
                    "admin@lms.com"
            );
            userRepo.addUser(defaultAdmin);
        }

        SwingUtilities.invokeLater(() -> {
            new GuiLogin(
                userRepo, kelasRepo, mapelRepo, materiRepo,
                tugasRepo, ujianRepo, jawabanRepo, nilaiRepo, forumRepo
            ).setVisible(true);
        });
    }
}