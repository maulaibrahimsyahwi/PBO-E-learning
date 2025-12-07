package model;

public class Soal {
    private String idSoal;
    private String idUjian;
    private String tipeSoal;
    private String pertanyaan;
    private String pilA, pilB, pilC, pilD;
    private String kunciJawaban;
    private String gambar;

    public Soal(String id, String idUjian, String tipe, String t, String a, String b, String c, String d, String k, String img) {
        this.idSoal = id;
        this.idUjian = idUjian;
        this.tipeSoal = tipe;
        this.pertanyaan = t;
        this.pilA = a; this.pilB = b; this.pilC = c; this.pilD = d;
        this.kunciJawaban = k;
        this.gambar = img;
    }

    public Soal(String id, String idUjian, String t, String a, String b, String c, String d, String k) {
        this(id, idUjian, "PG", t, a, b, c, d, k, null);
    }

    public String getIdSoal() { return idSoal; }
    public String getIdUjian() { return idUjian; }
    public String getTipeSoal() { return tipeSoal; }
    public String getPertanyaan() { return pertanyaan; }
    public String getPilA() { return pilA; }
    public String getPilB() { return pilB; }
    public String getPilC() { return pilC; }
    public String getPilD() { return pilD; }
    public String getKunciJawaban() { return kunciJawaban; }
    public String getGambar() { return gambar; }
}