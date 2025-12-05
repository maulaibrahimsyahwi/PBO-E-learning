package model;
import java.time.LocalDate;

public class Absensi {
    private String idAbsensi;
    private Siswa siswa;
    private Kelas kelas;
    private LocalDate tanggal;
    private String waktu;

    public Absensi(String idAbsensi, Siswa siswa, Kelas kelas, LocalDate tanggal, String waktu) {
        this.idAbsensi = idAbsensi;
        this.siswa = siswa;
        this.kelas = kelas;
        this.tanggal = tanggal;
        this.waktu = waktu;
    }

    public Absensi(String idAbsensi, String tanggalStr, String waktuStr) {
        this.idAbsensi = idAbsensi;
        this.tanggal = LocalDate.parse(tanggalStr);
        this.waktu = waktuStr;
    }

    public String getIdAbsensi() { return idAbsensi; }
    public Siswa getSiswa() { return siswa; }
    public void setSiswa(Siswa s) { this.siswa = s; }
    public Kelas getKelas() { return kelas; }
    public void setKelas(Kelas k) { this.kelas = k; }
    public LocalDate getTanggal() { return tanggal; }
    public String getWaktu() { return waktu; }
}