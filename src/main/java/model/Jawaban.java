package model;

import java.time.LocalDateTime;

public class Jawaban {
    private String idJawaban;
    private Siswa siswa;
    private Tugas tugas;          // ➜ jawaban untuk tugas mana
    private String fileJawaban;
    private LocalDateTime waktuKirim;

    public Jawaban(String id, Siswa s, Tugas t, String file) {
        this.idJawaban = id;
        this.siswa = s;
        this.tugas = t;
        this.fileJawaban = file;
        this.waktuKirim = LocalDateTime.now();
    }

    public String getIdJawaban() {
        return idJawaban;
    }

    public Siswa getSiswa() {
        return siswa;
    }

    public Tugas getTugas() {
        return tugas;
    }

    public String getFileJawaban() {
        return fileJawaban;
    }

    public LocalDateTime getWaktuKirim() {
        return waktuKirim;
    }
}
