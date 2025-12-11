package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public void tambahMapel(MataPelajaran m) {
        if (!daftarMapel.contains(m)) {
            daftarMapel.add(m);
        }
    }

    public List<MataPelajaran> getDaftarMapel() {
        return daftarMapel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kelas kelas = (Kelas) o;
        return Objects.equals(idKelas, kelas.idKelas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idKelas);
    }
}