package model;

public class Soal {
    private String idSoal;
    private String idUjian;
    private String pertanyaan;
    private String pilA, pilB, pilC, pilD;
    private String kunciJawaban;

    public Soal(String id, String idUjian, String t, String a, String b, String c, String d, String k) {
        this.idSoal = id;
        this.idUjian = idUjian;
        this.pertanyaan = t;
        this.pilA = a; this.pilB = b; this.pilC = c; this.pilD = d;
        this.kunciJawaban = k;
    }

    public String getIdSoal() { return idSoal; }
    public String getIdUjian() { return idUjian; }
    public String getPertanyaan() { return pertanyaan; }
    public String getPilA() { return pilA; }
    public String getPilB() { return pilB; }
    public String getPilC() { return pilC; }
    public String getPilD() { return pilD; }
    public String getKunciJawaban() { return kunciJawaban; }
}