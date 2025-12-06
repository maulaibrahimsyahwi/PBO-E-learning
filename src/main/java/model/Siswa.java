package model;

import java.util.ArrayList;
import java.util.List;

public class Siswa extends User {

    private String nis;
    private String angkatan;

    // Relasi objek ke Kelas
    private Kelas kelas;
    // ID Kelas untuk referensi database
    private String idKelas;

    private List<Nilai> daftarNilai = new ArrayList<>();

    public Siswa(String idUser, String username, String password,
                 String namaLengkap, String email,
                 String nis, String angkatan) {

        super(idUser, username, password, namaLengkap, email);
        this.nis = nis;
        this.angkatan = angkatan;
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
        this.idKelas = (kelas != null ? kelas.getIdKelas() : null);
    }

    public String getIdKelas() {
        return idKelas;
    }

    public void setIdKelas(String idKelas) {
        this.idKelas = idKelas;
    }
    
    public List<Nilai> getDaftarNilai() {
        return daftarNilai;
    }
}