package model;

import java.time.LocalDate;

public class Tugas {
    private String idTugas;
    private String judul;
    private String deskripsi;
    private LocalDate deadline;

    public Tugas(String id, String judul, String desk, LocalDate dl) {
        this.idTugas = id;
        this.judul = judul;
        this.deskripsi = desk;
        this.deadline = dl;
    }

    public String getJudul() { return judul; }
}
