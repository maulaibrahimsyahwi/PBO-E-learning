package view;

import model.*;
import repository.*;
import utils.IdUtil;
import utils.InputUtil;

import java.util.List;
import java.util.stream.Collectors;

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
            System.out.println("5. Assign Guru (Mapel & Kelas)");
            System.out.println("6. Assign Siswa ke Kelas");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            if (pilih == 0) break;

            switch (pilih) {
                case 1 -> tambahGuru();
                case 2 -> tambahSiswa();
                case 3 -> tambahKelas();
                case 4 -> tambahMapel();
                case 5 -> assignGuru();
                case 6 -> assignSiswaKelas();
                default -> System.out.println("Menu tidak tersedia!");
            }
        }
    }

    private void tambahGuru() {
        System.out.println("\n=== TAMBAH GURU ===");
        String username = InputUtil.inputString("Username: ");
        String pwd = InputUtil.inputString("Password: ");
        String nama = InputUtil.inputString("Nama Lengkap: ");
        String email = InputUtil.inputString("Email: ");
        String nip = InputUtil.inputString("NIP: ");
        String spes = InputUtil.inputString("Spesialisasi: ");

        String id = IdUtil.generate(); 
        Guru g = new Guru(id, username, pwd, nama, email, nip, spes);
        userRepo.addUser(g);
        System.out.println("Guru berhasil ditambahkan! (ID: " + id + ")");
    }

    private void tambahSiswa() {
        System.out.println("\n=== TAMBAH SISWA ===");
        String username = InputUtil.inputString("Username: ");
        String pwd = InputUtil.inputString("Password: ");
        String nama = InputUtil.inputString("Nama Lengkap: ");
        String email = InputUtil.inputString("Email: ");
        String nis = InputUtil.inputString("NIS: ");
        String angkatan = InputUtil.inputString("Angkatan: ");

        String id = IdUtil.generate();
        Siswa s = new Siswa(id, username, pwd, nama, email, nis, angkatan);
        userRepo.addUser(s);
        System.out.println("Siswa berhasil ditambahkan! (ID: " + id + ")");
    }

    private void tambahKelas() {
        System.out.println("\n=== TAMBAH KELAS ===");
        String nama = InputUtil.inputString("Nama Kelas: ");
        
        String tingkat;
        while (true) {
            tingkat = InputUtil.inputString("Tingkat (10/11/12): ");
            if (tingkat.equals("10") || tingkat.equals("11") || tingkat.equals("12")) {
                break;
            }
            System.out.println("Input salah! Tingkat hanya boleh 10, 11, atau 12.");
        }

        String id = IdUtil.generate();
        kelasRepo.addKelas(new Kelas(id, nama, tingkat));
        System.out.println("Kelas berhasil ditambahkan! (ID: " + id + ")");
    }

    private void tambahMapel() {
        System.out.println("\n=== TAMBAH MAPEL ===");
        String nama = InputUtil.inputString("Nama Mapel: ");
        String desk = InputUtil.inputString("Deskripsi: ");

        String tingkat;
        while (true) {
            tingkat = InputUtil.inputString("Untuk Tingkat Kelas (10/11/12): ");
            if (tingkat.equals("10") || tingkat.equals("11") || tingkat.equals("12")) {
                break;
            }
            System.out.println("Input salah! Tingkat hanya boleh 10, 11, atau 12.");
        }

        String id = IdUtil.generate();
        MataPelajaran m = new MataPelajaran(id, nama, desk, tingkat);
        mapelRepo.addMapel(m);

        // 🔥 PERBAIKAN UTAMA: Langsung update kelas yang relevan saat ini juga
        int count = 0;
        for (Kelas k : kelasRepo.getAll()) {
            if (k.getTingkat().equals(tingkat)) {
                k.tambahMapel(m);
                count++;
            }
        }

        System.out.println("Mapel berhasil ditambahkan! (ID: " + id + ")");
        System.out.println("Mapel otomatis didistribusikan ke " + count + " kelas tingkat " + tingkat + ".");
    }

    private void assignGuru() {
        System.out.println("\n=== ASSIGN GURU (MAPEL & KELAS) ===");

        List<User> allUsers = userRepo.getAll();
        List<Guru> guruList = allUsers.stream()
                .filter(u -> u instanceof Guru)
                .map(u -> (Guru) u)
                .collect(Collectors.toList());

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
            System.out.println(no++ + ". " + m.getIdMapel() + " - " + m.getNamaMapel() + " (Kls " + m.getTingkat() + ")");
        }
        int pilihMapel = InputUtil.inputInt("Pilih mapel untuk diajarkan: ");
        if (pilihMapel < 1 || pilihMapel > mapelList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }
        MataPelajaran mapel = mapelList.get(pilihMapel - 1);

        List<Kelas> kelasList = kelasRepo.getAll();
        if (kelasList.isEmpty()) {
            System.out.println("Belum ada kelas.");
            return;
        }

        no = 1;
        for (Kelas k : kelasList) {
            System.out.println(no++ + ". " + k.getIdKelas() + " - " + k.getNamaKelas() + " (Tingkat " + k.getTingkat() + ")");
        }
        int pilihKelas = InputUtil.inputInt("Pilih kelas tujuan: ");
        if (pilihKelas < 1 || pilihKelas > kelasList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }
        Kelas kelas = kelasList.get(pilihKelas - 1);

        if (!mapel.getTingkat().equals("-") && !mapel.getTingkat().equals(kelas.getTingkat())) {
            System.out.println("[INFO] Perhatian: Anda meng-assign Mapel Tingkat " + mapel.getTingkat() + 
                               " ke Kelas Tingkat " + kelas.getTingkat());
        }

        guru.tambahMapel(mapel);
        guru.tambahKelas(kelas);
        
        userRepo.saveToFile();

        System.out.println("Berhasil! Guru " + guru.getNamaLengkap() + 
                " ditugaskan mengajar " + mapel.getNamaMapel() + 
                " di kelas " + kelas.getNamaKelas());
    }

    private void assignSiswaKelas() {
        System.out.println("\n=== ASSIGN SISWA KE KELAS ===");

        List<User> allUsers = userRepo.getAll();
        List<Siswa> siswaList = allUsers.stream()
                .filter(u -> u instanceof Siswa)
                .map(u -> (Siswa) u)
                .collect(Collectors.toList());

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
            System.out.println(no++ + ". " + k.getIdKelas() + " - " + k.getNamaKelas() + " (Tingkat " + k.getTingkat() + ")");
        }
        int pilihKelas = InputUtil.inputInt("Pilih kelas: ");
        if (pilihKelas < 1 || pilihKelas > kelasList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }
        Kelas kelas = kelasList.get(pilihKelas - 1);

        siswa.setKelas(kelas);                     
        kelas.tambahSiswa(siswa);

        userRepo.saveToFile();

        System.out.println("Siswa " + siswa.getNamaLengkap() +
                " sekarang berada di kelas " + kelas.getNamaKelas());
    }
}