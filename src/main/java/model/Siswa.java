package model;

import java.util.ArrayList;
import java.util.List;

public class Siswa extends User implements IReportable {

    private String nis;
    private String angkatan;
    private Kelas kelas;
    private List<Nilai> daftarNilai = new ArrayList<>();

    public Siswa(String idUser, String username, String password,
                 String namaLengkap, String email,
                 String nis, String angkatan) {

        super(idUser, username, password, namaLengkap, email);
        this.nis = nis;
        this.angkatan = angkatan;
    }

    @Override
    public void tampilkanMenu() {
        System.out.println("=== Menu Siswa ===");
        System.out.println("1. Akses Materi");
        System.out.println("2. Lihat Tugas/Ujian");
        System.out.println("3. Submit Jawaban");
        System.out.println("4. Lihat Nilai");
        System.out.println("5. Kelola Profil");
        System.out.println("0. Logout");
    }

    @Override
    public void generateReport() {
        System.out.println("=== Laporan Nilai Siswa ===");
        if (daftarNilai.isEmpty()) {
            System.out.println("Belum ada nilai.");
            return;
        }

        for (Nilai n : daftarNilai) {
            String namaTugas = (n.getTugas() != null) ? n.getTugas().getJudul() : "-";
            System.out.println("Tugas: " + namaTugas +
                    " | Nilai: " + n.getNilaiAngka() +
                    " (" + n.getNilaiHuruf() + ")");
        }
    }

    public void tambahNilai(Nilai n) {
        daftarNilai.add(n);
    }

    public String getNis() {
        return nis;
    }

    public String getAngkatan() {
        return angkatan;
    }

    public Kelas getKelas() {
        return kelas;
    }

    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }
}
