package model;

import java.util.ArrayList;
import java.util.List;

public class Guru extends User {
    private String nip;
    private String spesialisasi;
    private List<MataPelajaran> mapelDiampu = new ArrayList<>();

    public Guru(String idUser, String username, String password,
                String namaLengkap, String email,
                String nip, String spesialisasi) {

        super(idUser, username, password, namaLengkap, email);
        this.nip = nip;
        this.spesialisasi = spesialisasi;
    }

    @Override
    public void tampilkanMenu() {
        System.out.println("=== Menu Guru ===");
        System.out.println("1. Kelola Materi");
        System.out.println("2. Kelola Tugas");
        System.out.println("3. Kelola Ujian");
        System.out.println("4. Lihat Jawaban");
        System.out.println("5. Nilai Jawaban");
        System.out.println("0. Logout");
    }

    public String getNip() {
        return nip;
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public List<MataPelajaran> getMapelDiampu() {
        return mapelDiampu;
    }

    public void tambahMapel(MataPelajaran mapel) {
        if (!mapelDiampu.contains(mapel)) {
            mapelDiampu.add(mapel);
        }
    }
}
