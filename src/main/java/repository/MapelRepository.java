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
                // Format: id;nama;deskripsi;tingkat
                bw.write(m.getIdMapel() + ";" +
                         m.getNamaMapel() + ";" +
                         m.getDeskripsi() + ";" +
                         m.getTingkat());
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
                if (line.trim().isEmpty()) continue;

                String[] d = line.split(";");
                
                // Handle format lama (length 3) dan baru (length 4)
                if (d.length >= 3) {
                    String id = d[0];
                    String nama = d[1];
                    String desk = d[2];
                    String tingkat = (d.length > 3) ? d[3] : "-"; // Default jika data lama

                    mapelList.add(new MataPelajaran(id, nama, desk, tingkat));
                }
            }

            br.close();
        } catch (Exception e) {
            System.out.println("Gagal memuat mapel.txt: " + e.getMessage());
        }
    }
}