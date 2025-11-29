package model;

import java.time.LocalDate;

public class Jawaban {

    private String idJawaban;
    private Siswa siswa;
    private Tugas tugas;
    private Ujian ujian;
    private String fileJawaban;
    private String tanggalSubmit;

    public Jawaban(String id, Siswa s, Tugas t, String file) {
        this.idJawaban = id;
        this.siswa = s;
        this.tugas = t;
        this.ujian = null;
        this.fileJawaban = file;
        this.tanggalSubmit = LocalDate.now().toString();
    }

    public Jawaban(String id, Siswa s, Ujian u, String file) {
        this.idJawaban = id;
        this.siswa = s;
        this.tugas = null;
        this.ujian = u;
        this.fileJawaban = file;
        this.tanggalSubmit = LocalDate.now().toString();
    }

    public Jawaban(String id, String file, String tgl) {
        this.idJawaban = id;
        this.fileJawaban = file;
        this.tanggalSubmit = tgl;
    }

    public String getIdJawaban() { return idJawaban; }
    public Siswa getSiswa() { return siswa; }
    public Tugas getTugas() { return tugas; }
    public Ujian getUjian() { return ujian; }
    public String getFileJawaban() { return fileJawaban; }
    public String getTanggalSubmit() { return tanggalSubmit; }

    public void setSiswa(Siswa siswa) { this.siswa = siswa; }
    public void setTugas(Tugas tugas) { this.tugas = tugas; }
    public void setUjian(Ujian ujian) { this.ujian = ujian; }
}