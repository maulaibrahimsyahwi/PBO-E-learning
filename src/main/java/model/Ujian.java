package model;

import java.time.LocalDate;

public class Ujian {
    private String idUjian;
    private String namaUjian; // Sebelumnya 'jenisUjian'
    private String tipeUjian; // "PG", "ESSAY", "HYBRID", "KUIS"
    private LocalDate tanggal;
    private int durasiTotal;   // Menit (untuk PG/Essay/Hybrid)
    private int waktuPerSoal;  // Detik (khusus tipe KUIS)
    private int maxSoal;       // Batas maksimal soal

    private Guru guru;
    private Kelas kelas;
    private MataPelajaran mapel;

    // Constructor Baru Lengkap
    public Ujian(String id, String nama, String tipe, LocalDate tgl, int durasi, int waktuPerSoal, int maxSoal) {
        this.idUjian = id;
        this.namaUjian = nama;
        this.tipeUjian = tipe;
        this.tanggal = tgl;
        this.durasiTotal = durasi;
        this.waktuPerSoal = waktuPerSoal;
        this.maxSoal = maxSoal;
    }

    // Constructor Lama (Backward Compatibility) - Default ke PG
    public Ujian(String id, String nama, LocalDate tgl, int durasi) {
        this(id, nama, "PG", tgl, durasi, 0, 50);
    }

    public String getIdUjian() { return idUjian; }
    public String getNamaUjian() { return namaUjian; }
    public String getTipeUjian() { return tipeUjian; }
    public LocalDate getTanggal() { return tanggal; }
    public int getDurasiTotal() { return durasiTotal; }
    public int getWaktuPerSoal() { return waktuPerSoal; }
    public int getMaxSoal() { return maxSoal; }

    // Helper untuk kompatibilitas kode lama
    public String getJenisUjian() { return namaUjian + " (" + tipeUjian + ")"; }
    public int getDurasi() { return durasiTotal; }

    public Guru getGuru() { return guru; }
    public void setGuru(Guru guru) { this.guru = guru; }
    public Kelas getKelas() { return kelas; }
    public void setKelas(Kelas kelas) { this.kelas = kelas; }
    public MataPelajaran getMapel() { return mapel; }
    public void setMapel(MataPelajaran mapel) { this.mapel = mapel; }
}