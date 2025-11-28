package model;

import java.time.LocalDate;

public class Ujian {
    private String idUjian;
    private String jenisUjian;
    private LocalDate tanggal;
    private int durasi;

    private Guru guru;
    private Kelas kelas;
    private MataPelajaran mapel;

    public Ujian(String id, String jenis, LocalDate tgl, int durasi) {
        this.idUjian = id;
        this.jenisUjian = jenis;
        this.tanggal = tgl;
        this.durasi = durasi;
    }

    public String getIdUjian() {
        return idUjian;
    }

    public String getJenisUjian() {
        return jenisUjian;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public int getDurasi() {
        return durasi;
    }

    public Guru getGuru() {
        return guru;
    }

    public void setGuru(Guru guru) {
        this.guru = guru;
    }

    public Kelas getKelas() {
        return kelas;
    }

    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }

    public MataPelajaran getMapel() {
        return mapel;
    }

    public void setMapel(MataPelajaran mapel) {
        this.mapel = mapel;
    }
}
