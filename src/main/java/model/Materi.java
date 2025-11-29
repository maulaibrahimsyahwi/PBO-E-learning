package model;

public class Materi {
    private String idMateri;
    private String judul;
    private String deskripsi;
    private String fileMateri;

    private Guru guru;
    private Kelas kelas;
    private MataPelajaran mapel;

    public Materi(String id, String judul, String desk, String file) {
        this.idMateri = id;
        this.judul = judul;
        this.deskripsi = desk;
        this.fileMateri = file;
    }
    
    public Materi(String idMateri, String judul) {
        this.idMateri = idMateri;
        this.judul = judul;
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
