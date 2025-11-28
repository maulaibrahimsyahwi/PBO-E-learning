package view;

import model.*;
import repository.*;
import utils.InputUtil;

public class AdminView {

    private UserRepository userRepo;
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;

    public AdminView(UserRepository u, KelasRepository k, MapelRepository m) {
        this.userRepo = u;
        this.kelasRepo = k;
        this.mapelRepo = m;
    }

    public void menu() {
        while (true) {
            System.out.println("\n=== MENU ADMIN ===");
            System.out.println("1. Tambah Guru");
            System.out.println("2. Tambah Siswa");
            System.out.println("3. Tambah Kelas");
            System.out.println("4. Tambah Mapel");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            if (pilih == 0) break;

            switch (pilih) {
                case 1 -> tambahGuru();
                case 2 -> tambahSiswa();
                case 3 -> tambahKelas();
                case 4 -> tambahMapel();
            }
        }
    }

    private void tambahGuru() {
        System.out.println("\n=== TAMBAH GURU ===");
        String id = InputUtil.inputString("ID Guru: ");
        String username = InputUtil.inputString("Username: ");
        String pwd = InputUtil.inputString("Password: ");
        String nama = InputUtil.inputString("Nama Lengkap: ");
        String email = InputUtil.inputString("Email: ");
        String nip = InputUtil.inputString("NIP: ");
        String spes = InputUtil.inputString("Spesialisasi: ");

        Guru g = new Guru(id, username, pwd, nama, email, nip, spes);
        userRepo.addUser(g);
        System.out.println("Guru berhasil ditambahkan!");
    }

    private void tambahSiswa() {
        System.out.println("\n=== TAMBAH SISWA ===");
        String id = InputUtil.inputString("ID Siswa: ");
        String username = InputUtil.inputString("Username: ");
        String pwd = InputUtil.inputString("Password: ");
        String nama = InputUtil.inputString("Nama Lengkap: ");
        String email = InputUtil.inputString("Email: ");
        String nis = InputUtil.inputString("NIS: ");
        String angkatan = InputUtil.inputString("Angkatan: ");

        Siswa s = new Siswa(id, username, pwd, nama, email, nis, angkatan);
        userRepo.addUser(s);
        System.out.println("Siswa berhasil ditambahkan!");
    }

    private void tambahKelas() {
        String id = InputUtil.inputString("ID Kelas: ");
        String nama = InputUtil.inputString("Nama Kelas: ");
        String tingkat = InputUtil.inputString("Tingkat: ");

        kelasRepo.addKelas(new Kelas(id, nama, tingkat));
        System.out.println("Kelas berhasil ditambahkan!");
    }

    private void tambahMapel() {
        String id = InputUtil.inputString("ID Mapel: ");
        String nama = InputUtil.inputString("Nama Mapel: ");
        String desk = InputUtil.inputString("Deskripsi: ");

        mapelRepo.addMapel(new MataPelajaran(id, nama, desk));
        System.out.println("Mapel berhasil ditambahkan!");
    }
}
