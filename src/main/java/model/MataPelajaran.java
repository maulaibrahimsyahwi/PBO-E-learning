package model;

public class MataPelajaran {
    private String idMapel;
    private String namaMapel;
    private String deskripsi;
    private String tingkat; // Tambahan: 10, 11, atau 12

    public MataPelajaran(String id, String nama, String desk, String tingkat) {
        this.idMapel = id;
        this.namaMapel = nama;
        this.deskripsi = desk;
        this.tingkat = tingkat;
    }

    public String getIdMapel() {
        return idMapel;
    }

    public String getNamaMapel() {
        return namaMapel;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getTingkat() {
        return tingkat;
    }

    @Override
    public String toString() {
        return idMapel + " - " + namaMapel + " (Kelas " + tingkat + ")";
    }
}