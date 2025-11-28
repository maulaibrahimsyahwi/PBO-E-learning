package app;

import model.*;
import repository.*;
import view.*;
import utils.InputUtil;

public class App {

    public static void main(String[] args) {

        UserRepository userRepo = new UserRepository();
        KelasRepository kelasRepo = new KelasRepository();
        MapelRepository mapelRepo = new MapelRepository();
        MateriRepository materiRepo = new MateriRepository();
        TugasRepository tugasRepo = new TugasRepository();
        UjianRepository ujianRepo = new UjianRepository();
        JawabanRepository jawabanRepo = new JawabanRepository();

        LoginView loginView = new LoginView(userRepo);
        AdminView adminView = new AdminView(userRepo, kelasRepo, mapelRepo);
        GuruView guruView = new GuruView(materiRepo, tugasRepo, ujianRepo);
        SiswaView siswaView = new SiswaView(materiRepo, tugasRepo, ujianRepo, jawabanRepo);

        while (true) {
            System.out.println("\n=== LMS SMK NUSANTARA ===");
            System.out.println("1. Login");
            System.out.println("0. Keluar");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            if (pilih == 0) break;

            switch (pilih) {

                case 1 -> {
                    User u = loginView.login();
                    if (u == null) continue;

                    if (u instanceof Admin a) adminView.menu();
                    else if (u instanceof Guru g) guruView.menu();
                    else if (u instanceof Siswa s) siswaView.menu(s);
                }
            }
        }

        System.out.println("Terima kasih menggunakan LMS!");
    }
}
