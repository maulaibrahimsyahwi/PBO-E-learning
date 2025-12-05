package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ForumDiskusi {
    private String idPesan;
    private User pengirim;
    private String judul; // Baru: Judul Topik
    private String isiPesan;
    private String waktu;
    private Kelas kelas;
    private MataPelajaran mapel;
    private String parentId; // Baru: ID Topik Induk (jika ini adalah balasan)

    // Constructor untuk Buat Topik Baru (Thread Starter)
    public ForumDiskusi(String id, User pengirim, String judul, String isi, 
                        Kelas kelas, MataPelajaran mapel) {
        this.idPesan = id;
        this.pengirim = pengirim;
        this.judul = judul;
        this.isiPesan = isi;
        this.kelas = kelas;
        this.mapel = mapel;
        this.parentId = "ROOT"; // Penanda ini adalah Topik Utama
        this.waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // Constructor untuk Balasan (Reply)
    public ForumDiskusi(String id, User pengirim, String isi, 
                        Kelas kelas, MataPelajaran mapel, String parentId) {
        this.idPesan = id;
        this.pengirim = pengirim;
        this.judul = "-"; // Balasan tidak butuh judul
        this.isiPesan = isi;
        this.kelas = kelas;
        this.mapel = mapel;
        this.parentId = parentId; // ID dari Topik yang dibalas
        this.waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // Constructor untuk Load dari File
    public ForumDiskusi(String id, String judul, String isi, String waktu, String parentId) {
        this.idPesan = id;
        this.judul = judul;
        this.isiPesan = isi;
        this.waktu = waktu;
        this.parentId = parentId;
    }

    public String getIdPesan() { return idPesan; }
    public User getPengirim() { return pengirim; }
    public void setPengirim(User pengirim) { this.pengirim = pengirim; }
    public String getJudul() { return judul; }
    public String getIsiPesan() { return isiPesan; }
    public String getWaktu() { return waktu; }
    public Kelas getKelas() { return kelas; }
    public void setKelas(Kelas kelas) { this.kelas = kelas; }
    public MataPelajaran getMapel() { return mapel; }
    public void setMapel(MataPelajaran mapel) { this.mapel = mapel; }
    public String getParentId() { return parentId; }
    
    public boolean isTopic() {
        return "ROOT".equals(parentId);
    }
}