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
    private final String FILE_PATH = "src/main/java/data/forum.txt";

    public ForumRepository() {
        loadFromFile();
    }

    public void addPesan(ForumDiskusi f) {
        forumList.add(f);
        saveToFile();
    }

    public List<ForumDiskusi> getAll() {
        return forumList;
    }

    public List<ForumDiskusi> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        return forumList.stream()
                .filter(f -> f.getMapel() != null && f.getMapel().equals(mapel))
                .filter(f -> f.getKelas() != null && f.getKelas().equals(kelas))
                .collect(Collectors.toList());
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (ForumDiskusi f : forumList) {
                bw.write(
                        f.getIdPesan() + ";" +
                        (f.getPengirim() != null ? f.getPengirim().getIdUser() : "-") + ";" +
                        f.getIsiPesan() + ";" +
                        f.getWaktu() + ";" +
                        (f.getKelas() != null ? f.getKelas().getIdKelas() : "-") + ";" +
                        (f.getMapel() != null ? f.getMapel().getIdMapel() : "-")
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
                
                if (d.length >= 6) {
                    ForumDiskusi f = new ForumDiskusi(d[0], d[2], d[3]);
                    forumList.add(f);
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Gagal memuat forum.txt: " + e.getMessage());
        }
    }
}