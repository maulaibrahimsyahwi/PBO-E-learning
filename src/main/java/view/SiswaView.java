package view;

import model.*;
import repository.*;
import utils.IdUtil;
import utils.InputUtil;

import java.util.List;

public class SiswaView {

    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;
    private ForumRepository forumRepo;

    public SiswaView(MateriRepository materiRepo,
                     TugasRepository tugasRepo,
                     UjianRepository ujianRepo,
                     JawabanRepository jawabanRepo,
                     NilaiRepository nilaiRepo,
                     ForumRepository forumRepo) {

        this.materiRepo = materiRepo;
        this.tugasRepo = tugasRepo;
        this.ujianRepo = ujianRepo;
        this.jawabanRepo = jawabanRepo;
        this.nilaiRepo = nilaiRepo;
        this.forumRepo = forumRepo;
    }

    public void menu(Siswa s) {
        while (true) {
            System.out.println("\n=== MENU SISWA (" + s.getNamaLengkap() + ") ===");
            System.out.println("1. Lihat Mata Pelajaran (Masuk Kelas)");
            System.out.println("2. Lihat Semua Materi");
            System.out.println("3. Lihat Semua Tugas");
            System.out.println("4. Submit Jawaban (Tugas)");
            System.out.println("5. Lihat Nilai");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            switch (pilih) {
                case 0 -> { return; }
                case 1 -> lihatMataPelajaran(s);
                case 2 -> lihatMateri(s);
                case 3 -> lihatTugas(s);
                case 4 -> submitJawaban(s);
                case 5 -> lihatNilai(s);
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private void lihatMataPelajaran(Siswa s) {
        Kelas kelas = s.getKelas();
        if (kelas == null) {
            System.out.println("Anda belum masuk ke kelas manapun. Hubungi admin.");
            return;
        }

        List<MataPelajaran> mapelList = kelas.getDaftarMapel();
        if (mapelList.isEmpty()) {
            System.out.println("Belum ada mata pelajaran di kelas " + kelas.getNamaKelas());
            return;
        }

        System.out.println("\n=== DAFTAR MAPEL KELAS " + kelas.getNamaKelas() + " ===");
        int no = 1;
        for (MataPelajaran m : mapelList) {
            System.out.println(no++ + ". " + m.getNamaMapel() + " (" + m.getIdMapel() + ")");
        }
        System.out.println("0. Kembali");

        int pilih = InputUtil.inputInt("Pilih mapel untuk masuk: ");
        if (pilih == 0) return;

        if (pilih < 1 || pilih > mapelList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        MataPelajaran selectedMapel = mapelList.get(pilih - 1);
        menuMapel(s, selectedMapel);
    }

    private void menuMapel(Siswa s, MataPelajaran mapel) {
        while (true) {
            System.out.println("\n=== MAPEL: " + mapel.getNamaMapel() + " ===");
            System.out.println("1. Lihat Materi");
            System.out.println("2. Lihat Tugas");
            System.out.println("3. Forum Diskusi");
            System.out.println("4. Lihat Jadwal Ujian"); // 🔥 Menu Baru
            System.out.println("0. Kembali ke Daftar Mapel");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            switch (pilih) {
                case 0 -> { return; }
                case 1 -> lihatMateriByMapel(s, mapel);
                case 2 -> lihatTugasByMapel(s, mapel);
                case 3 -> forumDiskusi(s, mapel);
                case 4 -> lihatUjianByMapel(s, mapel); // 🔥 Fitur Baru
                default -> System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // 🔥 FITUR BARU: LIHAT UJIAN DI MAPEL INI
    private void lihatUjianByMapel(Siswa s, MataPelajaran mapel) {
        System.out.println("\n--- Jadwal Ujian: " + mapel.getNamaMapel() + " ---");
        List<Ujian> list = ujianRepo.getByMapelAndKelas(mapel, s.getKelas());

        if (list.isEmpty()) {
            System.out.println("Belum ada ujian terjadwal.");
        } else {
            for (Ujian u : list) {
                System.out.println("- [" + u.getJenisUjian() + "] Tanggal: " + u.getTanggal() + 
                                   " | Durasi: " + u.getDurasi() + " menit");
            }
        }
        InputUtil.inputString("\nTekan Enter untuk kembali...");
    }

    private void forumDiskusi(Siswa s, MataPelajaran mapel) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("   FORUM DISKUSI: " + mapel.getNamaMapel().toUpperCase());
            System.out.println("========================================");
            
            List<ForumDiskusi> chats = forumRepo.getByMapelAndKelas(mapel, s.getKelas());
            
            if (chats.isEmpty()) {
                System.out.println("\n[!] Belum ada diskusi di mapel ini.");
                System.out.println("    Jadilah yang pertama memulai diskusi!");
            } else {
                for (ForumDiskusi f : chats) {
                    String senderName = (f.getPengirim() != null) ? f.getPengirim().getNamaLengkap() : "User Terhapus";
                    String role = (f.getPengirim() instanceof Guru) ? "[GURU]" : "[SISWA]";
                    
                    System.out.println("");
                    System.out.println(role + " " + senderName + " (" + f.getWaktu() + ")");
                    System.out.println("   \"" + f.getIsiPesan() + "\"");
                }
            }
            System.out.println("========================================");
            System.out.println("1. Tulis Pesan / Balas");
            System.out.println("0. Kembali");
            
            int pilih = InputUtil.inputInt("Pilih: ");
            if (pilih == 0) return;
            
            if (pilih == 1) {
                String pesan = InputUtil.inputString("Ketik pesan Anda: ");
                if (!pesan.isBlank()) {
                    String idPesan = IdUtil.generate();
                    ForumDiskusi fd = new ForumDiskusi(idPesan, s, pesan, s.getKelas(), mapel);
                    forumRepo.addPesan(fd);
                    System.out.println(">> Pesan terkirim!");
                }
            }
        }
    }

    private void lihatMateriByMapel(Siswa s, MataPelajaran mapel) {
        System.out.println("\n--- Materi " + mapel.getNamaMapel() + " ---");
        List<Materi> list = materiRepo.getByMapelAndKelas(mapel, s.getKelas());

        if (list.isEmpty()) {
            System.out.println("Belum ada materi.");
        } else {
            for (Materi m : list) {
                System.out.println("- [" + m.getIdMateri() + "] " + m.getJudul() + 
                                   " (Guru: " + (m.getGuru() != null ? m.getGuru().getNamaLengkap() : "-") + ")");
                System.out.println("  Desc: " + m.getDeskripsi());
            }
        }
        InputUtil.inputString("\nTekan Enter untuk kembali...");
    }

    private void lihatTugasByMapel(Siswa s, MataPelajaran mapel) {
        System.out.println("\n--- Tugas " + mapel.getNamaMapel() + " ---");
        List<Tugas> list = tugasRepo.getByMapelAndKelas(mapel, s.getKelas());

        if (list.isEmpty()) {
            System.out.println("Belum ada tugas.");
        } else {
            for (Tugas t : list) {
                System.out.println("- [" + t.getIdTugas() + "] " + t.getJudul() + 
                                   " | Deadline: " + t.getDeadline());
                System.out.println("  Desc: " + t.getDeskripsi());
            }
        }
        InputUtil.inputString("\nTekan Enter untuk kembali...");
    }

    private void lihatMateri(Siswa s) {
        if (s.getKelas() == null) return;
        List<Materi> list = materiRepo.getByKelas(s.getKelas());
        if (list.isEmpty()) { System.out.println("Tidak ada materi."); return; }
        for (Materi m : list) {
            System.out.println("- " + m.getJudul() + " (" + (m.getMapel()!=null?m.getMapel().getNamaMapel():"-") + ")");
        }
    }

    private void lihatTugas(Siswa s) {
        if (s.getKelas() == null) return;
        List<Tugas> list = tugasRepo.getByKelas(s.getKelas());
        if (list.isEmpty()) { System.out.println("Tidak ada tugas."); return; }
        for (Tugas t : list) {
            System.out.println("- " + t.getJudul() + " (" + (t.getMapel()!=null?t.getMapel().getNamaMapel():"-") + ")");
        }
    }

    private void submitJawaban(Siswa s) {
        System.out.println("\n=== SUBMIT JAWABAN ===");
        String idTugas = InputUtil.inputString("Masukkan ID Tugas: ");
        String file = InputUtil.inputString("Nama File Jawaban: ");
        
        Tugas tugasTujuan = null;
        for(Tugas t : tugasRepo.getAll()) {
            if(t.getIdTugas().equals(idTugas)) { tugasTujuan = t; break; }
        }

        if (tugasTujuan != null) {
            String idJawab = IdUtil.generate();
            Jawaban j = new Jawaban(idJawab, s, tugasTujuan, file);
            jawabanRepo.addJawaban(j);
            System.out.println("Jawaban berhasil dikirim! (ID: " + idJawab + ")");
        } else {
            System.out.println("Tugas dengan ID tersebut tidak ditemukan.");
        }
    }

    private void lihatNilai(Siswa s) {
        s.generateReport();
    }
}