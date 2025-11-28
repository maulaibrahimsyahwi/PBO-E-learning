package repository;

import model.MataPelajaran;

import java.io.*;
import java.util.*;

public class MapelRepository {

    private List<MataPelajaran> mapelList = new ArrayList<>();
    private final String FILE_PATH = "src/main/java/data/mapel.txt";

    public MapelRepository() {
        loadFromFile();
    }

    public void addMapel(MataPelajaran m) {
        mapelList.add(m);
        saveToFile();
    }

    public List<MataPelajaran> getAll() {
        return mapelList;
    }

    public MataPelajaran findById(String id) {
        for (MataPelajaran m : mapelList) {
            if (m.getIdMapel().equals(id)) return m;
        }
        return null;
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (MataPelajaran m : mapelList) {
                bw.write(m.getIdMapel() + ";" +
                         m.getNamaMapel() + ";" +
                         m.getDeskripsi());
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Gagal menyimpan mapel.txt: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] d = line.split(";");
                mapelList.add(new MataPelajaran(d[0], d[1], d[2]));
            }

            br.close();
        } catch (Exception e) {
            System.out.println("Gagal memuat mapel.txt: " + e.getMessage());
        }
    }
}
