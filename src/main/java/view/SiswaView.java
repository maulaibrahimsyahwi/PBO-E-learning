package view;

import model.*;
import repository.*;
import utils.InputUtil;

import java.util.List;
import java.util.stream.Collectors;

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
                case 1 -> lihatMateri(siswa);
                case 2 -> lihatTugas(siswa);
                case 3 -> lihatUjian(siswa);
                case 4 -> submitJawaban(siswa);
                case 5 -> lihatNilai(siswa);
                default -> System.out.println("Menu tidak tersedia!");
            }
        }
    }

    private void cekKelas(Siswa siswa) {
        if (siswa.getKelas() == null) {
            System.out.println("Anda belum memiliki kelas. Silakan hubungi admin.");
        }
    }

    private void lihatMateri(Siswa siswa) {
        System.out.println("\n=== DAFTAR MATERI KELAS SAYA ===");
        if (siswa.getKelas() == null) {
            System.out.println("Anda belum memiliki kelas. Silakan hubungi admin.");
            return;
        }

        List<Materi> list = materiRepo.findByKelas(siswa.getKelas());
        if (list.isEmpty()) {
            System.out.println("Belum ada materi untuk kelas Anda.");
            return;
        }

        int no = 1;
        for (Materi m : list) {
            System.out.println(no++ + ". " + m.getJudul()
                    + " | Mapel: " + (m.getMapel() != null ? m.getMapel().getNamaMapel() : "-"));
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
        System.out.println("Mapel    : " + (m.getMapel() != null ? m.getMapel().getNamaMapel() : "-"));
        System.out.println("Kelas    : " + siswa.getKelas().getNamaKelas());
    }

    private void lihatTugas(Siswa siswa) {
        System.out.println("\n=== DAFTAR TUGAS KELAS SAYA ===");
        if (siswa.getKelas() == null) {
            System.out.println("Anda belum memiliki kelas. Silakan hubungi admin.");
            return;
        }

        List<Tugas> list = tugasRepo.findByKelas(siswa.getKelas());
        if (list.isEmpty()) {
            System.out.println("Belum ada tugas untuk kelas Anda.");
            return;
        }

        int no = 1;
        for (Tugas t : list) {
            System.out.println(no++ + ". " + t.getIdTugas() + " - " + t.getJudul()
                    + " | Deadline: " + t.getDeadline());
        }
    }

    private void lihatUjian(Siswa siswa) {
        System.out.println("\n=== DAFTAR UJIAN KELAS SAYA ===");
        if (siswa.getKelas() == null) {
            System.out.println("Anda belum memiliki kelas. Silakan hubungi admin.");
            return;
        }

        List<Ujian> list = ujianRepo.findByKelas(siswa.getKelas());
        if (list.isEmpty()) {
            System.out.println("Belum ada ujian untuk kelas Anda.");
            return;
        }

        int no = 1;
        for (Ujian u : list) {
            System.out.println(no++ + ". " + u.getIdUjian() + " - " + u.getJenisUjian()
                    + " | Tanggal: " + u.getTanggal());
        }
    }

    private void submitJawaban(Siswa siswa) {
        System.out.println("\n=== SUBMIT JAWABAN ===");
        if (siswa.getKelas() == null) {
            System.out.println("Anda belum memiliki kelas. Silakan hubungi admin.");
            return;
        }

        List<Tugas> tugasList = tugasRepo.findByKelas(siswa.getKelas());
        if (tugasList.isEmpty()) {
            System.out.println("Belum ada tugas untuk kelas Anda.");
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
