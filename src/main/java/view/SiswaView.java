package view;

import model.Materi;
import model.Tugas;
import model.Ujian;
import model.Siswa;
import model.Jawaban;
import model.Nilai;
import repository.MateriRepository;
import repository.TugasRepository;
import repository.UjianRepository;
import repository.JawabanRepository;
import repository.NilaiRepository;
import utils.InputUtil;

import java.util.List;

public class SiswaView {

    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;

    public SiswaView(MateriRepository m,
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

    public void menu(Siswa siswa) {
        while (true) {
            System.out.println("\n=== MENU SISWA ===");
            System.out.println("1. Lihat Materi");
            System.out.println("2. Lihat Tugas");
            System.out.println("3. Lihat Ujian");
            System.out.println("4. Submit Jawaban");
            System.out.println("5. Lihat Nilai");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            if (pilih == 0) break;

            switch (pilih) {
                case 1 -> lihatMateri();
                case 2 -> lihatTugas();
                case 3 -> lihatUjian();
                case 4 -> submitJawaban(siswa);
                case 5 -> lihatNilai(siswa);
                default -> System.out.println("Menu tidak tersedia!");
            }
        }
    }

    // ========== LIHAT MATERI ==========
    private void lihatMateri() {
        List<Materi> list = materiRepo.getAll();
        System.out.println("\n=== DAFTAR MATERI ===");

        if (list.isEmpty()) {
            System.out.println("Belum ada materi yang diunggah guru.");
            return;
        }

        int no = 1;
        for (Materi m : list) {
            System.out.println(no++ + ". " + m.getJudul());
        }

        int pilih = InputUtil.inputInt("Pilih nomor materi untuk lihat detail (0 untuk kembali): ");
        if (pilih == 0) return;
        if (pilih < 1 || pilih > list.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        Materi m = list.get(pilih - 1);
        System.out.println("\n=== DETAIL MATERI ===");
        System.out.println("ID       : " + m.getIdMateri());
        System.out.println("Judul    : " + m.getJudul());
        System.out.println("Deskripsi: " + m.getDeskripsi());
        System.out.println("File     : " + m.getFileMateri());
    }

    // ========== LIHAT TUGAS ==========
    private void lihatTugas() {
        List<Tugas> list = tugasRepo.getAll();
        System.out.println("\n=== DAFTAR TUGAS ===");

        if (list.isEmpty()) {
            System.out.println("Belum ada tugas yang dibuat guru.");
            return;
        }

        int no = 1;
        for (Tugas t : list) {
            System.out.println(no++ + ". " + t.getIdTugas() + " - " + t.getJudul());
        }
    }

    // ========== LIHAT UJIAN ==========
    private void lihatUjian() {
        List<Ujian> list = ujianRepo.getAll();
        System.out.println("\n=== DAFTAR UJIAN ===");

        if (list.isEmpty()) {
            System.out.println("Belum ada ujian yang dijadwalkan.");
            return;
        }

        int no = 1;
        for (Ujian u : list) {
            System.out.println(no++ + ". " + u.getJenisUjian());
        }
    }

    // ========== SUBMIT JAWABAN ==========
    private void submitJawaban(Siswa siswa) {
        System.out.println("\n=== SUBMIT JAWABAN ===");

        // Pilih tugas dulu
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
        Tugas tugas = tugasList.get(pilih - 1);

        String id = InputUtil.inputString("ID Jawaban: ");
        String file = InputUtil.inputString("Nama File Jawaban: ");

        jawabanRepo.addJawaban(new Jawaban(id, siswa, tugas, file));
        System.out.println("Jawaban terkirim untuk tugas: " + tugas.getJudul());
    }

    // ========== LIHAT NILAI ==========
    private void lihatNilai(Siswa siswa) {
        System.out.println("\n=== NILAI SAYA ===");
        List<Nilai> nilaiList = nilaiRepo.findBySiswa(siswa.getIdUser());

        if (nilaiList.isEmpty()) {
            System.out.println("Belum ada nilai.");
            return;
        }

        for (Nilai n : nilaiList) {
            String namaTugas = (n.getTugas() != null) ? n.getTugas().getJudul() : "-";
            System.out.println("- Tugas: " + namaTugas
                    + " | Nilai: " + n.getNilaiAngka()
                    + " (" + n.getNilaiHuruf() + ")"
                    + " | Keterangan: " + n.getKeterangan());
        }
    }
}
