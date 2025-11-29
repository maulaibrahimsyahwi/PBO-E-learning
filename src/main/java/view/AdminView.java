package view;

import model.*;
import repository.*;
import utils.InputUtil;

import java.util.List;

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
            System.out.println("5. Assign Guru ke Mapel");
            System.out.println("6. Assign Siswa ke Kelas");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            if (pilih == 0) break;

            switch (pilih) {
                case 1 -> tambahGuru();
                case 2 -> tambahSiswa();
                case 3 -> tambahKelas();
                case 4 -> tambahMapel();
                case 5 -> assignGuruMapel();
                case 6 -> assignSiswaKelas();
                default -> System.out.println("Menu tidak tersedia!");
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
        System.out.println("\n=== TAMBAH KELAS ===");
        String id = InputUtil.inputString("ID Kelas: ");
        String nama = InputUtil.inputString("Nama Kelas: ");
        String tingkat = InputUtil.inputString("Tingkat: ");

        kelasRepo.addKelas(new Kelas(id, nama, tingkat));
        System.out.println("Kelas berhasil ditambahkan!");
    }

    private void tambahMapel() {
        System.out.println("\n=== TAMBAH MAPEL ===");
        String id = InputUtil.inputString("ID Mapel: ");
        String nama = InputUtil.inputString("Nama Mapel: ");
        String desk = InputUtil.inputString("Deskripsi: ");

        mapelRepo.addMapel(new MataPelajaran(id, nama, desk));
        System.out.println("Mapel berhasil ditambahkan!");
    }

    private void assignGuruMapel() {
        System.out.println("\n=== ASSIGN GURU KE MAPEL ===");

        List<User> allUsers = userRepo.getAll();
        List<Guru> guruList = allUsers.stream()
                .filter(u -> u instanceof Guru)
                .map(u -> (Guru) u)
                .toList();

        if (guruList.isEmpty()) {
            System.out.println("Belum ada guru.");
            return;
        }

        int no = 1;
        for (Guru g : guruList) {
            System.out.println(no++ + ". " + g.getIdUser() + " - " + g.getNamaLengkap());
        }
        int pilihGuru = InputUtil.inputInt("Pilih guru: ");
        if (pilihGuru < 1 || pilihGuru > guruList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }
        Guru guru = guruList.get(pilihGuru - 1);

        var mapelList = mapelRepo.getAll();
        if (mapelList.isEmpty()) {
            System.out.println("Belum ada mapel.");
            return;
        }

        no = 1;
        for (MataPelajaran m : mapelList) {
            System.out.println(no++ + ". " + m.getIdMapel() + " - " + m.getNamaMapel());
        }
        int pilihMapel = InputUtil.inputInt("Pilih mapel: ");
        if (pilihMapel < 1 || pilihMapel > mapelList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }
        MataPelajaran mapel = mapelList.get(pilihMapel - 1);

        guru.tambahMapel(mapel);
        userRepo.saveToFile(); // simpan perubahan guru
        System.out.println("Guru " + guru.getNamaLengkap() +
                " sekarang mengajar mapel " + mapel.getNamaMapel());
    }

    /** 🔥 BAGIAN PENTING: relasi siswa–kelas + saveToFile */
    private void assignSiswaKelas() {
        System.out.println("\n=== ASSIGN SISWA KE KELAS ===");

        List<User> allUsers = userRepo.getAll();
        List<Siswa> siswaList = allUsers.stream()
                .filter(u -> u instanceof Siswa)
                .map(u -> (Siswa) u)
                .toList();

        if (siswaList.isEmpty()) {
            System.out.println("Belum ada siswa.");
            return;
        }

        int no = 1;
        for (Siswa s : siswaList) {
            System.out.println(no++ + ". " + s.getIdUser() + " - " + s.getNamaLengkap());
        }
        int pilihSiswa = InputUtil.inputInt("Pilih siswa: ");
        if (pilihSiswa < 1 || pilihSiswa > siswaList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }
        Siswa siswa = siswaList.get(pilihSiswa - 1);

        List<Kelas> kelasList = kelasRepo.getAll();
        if (kelasList.isEmpty()) {
            System.out.println("Belum ada kelas.");
            return;
        }

        no = 1;
        for (Kelas k : kelasList) {
            System.out.println(no++ + ". " + k.getIdKelas() + " - " + k.getNamaKelas());
        }
        int pilihKelas = InputUtil.inputInt("Pilih kelas: ");
        if (pilihKelas < 1 || pilihKelas > kelasList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }
        Kelas kelas = kelasList.get(pilihKelas - 1);

        // set relasi di object + simpan idKelas
        siswa.setKelas(kelas);                     // ini sekaligus mengisi idKelas di Siswa
        kelas.tambahSiswa(siswa);

        // simpan perubahan ke file users.txt
        userRepo.saveToFile();

        System.out.println("Siswa " + siswa.getNamaLengkap() +
                " sekarang berada di kelas " + kelas.getNamaKelas());
    }
}
