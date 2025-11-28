package model;

import java.time.LocalDateTime;

public class Jawaban {
    private String idJawaban;
    private Siswa siswa;
    private String fileJawaban;
    private LocalDateTime waktuKirim;

    public Jawaban(String id, Siswa s, String file) {
        this.idJawaban = id;
        this.siswa = s;
        this.fileJawaban = file;
        this.waktuKirim = LocalDateTime.now();
    }

    public String getFileJawaban() { return fileJawaban; }
}
