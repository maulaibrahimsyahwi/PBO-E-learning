package model;

public class Nilai {
    private String idNilai;
    private double nilaiAngka;
    private String nilaiHuruf;
    private String mapel;

    public Nilai(String id, String mapel, double angka) {
        this.idNilai = id;
        this.mapel = mapel;
        setNilaiAngka(angka);
    }

    public void setNilaiAngka(double angka) {
        this.nilaiAngka = angka;
        if (angka >= 85) nilaiHuruf = "A";
        else if (angka >= 75) nilaiHuruf = "B";
        else if (angka >= 65) nilaiHuruf = "C";
        else nilaiHuruf = "D";
    }

    public String getMapel() { return mapel; }
    public String getNilaiHuruf() { return nilaiHuruf; }
}
