package view;

import model.*;
import repository.*;
import utils.InputUtil;
import utils.DateUtil;

import java.util.List;

public class GuruView {

    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;

    public GuruView(MateriRepository m,
                    TugasRepository t,
                    UjianRepository u,
                    JawabanRepository j,
                    NilaiRepository n) {
        this.materiRepo = m;
        this.tugasRepo = t;
        this.ujianRepo = u;
        this.jawabanRepo = j;
        this.nilaiRepo = n;
    }

    public void menu() {
        while (true) {
            System.out.println("\n=== MENU GURU ===");
            System.out.println("1. Tambah Materi");
            System.out.println("2. Buat Tugas");
            System.out.println("3. Buat Ujian");
            System.out.println("4. Lihat Jawaban Tugas");
            System.out.println("5. Beri Nilai Tugas");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            if (pilih == 0) break;

            switch (pilih) {
                case 1 -> tambahMateri();
                case 2 -> tambahTugas();
                case 3 -> tambahUjian();
                case 4 -> lihatJawabanTugas();
                case 5 -> beriNilaiTugas();
                default -> System.out.println("Menu tidak tersedia!");
            }
        }
    }

    private void tambahMateri() {
        System.out.println("\n=== TAMBAH MATERI ===");
        String id = InputUtil.inputString("ID Materi: ");
        String judul = InputUtil.inputString("Judul: ");
        String desk = InputUtil.inputString("Deskripsi: ");
        String file = InputUtil.inputString("Nama File (misal: materi1.pdf): ");
        materiRepo.addMateri(new Materi(id, judul, desk, file));
        System.out.println("Materi berhasil ditambahkan!");
    }

    private void tambahTugas() {
        System.out.println("\n=== BUAT TUGAS ===");
        String id = InputUtil.inputString("ID Tugas: ");
        String judul = InputUtil.inputString("Judul: ");
        String desk = InputUtil.inputString("Deskripsi: ");
        String tgl = InputUtil.inputString("Deadline (yyyy-mm-dd): ");
        tugasRepo.addTugas(new Tugas(id, judul, desk, DateUtil.parse(tgl)));
        System.out.println("Tugas berhasil dibuat!");
    }

    private void tambahUjian() {
        System.out.println("\n=== BUAT UJIAN ===");
        String id = InputUtil.inputString("ID Ujian: ");
        String jenis = InputUtil.inputString("Jenis Ujian: ");
        String tgl = InputUtil.inputString("Tanggal (yyyy-mm-dd): ");
        int durasi = InputUtil.inputInt("Durasi (menit): ");
        ujianRepo.addUjian(new Ujian(id, jenis, DateUtil.parse(tgl), durasi));
        System.out.println("Ujian berhasil dibuat!");
    }

    private void lihatJawabanTugas() {
        System.out.println("\n=== LIHAT JAWABAN TUGAS ===");
        List<Tugas> tugasList = tugasRepo.getAll();
        if (tugasList.isEmpty()) {
            System.out.println("Belum ada tugas.");
            return;
        }

        int no = 1;
        for (Tugas t : tugasList) {
            System.out.println(no++ + ". " + t.getIdTugas() + " - " + t.getJudul());
        }
        int pilih = InputUtil.inputInt("Pilih nomor tugas: ");
        if (pilih < 1 || pilih > tugasList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        Tugas t = tugasList.get(pilih - 1);
        List<Jawaban> jawabanList = jawabanRepo.findByTugas(t.getIdTugas());

        if (jawabanList.isEmpty()) {
            System.out.println("Belum ada jawaban untuk tugas ini.");
            return;
        }

        System.out.println("\nJawaban untuk tugas: " + t.getJudul());
        int noJ = 1;
        for (Jawaban j : jawabanList) {
            System.out.println(noJ++ + ". " + j.getIdJawaban()
                    + " | Siswa: " + j.getSiswa().getNamaLengkap()
                    + " | File: " + j.getFileJawaban()
                    + " | Waktu: " + j.getWaktuKirim());
        }
    }

    private void beriNilaiTugas() {
        System.out.println("\n=== BERIKAN NILAI TUGAS ===");
        List<Tugas> tugasList = tugasRepo.getAll();
        if (tugasList.isEmpty()) {
            System.out.println("Belum ada tugas.");
            return;
        }

        int no = 1;
        for (Tugas t : tugasList) {
            System.out.println(no++ + ". " + t.getIdTugas() + " - " + t.getJudul());
        }
        int pilihTugas = InputUtil.inputInt("Pilih nomor tugas: ");
        if (pilihTugas < 1 || pilihTugas > tugasList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        Tugas tugas = tugasList.get(pilihTugas - 1);
        List<Jawaban> jawabanList = jawabanRepo.findByTugas(tugas.getIdTugas());

        if (jawabanList.isEmpty()) {
            System.out.println("Belum ada jawaban untuk tugas ini.");
            return;
        }

        System.out.println("\nPilih jawaban yang ingin dinilai:");
        int noJ = 1;
        for (Jawaban j : jawabanList) {
            System.out.println(noJ++ + ". " + j.getIdJawaban()
                    + " | " + j.getSiswa().getNamaLengkap()
                    + " | File: " + j.getFileJawaban());
        }
        int pilihJ = InputUtil.inputInt("Pilih nomor jawaban: ");
        if (pilihJ < 1 || pilihJ > jawabanList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        Jawaban jawaban = jawabanList.get(pilihJ - 1);
        double angka = Double.parseDouble(InputUtil.inputString("Nilai angka: "));
        String ket = InputUtil.inputString("Keterangan: ");

        String idNilai = "N" + System.currentTimeMillis();
        Nilai nilai = new Nilai(idNilai, jawaban.getSiswa(), tugas, angka, ket);
        nilaiRepo.addNilai(nilai);

        System.out.println("Nilai berhasil disimpan untuk " + jawaban.getSiswa().getNamaLengkap());
    }
}
