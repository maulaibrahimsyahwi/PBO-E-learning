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
        NilaiRepository nilaiRepo = new NilaiRepository();

        // =====================================================
        //                 ADMIN DEFAULT DI SINI
        // =====================================================
        Admin defaultAdmin = new Admin(
                "A001",
                "admin",
                "admin",
                "Administrator",
                "admin@lms.com"
        );
        userRepo.addUser(defaultAdmin);
        System.out.println("Admin default berhasil ditambahkan: admin/admin");
        // =====================================================

        LoginView loginView = new LoginView(userRepo);
        AdminView adminView = new AdminView(userRepo, kelasRepo, mapelRepo);
        GuruView guruView = new GuruView(materiRepo, tugasRepo, ujianRepo, jawabanRepo, nilaiRepo);
        SiswaView siswaView = new SiswaView(materiRepo, tugasRepo, ujianRepo, jawabanRepo, nilaiRepo);

        while (true) {
            System.out.println("\n=== LMS SMK NUSANTARA ===");
            System.out.println("1. Login");
            System.out.println("2. Registrasi");
            System.out.println("0. Keluar");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            if (pilih == 0) break;

            switch (pilih) {
                case 1 -> {
                    User u = loginView.login();
                    if (u == null) continue;

                    if (u instanceof Admin) adminView.menu();
                    else if (u instanceof Guru) guruView.menu();
                    else if (u instanceof Siswa s) siswaView.menu(s);
                }
                case 2 -> registrasi(userRepo);
                default -> System.out.println("Menu tidak tersedia!");
            }
        }

        System.out.println("Terima kasih menggunakan LMS!");
    }

    private static void registrasi(UserRepository userRepo) {
        System.out.println("\n=== REGISTRASI AKUN ===");
        System.out.println("Pilih tipe akun:");
        System.out.println("1. Guru");
        System.out.println("2. Siswa");
        System.out.println("0. Batal");

        int tipe = InputUtil.inputInt("Pilih: ");
        if (tipe == 0) return;

        String id = InputUtil.inputString("ID User: ");
        String username = InputUtil.inputString("Username: ");
        String password = InputUtil.inputString("Password: ");
        String nama = InputUtil.inputString("Nama Lengkap: ");
        String email = InputUtil.inputString("Email: ");

        if (tipe == 1) {
            String nip = InputUtil.inputString("NIP: ");
            String spes = InputUtil.inputString("Spesialisasi: ");
            Guru g = new Guru(id, username, password, nama, email, nip, spes);
            userRepo.addUser(g);
            System.out.println("Registrasi Guru berhasil!");
        } else if (tipe == 2) {
            String nis = InputUtil.inputString("NIS: ");
            String angkatan = InputUtil.inputString("Angkatan: ");
            Siswa s = new Siswa(id, username, password, nama, email, nis, angkatan);
            userRepo.addUser(s);
            System.out.println("Registrasi Siswa berhasil!");
        } else {
            System.out.println("Pilihan tidak valid!");
        }
    }
}
