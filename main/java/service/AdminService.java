package service;

import model.*;
import java.util.*;

public class AdminService {

    private List<Guru> daftarGuru = new ArrayList<>();
    private List<Siswa> daftarSiswa = new ArrayList<>();
    private List<Kelas> daftarKelas = new ArrayList<>();
    private List<MataPelajaran> daftarMapel = new ArrayList<>();

    public void tambahGuru(Guru g) {
        daftarGuru.add(g);
        System.out.println("Guru berhasil ditambahkan.\n");
    }

    public void tambahSiswa(Siswa s) {
        daftarSiswa.add(s);
        System.out.println("Siswa berhasil ditambahkan.\n");
    }

    public void tambahKelas(Kelas k) {
        daftarKelas.add(k);
        System.out.println("Kelas berhasil ditambahkan.\n");
    }

    public void tambahMapel(MataPelajaran m) {
        daftarMapel.add(m);
        System.out.println("Mapel berhasil ditambahkan.\n");
    }

    public List<Guru> getGuru() { return daftarGuru; }
    public List<Siswa> getSiswa() { return daftarSiswa; }
    public List<Kelas> getKelas() { return daftarKelas; }
    public List<MataPelajaran> getMapel() { return daftarMapel; }
}
