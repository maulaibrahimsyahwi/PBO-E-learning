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

        // =====================================================
        // 1. REKONSTRUKSI SISWA → KELAS (METODE BARU & BENAR)
        // =====================================================
        for (User u : users) {
            if (u instanceof Siswa s) {
                String idKelas = s.getIdKelas();
                if (idKelas != null && !idKelas.equals("-")) {
                    Kelas k = kelasRepo.findById(idKelas);
                    if (k != null) {
                        s.setKelas(k);
                        k.tambahSiswa(s);
                    }
                }
            }
        }

        // =====================================================
        // 2. REKONSTRUKSI GURU → MAPEL
        // =====================================================
        for (User u : users) {
            if (u instanceof Guru g) {
                // TODO jika kamu simpan mapel → guru, isi di sini
            }
        }

        // =====================================================
        // 3. REKONSTRUKSI MATERI
        // =====================================================
        for (Materi m : materiRepo.getAll()) {

            // Guru
            if (m.getGuru() == null) {
                for (User u : users) {
                    if (u instanceof Guru g) {
                        if (m.getIdMateri().startsWith(g.getIdUser())) {
                            m.setGuru(g);
                        }
                    }
                }
            }

            // Kelas
            if (m.getKelas() == null) {
                for (Kelas k : kelasRepo.getAll()) {
                    if (m.getIdMateri().contains(k.getIdKelas())) {
                        m.setKelas(k);
                    }
                }
            }

            // Mapel
            if (m.getMapel() == null) {
                for (MataPelajaran map : mapelRepo.getAll()) {
                    if (m.getIdMateri().contains(map.getIdMapel())) {
                        m.setMapel(map);
                    }
                }
            }
        }

        // =====================================================
        // 4. REKONSTRUKSI TUGAS
        // =====================================================
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

        // =====================================================
        // 5. REKONSTRUKSI UJIAN
        // =====================================================
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

        // =====================================================
        // 6. REKONSTRUKSI JAWABAN
        // =====================================================
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

        // =====================================================
        // 7. REKONSTRUKSI NILAI
        // =====================================================
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

        System.out.println(">> Rekonstruksi data lengkap SELESAI!");
    }
}
