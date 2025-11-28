package view;

import model.*;
import repository.*;
import utils.InputUtil;

public class SiswaView {

    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;

    public SiswaView(MateriRepository m, TugasRepository t, UjianRepository u, JawabanRepository j) {
        this.materiRepo = m;
        this.tugasRepo = t;
        this.ujianRepo = u;
        this.jawabanRepo = j;
    }

    public void menu(Siswa siswa) {
        while (true) {
            System.out.println("\n=== MENU SISWA ===");
            System.out.println("1. Lihat Materi");
            System.out.println("2. Lihat Tugas");
            System.out.println("3. Lihat Ujian");
            System.out.println("4. Submit Jawaban");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            if (pilih == 0) break;

            switch (pilih) {
                case 1 -> lihatMateri();
                case 2 -> lihatTugas();
                case 3 -> lihatUjian();
                case 4 -> submitJawaban(siswa);
            }
        }
    }

    private void lihatMateri() {
        for (Materi m : materiRepo.getAll()) {
            System.out.println("- " + m.getJudul());
        }
    }

    private void lihatTugas() {
        for (Tugas t : tugasRepo.getAll()) {
            System.out.println("- " + t.getJudul());
        }
    }

    private void lihatUjian() {
        for (Ujian u : ujianRepo.getAll()) {
            System.out.println("- " + u.getJenisUjian());
        }
    }

    private void submitJawaban(Siswa siswa) {
        String id = InputUtil.inputString("ID Jawaban: ");
        String file = InputUtil.inputString("Nama File Jawaban: ");
        jawabanRepo.addJawaban(new Jawaban(id, siswa, file));
        System.out.println("Jawaban terkirim!");
    }
}
