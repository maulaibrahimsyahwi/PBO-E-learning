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

    public DataReconstructor(UserRepository u, KelasRepository k, MapelRepository mp,
                             MateriRepository mt, TugasRepository t, UjianRepository uj,
                             JawabanRepository j, NilaiRepository n, ForumRepository f) {
        this.userRepo = u;
        this.kelasRepo = k;
        this.mapelRepo = mp;
        this.materiRepo = mt;
        this.tugasRepo = t;
        this.ujianRepo = uj;
        this.jawabanRepo = j;
        this.nilaiRepo = n;
        this.forumRepo = f;
    }

    public void reconstruct() {
        List<User> users = userRepo.getAll();

        for (User u : users) {
            if (u instanceof Siswa s) {
                if (s.getIdKelas() != null && !s.getIdKelas().equals("-")) {
                    Kelas k = kelasRepo.findById(s.getIdKelas());
                    if (k != null) { s.setKelas(k); k.tambahSiswa(s); }
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

        for (Materi m : materiRepo.getAll()) {
            if (m.getGuru() == null) reconstructGuru(m, users);
            if (m.getKelas() == null) reconstructKelas(m);
            if (m.getMapel() == null) reconstructMapel(m);
        }

        for (Tugas t : tugasRepo.getAll()) {
            if (t.getGuru() == null) reconstructGuru(t, users);
            if (t.getKelas() == null) reconstructKelas(t);
            if (t.getMapel() == null) reconstructMapel(t);
        }
        for (Ujian u : ujianRepo.getAll()) {
            if (u.getGuru() == null) reconstructGuru(u, users);
            if (u.getKelas() == null) reconstructKelas(u);
            if (u.getMapel() == null) reconstructMapel(u);
        }

        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("src/main/java/data/jawaban.txt"));
            String line;
            int idx = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                if (d.length >= 6 && idx < jawabanRepo.getAll().size()) {
                    Jawaban j = jawabanRepo.getAll().get(idx++);
                    for (User u : users) { if (u.getIdUser().equals(d[1])) { j.setSiswa((Siswa) u); break; } }
                    if (!d[2].equals("-")) {
                        for (Tugas t : tugasRepo.getAll()) { if (t.getIdTugas().equals(d[2])) { j.setTugas(t); break; } }
                    }
                    if (!d[3].equals("-")) {
                        for (Ujian u : ujianRepo.getAll()) { if (u.getIdUjian().equals(d[3])) { j.setUjian(u); break; } }
                    }
                }
            }
            br.close();
        } catch (Exception e) {}

        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("src/main/java/data/nilai.txt"));
            String line;
            int idx = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                if (d.length >= 6 && idx < nilaiRepo.getAll().size()) {
                    Nilai n = nilaiRepo.getAll().get(idx++);
                    for (User u : users) { if (u.getIdUser().equals(d[1])) { n.setSiswa((Siswa) u); break; } }
                    if (!d[2].equals("-")) {
                        for (Tugas t : tugasRepo.getAll()) { if (t.getIdTugas().equals(d[2])) { n.setTugas(t); break; } }
                    }
                    if (!d[3].equals("-")) {
                        for (Ujian u : ujianRepo.getAll()) { if (u.getIdUjian().equals(d[3])) { n.setUjian(u); break; } }
                    }
                    if (n.getSiswa() != null) n.getSiswa().tambahNilai(n);
                }
            }
            br.close();
        } catch (Exception e) {}

        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("src/main/java/data/forum.txt"));
            String line;
            int idx = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                if (d.length >= 6 && idx < forumRepo.getAll().size()) {
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
            br.close();
        } catch (Exception e) {}

        for (Kelas k : kelasRepo.getAll()) {
            for (MataPelajaran m : mapelRepo.getAll()) {
                if (k.getTingkat().equals(m.getTingkat())) {
                    k.tambahMapel(m);
                }
            }
        }

        System.out.println(">> SELAMAT DATANG DI Aplikasi E-Learning <<");
    }

    private void reconstructGuru(Object obj, List<User> users) {
        if (obj instanceof Materi m) {
            for (User u : users) {
                if (u instanceof Guru g && m.getIdMateri().startsWith(g.getIdUser())) { m.setGuru(g); return; }
            }
        } else if (obj instanceof Tugas t) {
            for (User u : users) {
                if (u instanceof Guru g && t.getIdTugas().startsWith(g.getIdUser())) { t.setGuru(g); return; }
            }
        } else if (obj instanceof Ujian u) {
            for (User usr : users) {
                if (usr instanceof Guru g && u.getIdUjian().startsWith(g.getIdUser())) { u.setGuru(g); return; }
            }
        }
    }

    private void reconstructKelas(Object obj) {
        if (obj instanceof Materi m) {
            for (Kelas k : kelasRepo.getAll()) {
                if (m.getIdMateri().contains(k.getIdKelas())) { m.setKelas(k); return; }
            }
        } else if (obj instanceof Tugas t) {
            for (Kelas k : kelasRepo.getAll()) {
                if (t.getIdTugas().contains(k.getIdKelas())) { t.setKelas(k); return; }
            }
        } else if (obj instanceof Ujian u) {
            for (Kelas k : kelasRepo.getAll()) {
                if (u.getIdUjian().contains(k.getIdKelas())) { u.setKelas(k); return; }
            }
        }
    }

    private void reconstructMapel(Object obj) {
        if (obj instanceof Materi m) {
            for (MataPelajaran mp : mapelRepo.getAll()) {
                if (m.getIdMateri().contains(mp.getIdMapel())) { m.setMapel(mp); return; }
            }
        } else if (obj instanceof Tugas t) {
            for (MataPelajaran mp : mapelRepo.getAll()) {
                if (t.getIdTugas().contains(mp.getIdMapel())) { t.setMapel(mp); return; }
            }
        } else if (obj instanceof Ujian u) {
            for (MataPelajaran mp : mapelRepo.getAll()) {
                if (u.getIdUjian().contains(mp.getIdMapel())) { u.setMapel(mp); return; }
            }
        }
    }
}