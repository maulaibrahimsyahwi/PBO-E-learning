package model;

import java.time.LocalDate;

public class Tugas {
    private String idTugas;
    private String judul;
    private String deskripsi;
    private LocalDate deadline;

    private Guru guru;
    private Kelas kelas;
    private MataPelajaran mapel;

    public Tugas(String id, String judul, String desk, LocalDate dl) {
        this.idTugas = id;
        this.judul = judul;
        this.deskripsi = desk;
        this.deadline = dl;
    }

    public String getIdTugas() {
        return idTugas;
    }

    public String getJudul() {
        return judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public LocalDate getDeadline() {
        return deadline;
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
