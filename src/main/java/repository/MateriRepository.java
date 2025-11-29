package repository;

import model.Kelas;
import model.MataPelajaran;
import model.Materi;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MateriRepository {

    private List<Materi> materiList = new ArrayList<>();
    private final String FILE_PATH = "src/main/java/data/materi.txt";

    public MateriRepository() {
        loadFromFile();
    }

    public void addMateri(Materi m) {
        materiList.add(m);
        saveToFile();
    }

    public List<Materi> getAll() {
        return materiList;
    }

    public List<Materi> getByKelas(Kelas kelas) {
        return materiList.stream()
                .filter(m -> m.getKelas() != null && m.getKelas().equals(kelas))
                .collect(Collectors.toList());
    }

    public List<Materi> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        return materiList.stream()
                .filter(m -> m.getMapel() != null && m.getMapel().equals(mapel))
                .filter(m -> m.getKelas() != null && m.getKelas().equals(kelas))
                .collect(Collectors.toList());
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Materi m : materiList) {
                bw.write(
                        m.getIdMateri() + ";" +
                        m.getJudul() + ";" +
                        (m.getGuru() != null ? m.getGuru().getIdUser() : "-") + ";" +
                        (m.getKelas() != null ? m.getKelas().getIdKelas() : "-") + ";" +
                        (m.getMapel() != null ? m.getMapel().getIdMapel() : "-")
                );
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Gagal menyimpan materi.txt: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        materiList.clear();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                Materi m = new Materi(d[0], d[1]); 
                materiList.add(m);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Gagal memuat materi.txt: " + e.getMessage());
        }
    }
}