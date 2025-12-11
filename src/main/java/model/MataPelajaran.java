package model;

import java.util.Objects;

public class MataPelajaran {
    private String idMapel;
    private String namaMapel;
    private String deskripsi;
    private String tingkat;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MataPelajaran that = (MataPelajaran) o;
        return Objects.equals(idMapel, that.idMapel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMapel);
    }
}