package model;

public class Nilai {

    private String idNilai;
    private Siswa siswa;
    private Tugas tugas;
    private Ujian ujian;
    private int nilaiAngka;
    private String keterangan;

    public Nilai(String id, Siswa s, Tugas t, int angka, String ket) {
        this.idNilai = id;
        this.siswa = s;
        this.tugas = t;
        this.ujian = null;
        this.nilaiAngka = angka;
        this.keterangan = ket;
    }

    public Nilai(String id, Siswa s, Ujian u, int angka, String ket) {
        this.idNilai = id;
        this.siswa = s;
        this.tugas = null;
        this.ujian = u;
        this.nilaiAngka = angka;
        this.keterangan = ket;
    }

    public Nilai(String id, int angka, String ket) {
        this.idNilai = id;
        this.nilaiAngka = angka;
        this.keterangan = ket;
    }

    public String getIdNilai() { return idNilai; }
    public Siswa getSiswa() { return siswa; }
    public Tugas getTugas() { return tugas; }
    public Ujian getUjian() { return ujian; }
    public int getNilaiAngka() { return nilaiAngka; }
    public String getKeterangan() { return keterangan; }

    public String getNilaiHuruf() {
        if (nilaiAngka >= 85) return "A";
        if (nilaiAngka >= 75) return "B";
        if (nilaiAngka >= 65) return "C";
        return "D";
    }

    public void setSiswa(Siswa s) { this.siswa = s; }
    public void setTugas(Tugas t) { this.tugas = t; }
    public void setUjian(Ujian u) { this.ujian = u; }
    
    // Setter baru untuk fitur update
    public void setNilaiAngka(int nilaiAngka) {
        this.nilaiAngka = nilaiAngka;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}