package model;

import java.time.LocalDate;

public class Ujian {
    private String idUjian;
    private String jenisUjian;
    private LocalDate tanggal;
    private int durasi;

    public Ujian(String id, String jenis, LocalDate tgl, int durasi) {
        this.idUjian = id;
        this.jenisUjian = jenis;
        this.tanggal = tgl;
        this.durasi = durasi;
    }

    public String getJenisUjian() { return jenisUjian; }
}
