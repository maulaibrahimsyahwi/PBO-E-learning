package model;

public class Materi {
    private String idMateri;
    private String judul;
    private String deskripsi;
    private String fileMateri;

    public Materi(String id, String judul, String desk, String file) {
        this.idMateri = id;
        this.judul = judul;
        this.deskripsi = desk;
        this.fileMateri = file;
    }

    public String getIdMateri() {
        return idMateri;
    }

    public String getJudul() {
        return judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getFileMateri() {
        return fileMateri;
    }
}
