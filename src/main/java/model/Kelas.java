package model;

import java.util.ArrayList;
import java.util.List;

public class Kelas {
    private String idKelas;
    private String namaKelas;
    private String tingkat;
    private List<Siswa> daftarSiswa = new ArrayList<>();

    public Kelas(String idKelas, String namaKelas, String tingkat) {
        this.idKelas = idKelas;
        this.namaKelas = namaKelas;
        this.tingkat = tingkat;
    }

    public void tambahSiswa(Siswa s) {
        daftarSiswa.add(s);
    }

    // --- GETTER YANG DIPERBAIKI ---
    public String getIdKelas() { return idKelas; }

    public List<Siswa> getDaftarSiswa() {
        return daftarSiswa;
    }
}