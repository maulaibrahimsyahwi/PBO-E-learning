package model;

import java.util.ArrayList;
import java.util.List;

public class Kelas {

    private String idKelas;
    private String namaKelas;
    private String tingkat;

    private List<Siswa> daftarSiswa = new ArrayList<>();
    private List<MataPelajaran> daftarMapel = new ArrayList<>();

    public Kelas(String idKelas, String namaKelas, String tingkat) {
        this.idKelas = idKelas;
        this.namaKelas = namaKelas;
        this.tingkat = tingkat;
    }

    public String getIdKelas() {
        return idKelas;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public String getTingkat() {
        return tingkat;
    }

    public List<Siswa> getDaftarSiswa() {
        return daftarSiswa;
    }

    public void tambahSiswa(Siswa s) {
        if (!daftarSiswa.contains(s)) {
            daftarSiswa.add(s);
        }
    }

    // ===== MAPEL =====

    public void tambahMapel(MataPelajaran m) {
        if (!daftarMapel.contains(m)) {
            daftarMapel.add(m);
        }
    }

    public List<MataPelajaran> getDaftarMapel() {
        return daftarMapel;
    }
}
