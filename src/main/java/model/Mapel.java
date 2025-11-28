package model;

public class Mapel {
    private String idMapel;
    private String namaMapel;

    public Mapel(String id, String nama) {
        this.idMapel = id;
        this.namaMapel = nama;
    }

    public String getIdMapel() {
        return idMapel;
    }

    public String getNamaMapel() {
        return namaMapel;
    }

    @Override
    public String toString() {
        return namaMapel;
    }
}
