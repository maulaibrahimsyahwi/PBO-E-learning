package model;

public class Nilai {
    // Menambahkan idUser agar bisa difilter oleh Repository
    private String idUser;
    
    private String idNilai;
    private double nilaiAngka;
    private String nilaiHuruf;
    private String mapel;

    // Konstruktor disesuaikan (optional: jika error di tempat lain, kembalikan ke constructor lama)
    public Nilai(String id, String idUser, String mapel, double angka) {
        this.idNilai = id;
        this.idUser = idUser;
        this.mapel = mapel;
        setNilaiAngka(angka);
    }
    
    // Konstruktor alternatif untuk kompatibilitas kode lama (JAGA-JAGA)
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

    // --- GETTER & SETTER TAMBAHAN ---
    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }
    // --------------------------------

    public String getMapel() { return mapel; }
    public String getNilaiHuruf() { return nilaiHuruf; }
}