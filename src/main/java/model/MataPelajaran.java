package model;

public class MataPelajaran {
    private String idMapel;
    private String namaMapel;
    private String deskripsi;

    public MataPelajaran(String id, String nama, String desk) {
        this.idMapel = id;
        this.namaMapel = nama;
        this.deskripsi = desk;
    }

    public String getNamaMapel() { return namaMapel; }
}
