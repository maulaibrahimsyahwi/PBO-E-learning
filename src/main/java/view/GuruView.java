package view;

import model.*;
import repository.*;
import utils.IdUtil;
import utils.InputUtil;
import utils.DateUtil;

import java.util.List;

public class GuruView {

    private MateriRepository materiRepo;
    private TugasRepository tugasRepo;
    private UjianRepository ujianRepo;
    private JawabanRepository jawabanRepo;
    private NilaiRepository nilaiRepo;
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;
    private UserRepository userRepo;
    private ForumRepository forumRepo;

    public GuruView(MateriRepository m,
                    TugasRepository t,
                    UjianRepository u,
                    JawabanRepository j,
                    NilaiRepository n,
                    KelasRepository k,
                    MapelRepository mapelRepo,
                    UserRepository userRepo,
                    ForumRepository forumRepo) {
        this.materiRepo = m;
        this.tugasRepo = t;
        this.ujianRepo = u;
        this.jawabanRepo = j;
        this.nilaiRepo = n;
        this.kelasRepo = k;
        this.mapelRepo = mapelRepo;
        this.userRepo = userRepo;
        this.forumRepo = forumRepo;
    }

    public void menu(Guru guru) {
        while (true) {
            System.out.println("\n=== MENU UTAMA GURU (" + guru.getNamaLengkap() + ") ===");
            System.out.println("1. Masuk ke Kelas (Kelola KBM)");
            System.out.println("2. Profil & Akun");
            System.out.println("0. Logout");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            switch (pilih) {
                case 0 -> { return; }
                case 1 -> pilihKelas(guru); 
                case 2 -> menuProfil(guru);
                default -> System.out.println("Menu tidak tersedia!");
            }
        }
    }

