package service;

import model.*;
import repository.*;

import java.util.List;

public class DataReconstructor {

    private UserRepository userRepo;
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;
    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;

    public DataReconstructor(
            UserRepository userRepo,
            KelasRepository kelasRepo,
            MapelRepository mapelRepo,
            MateriRepository materiRepo,
            TugasRepository tugasRepo,
            UjianRepository ujianRepo,
            JawabanRepository jawabanRepo,
            NilaiRepository nilaiRepo) {

        this.userRepo = userRepo;
        this.kelasRepo = kelasRepo;
        this.mapelRepo = mapelRepo;
        this.materiRepo = materiRepo;
        this.tugasRepo = tugasRepo;
        this.ujianRepo = ujianRepo;
        this.jawabanRepo = jawabanRepo;
        this.nilaiRepo = nilaiRepo;
    }

    public void reconstruct() {

        List<User> users = userRepo.getAll();

        // === 1. HUBUNGKAN SISWA → KELAS ===
        for (User u : users) {
            if (u instanceof Siswa s) {
                for (Kelas k : kelasRepo.getAll()) {
                    if (k.getDaftarSiswa().stream().anyMatch(
                            sx -> sx.getIdUser().equals(s.getIdUser()))) {
                        s.setKelas(k);
                    }
                }
            }
        }

        // === 2. HUBUNGKAN GURU → MAPEL ===
        for (User u : users) {
            if (u instanceof Guru g) {
                // Guru.mapelDiampu sudah dimuat dari file user.txt? Jika belum, bisa ditambah formatnya nanti.
                // Untuk sekarang, skip jika mapel assignment belum ditulis dalam file.
            }
        }

        // === 3. HUBUNGKAN MATERI → (GURU, KELAS, MAPEL) ===
        for (Materi m : materiRepo.getAll()) {

            if (m.getGuru() == null) {
                // cari guru
                for (User u : users) {
                    if (u instanceof Guru g) {
                        if (m.getIdMateri().startsWith(g.getIdUser())) { 
                            // atau cek idGuru saat save
                            m.setGuru(g);
                        }
                    }
                }
            }

            // Hubungkan kelas
            if (m.getKelas() == null) {
                for (Kelas k : kelasRepo.getAll()) {
                    if (m.getIdMateri().contains(k.getIdKelas())) {
                        m.setKelas(k);
                    }
                }
            }

            // Hubungkan mapel
            if (m.getMapel() == null) {
                for (MataPelajaran map : mapelRepo.getAll()) {
                    if (m.getIdMateri().contains(map.getIdMapel())) {
                        m.setMapel(map);
                    }
                }
            }
        }

        // === 4. HUBUNGKAN TUGAS → (GURU, KELAS, MAPEL) ===
        for (Tugas t : tugasRepo.getAll()) {

            // Guru
            for (User u : users) {
                if (u instanceof Guru g && t.getGuru() == null) {
                    if (t.getIdTugas().startsWith(g.getIdUser())) {
                        t.setGuru(g);
                    }
                }
            }

            // Kelas
            for (Kelas k : kelasRepo.getAll()) {
                if (t.getKelas() == null && t.getIdTugas().contains(k.getIdKelas())) {
                    t.setKelas(k);
                }
            }

            // Mapel
            for (MataPelajaran map : mapelRepo.getAll()) {
                if (t.getMapel() == null && t.getIdTugas().contains(map.getIdMapel())) {
                    t.setMapel(map);
                }
            }
        }

        // === 5. HUBUNGKAN UJIAN → (GURU, KELAS, MAPEL) ===
        for (Ujian u : ujianRepo.getAll()) {

            for (User usr : users) {
                if (usr instanceof Guru g && u.getGuru() == null) {
                    if (u.getIdUjian().startsWith(g.getIdUser())) {
                        u.setGuru(g);
                    }
                }
            }

            for (Kelas k : kelasRepo.getAll()) {
                if (u.getKelas() == null && u.getIdUjian().contains(k.getIdKelas())) {
                    u.setKelas(k);
                }
            }

            for (MataPelajaran m : mapelRepo.getAll()) {
                if (u.getMapel() == null && u.getIdUjian().contains(m.getIdMapel())) {
                    u.setMapel(m);
                }
            }
        }

        // === 6. HUBUNGKAN JAWABAN → (SISWA, TUGAS) ===
        for (Jawaban j : jawabanRepo.getAll()) {

            // siswa
            for (User u : users) {
                if (u instanceof Siswa s && j.getSiswa() == null) {
                    if (j.getIdJawaban().startsWith(s.getIdUser())) {
                        j.setSiswa(s);
                    }
                }
            }

            // tugas
            for (Tugas t : tugasRepo.getAll()) {
                if (j.getTugas() == null && j.getIdJawaban().contains(t.getIdTugas())) {
                    j.setTugas(t);
                }
            }
        }

        // === 7. HUBUNGKAN NILAI → (SISWA, TUGAS) ===
        for (Nilai n : nilaiRepo.getAll()) {

            // siswa
            for (User u : users) {
                if (u instanceof Siswa s && n.getSiswa() == null) {
                    if (n.getIdNilai().startsWith(s.getIdUser())) {
                        n.setSiswa(s);
                    }
                }
            }

            // tugas
            for (Tugas t : tugasRepo.getAll()) {
                if (n.getTugas() == null && n.getIdNilai().contains(t.getIdTugas())) {
                    n.setTugas(t);
                }
            }
        }

        System.out.println(">> Rekonstruksi data selesai!");
    }
}
