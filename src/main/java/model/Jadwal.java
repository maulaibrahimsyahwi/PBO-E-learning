package model;

public class Jadwal {
    private String idJadwal;
    private String hari;
    private String jamMulai;
    private String jamSelesai;

    public Jadwal(String id, String hari, String mulai, String selesai) {
        this.idJadwal = id;
        this.hari = hari;
        this.jamMulai = mulai;
        this.jamSelesai = selesai;
    }

    public String getHari() { return hari; }
}
