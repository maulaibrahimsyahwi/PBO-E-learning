package repository;

import model.Kelas;
import java.io.*;
import java.util.*;

public class KelasRepository {

    private List<Kelas> kelasList = new ArrayList<>();
    private final String FILE_PATH = "data/kelas.txt";

    public KelasRepository() {
        loadFromFile();
    }

    public void addKelas(Kelas k) {
        kelasList.add(k);
        saveToFile();
    }

    public List<Kelas> getAll() {
        return kelasList;
    }

    public Kelas findById(String id) {
        for (Kelas k : kelasList) {
            if (k.getIdKelas().equals(id)) return k;
        }
        return null;
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Kelas k : kelasList) {
                bw.write(k.getIdKelas() + ";" +
                         k.getNamaKelas() + ";" +
                         k.getTingkat());
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Gagal menyimpan kelas.txt: " + e.getMessage());
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
                kelasList.add(new Kelas(d[0], d[1], d[2]));
            }

            br.close();
        } catch (Exception e) {
            System.out.println("Gagal memuat kelas.txt: " + e.getMessage());
        }
    }
}
