package view;

import model.*;
import repository.*;
import utils.InputUtil;
import utils.DateUtil;
import java.time.LocalDate;

public class GuruView {

    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;

    public GuruView(MateriRepository m, TugasRepository t, UjianRepository u) {
        this.materiRepo = m;
        this.tugasRepo = t;
        this.ujianRepo = u;
    }

    public void menu() {
        while (true) {
            System.out.println("\n=== MENU GURU ===");
            System.out.println("1. Tambah Materi");
            System.out.println("2. Buat Tugas");
            System.out.println("3. Buat Ujian");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            if (pilih == 0) break;

            switch (pilih) {
                case 1 -> tambahMateri();
                case 2 -> tambahTugas();
                case 3 -> tambahUjian();
            }
        }
    }

    private void tambahMateri() {
        String id = InputUtil.inputString("ID Materi: ");
        String judul = InputUtil.inputString("Judul: ");
        String desk = InputUtil.inputString("Deskripsi: ");
        String file = InputUtil.inputString("Nama File: ");
        materiRepo.addMateri(new Materi(id, judul, desk, file));
    }

    private void tambahTugas() {
        String id = InputUtil.inputString("ID Tugas: ");
        String judul = InputUtil.inputString("Judul: ");
        String desk = InputUtil.inputString("Deskripsi: ");
        String tgl = InputUtil.inputString("Deadline (yyyy-mm-dd): ");

        tugasRepo.addTugas(new Tugas(id, judul, desk, DateUtil.parse(tgl)));
    }

    private void tambahUjian() {
        String id = InputUtil.inputString("ID Ujian: ");
        String jenis = InputUtil.inputString("Jenis Ujian: ");
        String tgl = InputUtil.inputString("Tanggal (yyyy-mm-dd): ");
        int durasi = InputUtil.inputInt("Durasi (menit): ");

        ujianRepo.addUjian(new Ujian(id, jenis, DateUtil.parse(tgl), durasi));
    }
}