    private void pilihKelas(Guru guru) {
        List<Kelas> kelasList;
        
        if (!guru.getDaftarKelas().isEmpty()) {
            kelasList = guru.getDaftarKelas();
            System.out.println("\n=== KELAS YANG ANDA AMPU ===");
        } else {
            kelasList = kelasRepo.getAll();
            System.out.println("\n=== SEMUA KELAS (Belum ada assignment spesifik) ===");
        }

        if (kelasList.isEmpty()) {
            System.out.println("Belum ada kelas terdaftar di sistem.");
            return;
        }

        int no = 1;
        for (Kelas k : kelasList) {
            System.out.println(no++ + ". " + k.getNamaKelas() + " (Tingkat " + k.getTingkat() + ")");
        }
        System.out.println("0. Kembali");

        int pilih = InputUtil.inputInt("Pilih kelas: ");
        if (pilih == 0) return;

        if (pilih < 1 || pilih > kelasList.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        Kelas kelasTerpilih = kelasList.get(pilih - 1);
        pilihMapel(guru, kelasTerpilih);
    }

    private void pilihMapel(Guru guru, Kelas kelas) {
        List<MataPelajaran> mapelGuru = guru.getMapelDiampu();
        
        if (mapelGuru.isEmpty()) {
            System.out.println("Anda belum di-assign ke mata pelajaran apapun. Hubungi Admin.");
            return;
        }

        System.out.println("\n=== PILIH MATA PELAJARAN (Kelas " + kelas.getNamaKelas() + ") ===");
        int no = 1;
        for (MataPelajaran m : mapelGuru) {
            System.out.println(no++ + ". " + m.getNamaMapel());
        }
        System.out.println("0. Kembali");

        int pilih = InputUtil.inputInt("Pilih mapel: ");
        if (pilih == 0) return;

        if (pilih < 1 || pilih > mapelGuru.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        MataPelajaran mapelTerpilih = mapelGuru.get(pilih - 1);
        dashboardKBM(guru, kelas, mapelTerpilih);
    }

    private void dashboardKBM(Guru guru, Kelas kelas, MataPelajaran mapel) {
        while (true) {
            System.out.println("\n=== KELOLA: " + mapel.getNamaMapel() + " | KELAS: " + kelas.getNamaKelas() + " ===");
            System.out.println("1. Tambah Materi");
            System.out.println("2. Buat Tugas");
            System.out.println("3. Buat Ujian");
            System.out.println("4. Penilaian (Tugas/Ujian)");
            System.out.println("5. Forum Diskusi");
            System.out.println("0. Kembali ke Menu Awal");

            int pilih = InputUtil.inputInt("Pilih menu: ");

            switch (pilih) {
                case 0 -> { return; }
                case 1 -> tambahMateri(guru, kelas, mapel);
                case 2 -> tambahTugas(guru, kelas, mapel);
                case 3 -> tambahUjian(guru, kelas, mapel);
                case 4 -> menuPenilaian(guru, kelas, mapel);
                case 5 -> forumDiskusi(guru, kelas, mapel);
                default -> System.out.println("Menu tidak tersedia!");
            }
        }
    }

    private void tambahMateri(Guru guru, Kelas kelas, MataPelajaran mapel) {
        System.out.println("\n--- Tambah Materi Baru ---");
        
        String judul = InputUtil.inputString("Judul Materi: ");
        String desk = InputUtil.inputString("Deskripsi: ");
        String file = InputUtil.inputString("Nama File (contoh: slide.pdf): ");

        String id = IdUtil.generate(); 
        Materi m = new Materi(id, judul, desk, file);
        m.setGuru(guru);
        m.setMapel(mapel);
        m.setKelas(kelas);

        materiRepo.addMateri(m);
        System.out.println("Sukses! Materi ditambahkan (ID: " + id + ")");
    }

    private void tambahTugas(Guru guru, Kelas kelas, MataPelajaran mapel) {
        System.out.println("\n--- Buat Tugas Baru ---");

        String judul = InputUtil.inputString("Judul Tugas: ");
        String desk = InputUtil.inputString("Deskripsi: ");
        String tgl = InputUtil.inputString("Deadline (yyyy-MM-dd): ");

        try {
            String id = IdUtil.generate(); 
            Tugas tugas = new Tugas(id, judul, desk, DateUtil.parse(tgl));
            tugas.setGuru(guru);
            tugas.setMapel(mapel);
            tugas.setKelas(kelas);

            tugasRepo.addTugas(tugas);
            System.out.println("Sukses! Tugas berhasil dibuat (ID: " + id + ")");
        } catch (Exception e) {
            System.out.println("Format tanggal salah. Gunakan yyyy-MM-dd.");
        }
    }

    private void tambahUjian(Guru guru, Kelas kelas, MataPelajaran mapel) {
        System.out.println("\n--- Buat Ujian Baru ---");

        String jenis = InputUtil.inputString("Jenis (UTS/UAS/Kuis): ");
        String tgl = InputUtil.inputString("Tanggal (yyyy-MM-dd): ");
        int durasi = InputUtil.inputInt("Durasi (menit): ");

        try {
            String id = IdUtil.generate();
            Ujian ujian = new Ujian(id, jenis, DateUtil.parse(tgl), durasi);
            ujian.setGuru(guru);
            ujian.setMapel(mapel);
            ujian.setKelas(kelas);

            ujianRepo.addUjian(ujian);
            System.out.println("Sukses! Ujian dijadwalkan (ID: " + id + ")");
        } catch (Exception e) {
            System.out.println("Format tanggal salah.");
        }
    }

    private void menuPenilaian(Guru guru, Kelas kelas, MataPelajaran mapel) {
        System.out.println("\n--- Penilaian (" + kelas.getNamaKelas() + " - " + mapel.getNamaMapel() + ") ---");
        System.out.println("1. Lihat Jawaban Siswa");
        System.out.println("2. Input Nilai");
        System.out.println("0. Kembali");
        
        int p = InputUtil.inputInt("Pilih: ");
        if (p == 1) lihatJawabanFilter(kelas, mapel);
        else if (p == 2) beriNilaiFilter(guru, kelas, mapel);
    }

    private void lihatJawabanFilter(Kelas kelas, MataPelajaran mapel) {
        List<Tugas> listTugas = tugasRepo.getByMapelAndKelas(mapel, kelas);
        if (listTugas.isEmpty()) {
            System.out.println("Belum ada tugas di kelas ini.");
            return;
        }

        System.out.println("Daftar Tugas:");
        for(Tugas t : listTugas) {
            System.out.println("- [" + t.getIdTugas() + "] " + t.getJudul());
        }

        String idTugas = InputUtil.inputString("Masukkan ID Tugas: ");
        List<Jawaban> list = jawabanRepo.findByTugas(idTugas);
        
        if (list.isEmpty()) {
            System.out.println("Belum ada jawaban masuk.");
            return;
        }
        
        System.out.println("--- Jawaban Masuk ---");
        for (Jawaban j : list) {
            System.out.println("Siswa: " + j.getSiswa().getNamaLengkap() + 
                               " | ID Siswa: " + j.getSiswa().getIdUser() +
                               " | File: " + j.getFileJawaban() + 
                               " | Tgl: " + j.getTanggalSubmit());
        }
    }

    private void beriNilaiFilter(Guru guru, Kelas kelas, MataPelajaran mapel) {
        String idTugas = InputUtil.inputString("Masukkan ID Tugas yang dinilai: ");
        
        Tugas t = null;
        for (Tugas task : tugasRepo.getAll()) {
            if (task.getIdTugas().equals(idTugas) && 
                task.getMapel().equals(mapel) && 
                task.getKelas().equals(kelas)) {
                t = task; 
                break;
            }
        }
        
        if (t == null) {
            System.out.println("Tugas tidak ditemukan di kelas/mapel ini.");
            return;
        }

        String idSiswa = InputUtil.inputString("Masukkan ID Siswa: ");
        
        Jawaban jawab = null;
        for (Jawaban j : jawabanRepo.findByTugas(idTugas)) {
            if (j.getSiswa().getIdUser().equals(idSiswa)) {
                jawab = j;
                break;
            }
        }

        if (jawab == null) {
            System.out.println("Siswa ini belum mengumpulkan jawaban.");
            return;
        }

        int angka = InputUtil.inputInt("Nilai Angka (0-100): ");
        String ket = InputUtil.inputString("Keterangan: ");

        String idNilai = IdUtil.generate();
        Nilai n = new Nilai(idNilai, jawab.getSiswa(), t, angka, ket);
        
        nilaiRepo.addNilai(n);
        jawab.getSiswa().tambahNilai(n);
        
        System.out.println("Nilai berhasil disimpan (ID Nilai: " + idNilai + ")");
    }

    private void forumDiskusi(Guru guru, Kelas kelas, MataPelajaran mapel) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("   FORUM DISKUSI: " + mapel.getNamaMapel().toUpperCase());
            System.out.println("========================================");
            
            List<ForumDiskusi> chats = forumRepo.getByMapelAndKelas(mapel, kelas);
            
            if (chats.isEmpty()) {
                System.out.println("\n[!] Belum ada diskusi di mapel ini.");
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
                String pesan = InputUtil.inputString("Ketik pesan: ");
                if (!pesan.isBlank()) {
                    String idPesan = IdUtil.generate();
                    ForumDiskusi fd = new ForumDiskusi(idPesan, guru, pesan, kelas, mapel);
                    forumRepo.addPesan(fd);
                    System.out.println(">> Pesan terkirim!");
                }
            }
        }
    }

