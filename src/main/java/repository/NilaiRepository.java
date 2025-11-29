package repository;

import model.*;
import java.io.*;
import java.util.*;

public class NilaiRepository {

    private List<Nilai> nilaiList = new ArrayList<>();
    private final String FILE_PATH = "src/main/java/data/nilai.txt";

    public NilaiRepository() {
        loadFromFile();
    }

    public void addNilai(Nilai n) {
        nilaiList.add(n);
        saveToFile();
    }

    public List<Nilai> getAll() {
        return nilaiList;
    }

    public List<Nilai> findBySiswa(String idSiswa) {
        List<Nilai> hasil = new ArrayList<>();
        for (Nilai n : nilaiList) {
            if (n.getSiswa() != null && n.getSiswa().getIdUser().equals(idSiswa)) {
                hasil.add(n);
            }
        }
        return hasil;
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Nilai n : nilaiList) {
                String idTugas = (n.getTugas() != null) ? n.getTugas().getIdTugas() : "-";
                String idUjian = (n.getUjian() != null) ? n.getUjian().getIdUjian() : "-";

                bw.write(n.getIdNilai() + ";" +
                         n.getSiswa().getIdUser() + ";" +
                         idTugas + ";" +
                         idUjian + ";" +
                         n.getNilaiAngka() + ";" +
                         n.getKeterangan());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Gagal menyimpan nilai.txt: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        nilaiList.clear();
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                if (d.length >= 6) {
                    nilaiList.add(new Nilai(d[0], Integer.parseInt(d[4]), d[5]));
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Gagal memuat nilai.txt: " + e.getMessage());
        }
    }
}