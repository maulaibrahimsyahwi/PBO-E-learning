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
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;

    public GuruView(MateriRepository m,
                    TugasRepository t,
                    UjianRepository u,
                    JawabanRepository j,
                    NilaiRepository n,
                    KelasRepository k,
                    MapelRepository mapelRepo) {
        this.materiRepo = m;
        this.tugasRepo = t;
        this.ujianRepo = u;
        this.jawabanRepo = j;
        this.nilaiRepo = n;
        this.kelasRepo = k;
        this.mapelRepo = mapelRepo;
    }

    public void menu(Guru guru) {
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
                case 1 -> tambahMateri(guru);
                case 2 -> tambahTugas(guru);
                case 3 -> tambahUjian(guru);
                case 4 -> lihatJawabanTugas();
                case 5 -> beriNilaiTugas();
                default -> System.out.println("Menu tidak tersedia!");
            }
        }
    }

    private MataPelajaran pilihMapelUntukGuru(Guru guru) {
        List<MataPelajaran> mapelGuru = guru.getMapelDiampu();
        List<MataPelajaran> listPakai;

        if (!mapelGuru.isEmpty()) {
            listPakai = mapelGuru;
            System.out.println("Pilih mapel (yang Anda ampu):");
        } else {
            listPakai = mapelRepo.getAll();
            System.out.println("Pilih mapel:");
        }

        if (listPakai.isEmpty()) {
            System.out.println("Belum ada mapel.");
            return null;
        }

        int no = 1;
        for (MataPelajaran m : listPakai) {
            System.out.println(no++ + ". " + m.getIdMapel() + " - " + m.getNamaMapel());
        }
        int pilih = InputUtil.inputInt("Pilih mapel: ");
        if (pilih < 1 || pilih > listPakai.size()) {
            System.out.println("Pilihan tidak valid.");
            return null;
        }
        return listPakai.get(pilih - 1);
    }

    private Kelas pilihKelas() {
        List<Kelas> kelasList = kelasRepo.getAll();
        if (kelasList.isEmpty()) {
            System.out.println("Belum ada kelas.");
            return null;
        }

        System.out.println("Pilih kelas:");
        int no = 1;
        for (Kelas k : kelasList) {
            System.out.println(no++ + ". " + k.getIdKelas() + " - " + k.getNamaKelas());
        }
        int pilih = InputUtil.inputInt("Pilih kelas: ");
        if (pilih < 1 || pilih > kelasList.size()) {
            System.out.println("Pilihan tidak valid.");
            return null;
        }
        return kelasList.get(pilih - 1);
    }

    private void tambahMateri(Guru guru) {
        System.out.println("\n=== TAMBAH MATERI ===");

        MataPelajaran mapel = pilihMapelUntukGuru(guru);
        if (mapel == null) return;

        Kelas kelas = pilihKelas();
        if (kelas == null) return;

        String id = InputUtil.inputString("ID Materi: ");
        String judul = InputUtil.inputString("Judul: ");
        String desk = InputUtil.inputString("Deskripsi: ");
        String file = InputUtil.inputString("Nama File (misal: materi1.pdf): ");

        Materi m = new Materi(id, judul, desk, file);
        m.setGuru(guru);
        m.setMapel(mapel);
        m.setKelas(kelas);

        materiRepo.addMateri(m);
        System.out.println("Materi berhasil ditambahkan untuk kelas " + kelas.getNamaKelas()
                + " | Mapel: " + mapel.getNamaMapel());
    }

    private void tambahTugas(Guru guru) {
        System.out.println("\n=== BUAT TUGAS ===");

        MataPelajaran mapel = pilihMapelUntukGuru(guru);
        if (mapel == null) return;

        Kelas kelas = pilihKelas();
        if (kelas == null) return;

        String id = InputUtil.inputString("ID Tugas: ");
        String judul = InputUtil.inputString("Judul: ");
        String desk = InputUtil.inputString("Deskripsi: ");
        String tgl = InputUtil.inputString("Deadline (yyyy-mm-dd): ");

        Tugas tugas = new Tugas(id, judul, desk, DateUtil.parse(tgl));
        tugas.setGuru(guru);
        tugas.setMapel(mapel);
        tugas.setKelas(kelas);

        tugasRepo.addTugas(tugas);
        System.out.println("Tugas berhasil dibuat untuk kelas " + kelas.getNamaKelas()
                + " | Mapel: " + mapel.getNamaMapel());
    }

    private void tambahUjian(Guru guru) {
        System.out.println("\n=== BUAT UJIAN ===");

        MataPelajaran mapel = pilihMapelUntukGuru(guru);
        if (mapel == null) return;

        Kelas kelas = pilihKelas();
        if (kelas == null) return;

        String id = InputUtil.inputString("ID Ujian: ");
        String jenis = InputUtil.inputString("Jenis Ujian: ");
        String tgl = InputUtil.inputString("Tanggal (yyyy-mm-dd): ");
        int durasi = InputUtil.inputInt("Durasi (menit): ");

        Ujian ujian = new Ujian(id, jenis, DateUtil.parse(tgl), durasi);
        ujian.setGuru(guru);
        ujian.setMapel(mapel);
        ujian.setKelas(kelas);

        ujianRepo.addUjian(ujian);
        System.out.println("Ujian berhasil dibuat untuk kelas " + kelas.getNamaKelas()
                + " | Mapel: " + mapel.getNamaMapel());
    }

    // ==== bagian lihatJawabanTugas() & beriNilaiTugas() bisa pakai versi yang sudah kita buat sebelumnya ====
    // (tidak aku ulang di sini supaya jawabannya tidak kepanjangan)
    // Kalau kamu mau, nanti aku kirim FULL GuruView dengan bagian nilai juga.
    
    private void lihatJawabanTugas() {
        // ... pakai versi yang sudah kita buat sebelumnya (tidak berubah banyak)
    }

    private void beriNilaiTugas() {
        // ... pakai versi yang sudah kita buat sebelumnya (tidak berubah banyak)
    }
}
