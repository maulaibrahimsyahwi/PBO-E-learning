package view;

import model.*;
import repository.*;
import utils.InputUtil;

import java.util.List;

public class SiswaView {

    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;

    public SiswaView(MateriRepository materiRepo,
                     TugasRepository tugasRepo,
                     UjianRepository ujianRepo,
                     JawabanRepository jawabanRepo,
                     NilaiRepository nilaiRepo) {

        this.materiRepo = materiRepo;
        this.tugasRepo = tugasRepo;
        this.ujianRepo = ujianRepo;
        this.jawabanRepo = jawabanRepo;
        this.nilaiRepo = nilaiRepo;
    }

    public void menu(Siswa s) {

        while (true) {

            System.out.println("\n=== MENU SISWA ===");
            System.out.println("1. Lihat Mata Pelajaran");
            System.out.println("2. Lihat Materi");
            System.out.println("3. Lihat Tugas");
            System.out.println("4. Submit Jawaban");
            System.out.println("5. Lihat Nilai");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            switch (pilih) {
                case 0 -> { return; }
                case 1 -> lihatMataPelajaran(s);
                case 2 -> lihatMateri(s);
                case 3 -> lihatTugas(s);
                case 4 -> submitJawaban(s);
                case 5 -> lihatNilai(s);
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    // ============================================================
    // 1. FITUR BARU: LIHAT MATA PELAJARAN
    // ============================================================

    private void lihatMataPelajaran(Siswa s) {

        Kelas kelas = s.getKelas();
        if (kelas == null) {
            System.out.println("Anda belum memiliki kelas.");
            return;
        }

        System.out.println("\n=== DAFTAR MAPEL KELAS " + kelas.getNamaKelas() + " ===");

        List<MataPelajaran> mapelList = kelas.getDaftarMapel();

        if (mapelList.isEmpty()) {
            System.out.println("Belum ada mata pelajaran untuk kelas ini.");
            return;
        }

        int no = 1;
        for (MataPelajaran m : mapelList) {
            System.out.println(no++ + ". " + m.getNamaMapel());
        }

        int pilih = InputUtil.inputInt("Pilih mata pelajaran: ");
        if (pilih < 1 || pilih > mapelList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        MataPelajaran mapelDipilih = mapelList.get(pilih - 1);

        // masuk submenu
        menuMapel(s, mapelDipilih);
    }

    // ============================================================
    // 2. SUB-MENU: LIHAT MATERI / TUGAS / DISKUSI
    // ============================================================

    private void menuMapel(Siswa s, MataPelajaran mapel) {

        while (true) {
            System.out.println("\n=== MAPEL: " + mapel.getNamaMapel() + " ===");
            System.out.println("1. Lihat Materi");
            System.out.println("2. Lihat Tugas");
            System.out.println("3. Lihat Forum Diskusi");
            System.out.println("0. Kembali");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            switch (pilih) {
                case 0 -> { return; }
                case 1 -> lihatMateriByMapel(s, mapel);
                case 2 -> lihatTugasByMapel(s, mapel);
                case 3 -> lihatDiskusi(s, mapel);
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    // ============================================================
    // 3. LIHAT MATERI BERDASARKAN MAPEL
    // ============================================================

    private void lihatMateriByMapel(Siswa s, MataPelajaran mapel) {
        System.out.println("\n=== MATERI: " + mapel.getNamaMapel() + " ===");

        List<Materi> materiList = materiRepo.getByMapelAndKelas(mapel, s.getKelas());

        if (materiList.isEmpty()) {
            System.out.println("Belum ada materi.");
            return;
        }

        for (Materi m : materiList) {
            System.out.println("- " + m.getJudul() + " (oleh: " + m.getGuru().getNamaLengkap() + ")");
        }
    }

    // ============================================================
    // 4. LIHAT TUGAS BERDASARKAN MAPEL
    // ============================================================

    private void lihatTugasByMapel(Siswa s, MataPelajaran mapel) {
        System.out.println("\n=== TUGAS: " + mapel.getNamaMapel() + " ===");

        List<Tugas> tugasList = tugasRepo.getByMapelAndKelas(mapel, s.getKelas());

        if (tugasList.isEmpty()) {
            System.out.println("Belum ada tugas.");
            return;
        }

        for (Tugas t : tugasList) {
            System.out.println("- " + t.getJudul() + " (deadline: " + t.getDeadline() + ")");
        }
    }

    // ============================================================
    // 5. FORUM DISKUSI (Versi Basic)
    // ============================================================

    private void lihatDiskusi(Siswa s, MataPelajaran mapel) {
        System.out.println("\n=== DISKUSI MAPEL: " + mapel.getNamaMapel() + " ===");
        System.out.println("Fitur forum diskusi sederhana:");

        System.out.println("1. Lihat pesan");
        System.out.println("2. Kirim pesan");
        System.out.println("0. Kembali");

        int pilih = InputUtil.inputInt("Pilih: ");

        switch (pilih) {
            case 0 -> { return; }
            case 1 -> {
                // kalau mau: simpan diskusi di file diskusi.txt
                System.out.println("Belum ada pesan (prototype).");
            }
            case 2 -> {
                String pesan = InputUtil.inputString("Tulis pesan: ");
                System.out.println("Pesan tersimpan (prototype).");
            }
        }
    }


    // ============================================================
    // Fitur Lama: Lihat Materi
    // ============================================================

    private void lihatMateri(Siswa s) {
        List<Materi> materiList = materiRepo.getByKelas(s.getKelas());

        System.out.println("\n=== DAFTAR MATERI ===");
        if (materiList.isEmpty()) {
            System.out.println("Belum ada materi.");
            return;
        }

        for (Materi m : materiList) {
            System.out.println("- " + m.getJudul());
        }
    }

    // ============================================================
    // Fitur Lama: Lihat Tugas
    // ============================================================

    private void lihatTugas(Siswa s) {

        List<Tugas> tugasList = tugasRepo.getByKelas(s.getKelas());

        System.out.println("\n=== DAFTAR TUGAS ===");
        if (tugasList.isEmpty()) {
            System.out.println("Belum ada tugas.");
            return;
        }

        for (Tugas t : tugasList) {
            System.out.println("- " + t.getJudul());
        }
    }

    // ============================================================
    // Submit Jawaban & Lihat Nilai (Fitur lama tetap ada)
    // ============================================================

    private void submitJawaban(Siswa s) {
        System.out.println("Fitur submit jawaban tetap sama.");
    }

    private void lihatNilai(Siswa s) {
        s.generateReport();
    }
}
