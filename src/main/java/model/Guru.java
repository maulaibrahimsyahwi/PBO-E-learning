package model;

import java.util.ArrayList;
import java.util.List;

public class Guru extends User {
    private String nip;
    private String spesialisasi;
    private List<MataPelajaran> mapelDiampu = new ArrayList<>();
    private List<Kelas> daftarKelas = new ArrayList<>();

    private List<String> tempIdMapel = new ArrayList<>();
    private List<String> tempIdKelas = new ArrayList<>();

    public Guru(String idUser, String username, String password,
                String namaLengkap, String email,
                String nip, String spesialisasi) {

        super(idUser, username, password, namaLengkap, email);
        this.nip = nip;
        this.spesialisasi = spesialisasi;
    }

    @Override
    public void tampilkanMenu() {
        System.out.println("=== Menu Guru ===");
    }

    public String getNip() {
        return nip;
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public List<MataPelajaran> getMapelDiampu() {
        return mapelDiampu;
    }

    public void tambahMapel(MataPelajaran mapel) {
        if (!mapelDiampu.contains(mapel)) {
            mapelDiampu.add(mapel);
        }
    }

    public List<Kelas> getDaftarKelas() {
        return daftarKelas;
    }

    public void tambahKelas(Kelas kelas) {
        if (!daftarKelas.contains(kelas)) {
            daftarKelas.add(kelas);
        }
    }

    public List<String> getTempIdMapel() {
        return tempIdMapel;
    }

    public void addTempIdMapel(String id) {
        this.tempIdMapel.add(id);
    }

    public List<String> getTempIdKelas() {
        return tempIdKelas;
    }

    public void addTempIdKelas(String id) {
        this.tempIdKelas.add(id);
    }
}