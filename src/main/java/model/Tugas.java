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

    // --- GETTER YANG DIPERBAIKI ---
    public String getIdTugas() { return idTugas; }
    public LocalDate getDeadline() { return deadline; }
    // ------------------------------

    public String getJudul() { return judul; }
}