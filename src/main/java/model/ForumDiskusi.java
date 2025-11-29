package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ForumDiskusi {
    private String idPesan;
    private User pengirim;
    private String isiPesan;
    private String waktu;
    
    private Kelas kelas;
    private MataPelajaran mapel;

    public ForumDiskusi(String id, User pengirim, String isi, Kelas kelas, MataPelajaran mapel) {
        this.idPesan = id;
        this.pengirim = pengirim;
        this.isiPesan = isi;
        this.kelas = kelas;
        this.mapel = mapel;
        this.waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public ForumDiskusi(String id, String isi, String waktu) {
        this.idPesan = id;
        this.isiPesan = isi;
        this.waktu = waktu;
    }

    public String getIdPesan() {
        return idPesan;
    }

    public User getPengirim() {
        return pengirim;
    }

    public void setPengirim(User pengirim) {
        this.pengirim = pengirim;
    }

    public String getIsiPesan() {
        return isiPesan;
    }

    public String getWaktu() {
        return waktu;
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