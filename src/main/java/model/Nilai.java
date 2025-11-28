package model;

public class Nilai {

    private String idNilai;
    private Siswa siswa;
    private Tugas tugas;
    private int nilaiAngka;
    private String keterangan;

    public Nilai(String id, Siswa siswa, Tugas tugas, int angka, String ket) {
        this.idNilai = id;
        this.siswa = siswa;
        this.tugas = tugas;
        this.nilaiAngka = angka;
        this.keterangan = ket;
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

    public int getNilaiAngka() {
        return nilaiAngka;
    }

    public String getNilaiHuruf() {
        if (nilaiAngka >= 85) return "A";
        if (nilaiAngka >= 75) return "B";
        if (nilaiAngka >= 65) return "C";
        return "D";
    }

    public String getKeterangan() {
        return keterangan;
    }

    // ⬇ Tambahan penting untuk RECONSTRUCTOR
    public void setSiswa(Siswa siswa) {
        this.siswa = siswa;
    }

    public void setTugas(Tugas tugas) {
        this.tugas = tugas;
    }
}
