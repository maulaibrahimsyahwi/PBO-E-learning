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
    private ForumRepository forumRepo; 

    public DataReconstructor(
            UserRepository userRepo,
            KelasRepository kelasRepo,
            MapelRepository mapelRepo,
            MateriRepository materiRepo,
            TugasRepository tugasRepo,
            UjianRepository ujianRepo,
            JawabanRepository jawabanRepo,
            NilaiRepository nilaiRepo,
            ForumRepository forumRepo) {

        this.userRepo = userRepo;
        this.kelasRepo = kelasRepo;
        this.mapelRepo = mapelRepo;
        this.materiRepo = materiRepo;
        this.tugasRepo = tugasRepo;
        this.ujianRepo = ujianRepo;
        this.jawabanRepo = jawabanRepo;
        this.nilaiRepo = nilaiRepo;
        this.forumRepo = forumRepo;
    }

    public void reconstruct() {

        List<User> users = userRepo.getAll();

        // 1. Link Siswa -> Kelas & Guru -> Mapel/Kelas (dari file users.txt)
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
            } else if (u instanceof Guru g) {
                for (String mid : g.getTempIdMapel()) {
                    MataPelajaran m = mapelRepo.findById(mid);
                    if (m != null) g.tambahMapel(m);
                }
                for (String kid : g.getTempIdKelas()) {
                    Kelas k = kelasRepo.findById(kid);
                    if (k != null) g.tambahKelas(k);
                }
            }
        }

        // 2. Link Materi -> Guru/Kelas/Mapel
        for (Materi m : materiRepo.getAll()) {
            if (m.getGuru() == null) {
                for (User u : users) {
                    if (u instanceof Guru g && m.getIdMateri().startsWith(g.getIdUser())) {
                        m.setGuru(g);
                    }
                }
            }
            if (m.getKelas() == null) {
                for (Kelas k : kelasRepo.getAll()) {
                    if (m.getIdMateri().contains(k.getIdKelas())) m.setKelas(k);
                }
            }
            if (m.getMapel() == null) {
                for (MataPelajaran map : mapelRepo.getAll()) {
                    if (m.getIdMateri().contains(map.getIdMapel())) m.setMapel(map);
                }
            }
        }

        // 3. Link Tugas -> Guru/Kelas/Mapel
        for (Tugas t : tugasRepo.getAll()) {
            for (User u : users) {
                if (u instanceof Guru g && t.getGuru() == null && t.getIdTugas().startsWith(g.getIdUser())) {
                    t.setGuru(g);
                }
            }
            for (Kelas k : kelasRepo.getAll()) {
                if (t.getKelas() == null && t.getIdTugas().contains(k.getIdKelas())) t.setKelas(k);
            }
            for (MataPelajaran map : mapelRepo.getAll()) {
                if (t.getMapel() == null && t.getIdTugas().contains(map.getIdMapel())) t.setMapel(map);
            }
        }

        // 4. Link Ujian -> Guru/Kelas/Mapel
        for (Ujian u : ujianRepo.getAll()) {
            for (User usr : users) {
                if (usr instanceof Guru g && u.getGuru() == null && u.getIdUjian().startsWith(g.getIdUser())) {
                    u.setGuru(g);
                }
            }
            for (Kelas k : kelasRepo.getAll()) {
                if (u.getKelas() == null && u.getIdUjian().contains(k.getIdKelas())) u.setKelas(k);
            }
            for (MataPelajaran m : mapelRepo.getAll()) {
                if (u.getMapel() == null && u.getIdUjian().contains(m.getIdMapel())) u.setMapel(m);
            }
        }

        // 5. Link Jawaban -> Siswa/Tugas
        for (Jawaban j : jawabanRepo.getAll()) {
            for (User u : users) {
                if (u instanceof Siswa s && j.getSiswa() == null && j.getIdJawaban().startsWith(s.getIdUser())) {
                    j.setSiswa(s);
                }
            }
            for (Tugas t : tugasRepo.getAll()) {
                if (j.getTugas() == null && j.getIdJawaban().contains(t.getIdTugas())) j.setTugas(t);
            }
        }

        // 6. Link Nilai -> Siswa/Tugas
        for (Nilai n : nilaiRepo.getAll()) {
            for (User u : users) {
                if (u instanceof Siswa s && n.getSiswa() == null && n.getIdNilai().startsWith(s.getIdUser())) {
                    n.setSiswa(s);
                }
            }
            for (Tugas t : tugasRepo.getAll()) {
                if (n.getTugas() == null && n.getIdNilai().contains(t.getIdTugas())) n.setTugas(t);
            }
        }
        
        // 7. Link Forum -> Pengirim/Kelas/Mapel
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("src/main/java/data/forum.txt"));
            String line;
            int idx = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                if (d.length >= 6) {
                    if (idx < forumRepo.getAll().size()) {
                        ForumDiskusi f = forumRepo.getAll().get(idx++);
                        for (User u : users) {
                            if (u.getIdUser().equals(d[1])) {
                                f.setPengirim(u);
                                break;
                            }
                        }
                        f.setKelas(kelasRepo.findById(d[4]));
                        f.setMapel(mapelRepo.findById(d[5]));
                    }
                }
            }
            br.close();
        } catch (Exception e) { 
            // File mungkin belum ada
        }

        // =================================================================
        // 🔥 UPDATE PENTING: LINK MAPEL KE KELAS BERDASARKAN TINGKAT 🔥
        // =================================================================
        for (Kelas k : kelasRepo.getAll()) {
            for (MataPelajaran m : mapelRepo.getAll()) {
                // Jika tingkat sama (misal 10 == 10), masukkan mapel ke kelas
                if (k.getTingkat().equals(m.getTingkat())) {
                    k.tambahMapel(m);
                }
            }
        }

        System.out.println(">> Rekonstruksi data lengkap SELESAI!");
    }
}