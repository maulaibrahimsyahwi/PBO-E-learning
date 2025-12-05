package repository;

import model.ForumDiskusi;
import model.Kelas;
import model.MataPelajaran;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ForumRepository {

    private List<ForumDiskusi> forumList = new ArrayList<>();
    private final String FILE_PATH = "data/forum.txt";

    public ForumRepository() {
        loadFromFile();
    }

    public void addPesan(ForumDiskusi f) {
        forumList.add(f);
        saveToFile();
    }

    public List<ForumDiskusi> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        return forumList.stream()
                .filter(f -> f.getMapel() != null && f.getMapel().equals(mapel))
                .filter(f -> f.getKelas() != null && f.getKelas().equals(kelas))
                .collect(Collectors.toList());
    }
    
    // Baru: Ambil semua balasan berdasarkan ID Topik
    public List<ForumDiskusi> getReplies(String threadId) {
        return forumList.stream()
                .filter(f -> f.getParentId().equals(threadId))
                .collect(Collectors.toList());
    }

    public List<ForumDiskusi> getAll() {
        return forumList;
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (ForumDiskusi f : forumList) {
                // Format: id;idUser;judul;isi;waktu;idKelas;idMapel;parentId
                bw.write(
                        f.getIdPesan() + ";" +
                        (f.getPengirim() != null ? f.getPengirim().getIdUser() : "-") + ";" +
                        f.getJudul() + ";" +
                        f.getIsiPesan() + ";" +
                        f.getWaktu() + ";" +
                        (f.getKelas() != null ? f.getKelas().getIdKelas() : "-") + ";" +
                        (f.getMapel() != null ? f.getMapel().getIdMapel() : "-") + ";" +
                        f.getParentId()
                );
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Gagal menyimpan forum.txt: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        forumList.clear();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                
                // Support format lama (migrasi otomatis saat runtime) & baru
                // Format Baru: id;idUser;judul;isi;waktu;idKelas;idMapel;parentId (len 8)
                if (d.length >= 8) {
                    ForumDiskusi f = new ForumDiskusi(d[0], d[2], d[3], d[4], d[7]);
                    forumList.add(f);
                } 
                // Fallback format lama (jika ada data lama)
                else if (d.length >= 6) {
                    // Anggap data lama sebagai ROOT topic tanpa judul spesifik
                    ForumDiskusi f = new ForumDiskusi(d[0], "Diskusi Umum", d[2], d[3], "ROOT"); 
                    forumList.add(f);
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Gagal memuat forum.txt: " + e.getMessage());
        }
    }
}