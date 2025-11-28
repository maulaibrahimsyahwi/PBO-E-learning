package model;

public class Nilai {
    private String idNilai;
    private Siswa siswa;
    private Tugas tugas;
    private double nilaiAngka;
    private String nilaiHuruf;
    private String keterangan;

    public Nilai(String idNilai, Siswa siswa, Tugas tugas,
                 double nilaiAngka, String keterangan) {
        this.idNilai = idNilai;
        this.siswa = siswa;
        this.tugas = tugas;
        setNilaiAngka(nilaiAngka);
        this.keterangan = keterangan;
    }

    public String getIdNilai() {
        return idNilai;
    }

    public Siswa getSiswa() {
        return siswa;
    }

    public Tugas getTugas() {
        return tugas;
    }

    public double getNilaiAngka() {
        return nilaiAngka;
    }

    public String getNilaiHuruf() {
        return nilaiHuruf;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setNilaiAngka(double nilaiAngka) {
        this.nilaiAngka = nilaiAngka;
        if (nilaiAngka >= 85) nilaiHuruf = "A";
        else if (nilaiAngka >= 75) nilaiHuruf = "B";
        else if (nilaiAngka >= 65) nilaiHuruf = "C";
        else nilaiHuruf = "D";
    }
}
