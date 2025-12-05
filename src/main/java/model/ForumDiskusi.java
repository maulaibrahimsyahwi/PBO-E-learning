package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ForumDiskusi {
    private String idPesan;
    private User pengirim;
    private String judul;
    private String isiPesan;
    private String waktu;
    private Kelas kelas;
    private MataPelajaran mapel;
    private String parentId;

    // Constructor 1: Buat Topik Baru (Runtime)
    public ForumDiskusi(String id, User pengirim, String judul, String isi, 
                        Kelas kelas, MataPelajaran mapel) {
        this.idPesan = id;
        this.pengirim = pengirim;
        this.judul = judul;
        this.isiPesan = isi;
        this.kelas = kelas;
        this.mapel = mapel;
        this.parentId = "ROOT";
        this.waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // Constructor 2: Balasan / Reply (Runtime)
    public ForumDiskusi(String id, User pengirim, String isi, 
                        Kelas kelas, MataPelajaran mapel, String parentId) {
        this.idPesan = id;
        this.pengirim = pengirim;
        this.judul = "-";
        this.isiPesan = isi;
        this.kelas = kelas;
        this.mapel = mapel;
        this.parentId = parentId;
        this.waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // Constructor 3: Load dari Database (LENGKAP) - INI YANG DITAMBAHKAN
    public ForumDiskusi(String id, User pengirim, String judul, String isi, String waktu, 
                        Kelas kelas, MataPelajaran mapel, String parentId) {
        this.idPesan = id;
        this.pengirim = pengirim;
        this.judul = judul;
        this.isiPesan = isi;
        this.waktu = waktu;
        this.kelas = kelas;
        this.mapel = mapel;
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