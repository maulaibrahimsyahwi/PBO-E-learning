package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ForumThread {
    private String idThread;
    private User pengirim;
    private String judul;
    private String isiUtama;
    private String waktu;
    private Kelas kelas;
    private MataPelajaran mapel;

    // Constructor untuk membuat thread baru (Runtime)
    public ForumThread(String id, User pengirim, String judul, String isi, Kelas k, MataPelajaran m) {
        this.idThread = id;
        this.pengirim = pengirim;
        this.judul = judul;
        this.isiUtama = isi;
        this.kelas = k;
        this.mapel = m;
        this.waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // Constructor untuk Load dari DB
    public ForumThread(String id, User pengirim, String judul, String isi, String waktu, Kelas k, MataPelajaran m) {
        this.idThread = id;
        this.pengirim = pengirim;
        this.judul = judul;
        this.isiUtama = isi;
        this.waktu = waktu;
        this.kelas = k;
        this.mapel = m;
    }

    public String getIdThread() { return idThread; }
    public User getPengirim() { return pengirim; }
    public String getJudul() { return judul; }
    public String getIsiUtama() { return isiUtama; }
    public String getWaktu() { return waktu; }
    public Kelas getKelas() { return kelas; }
    public MataPelajaran getMapel() { return mapel; }
}