    private void menuProfil(Guru guru) {
        while (true) {
            System.out.println("\n=== PROFIL GURU ===");
            System.out.println("Nama   : " + guru.getNamaLengkap());
            System.out.println("NIP    : " + guru.getNip());
            System.out.println("Email  : " + guru.getEmail());
            System.out.println("Bidang : " + guru.getSpesialisasi());
            System.out.println("-------------------------");
            System.out.println("1. Ubah Password");
            System.out.println("0. Kembali");

            int pilih = InputUtil.inputInt("Pilih: ");
            if (pilih == 0) return;

            if (pilih == 1) {
                ubahPassword(guru);
            }
        }
    }

    private void ubahPassword(Guru guru) {
        System.out.println("\n--- Ubah Password ---");
        String oldPass = InputUtil.inputString("Masukkan password lama: ");

        if (!guru.getPassword().equals(oldPass)) {
            System.out.println("Password lama salah!");
            return;
        }

        String newPass = InputUtil.inputString("Masukkan password baru: ");
        if (newPass.isBlank()) {
            System.out.println("Password tidak boleh kosong.");
            return;
        }

        String confirmPass = InputUtil.inputString("Konfirmasi password baru: ");
        if (!newPass.equals(confirmPass)) {
            System.out.println("Konfirmasi password tidak cocok!");
            return;
        }

        guru.setPassword(newPass);
        userRepo.saveToFile();
        System.out.println("Password berhasil diubah!");
    }
}