package repository;

import model.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class JawabanRepository {

    private List<Jawaban> jawabanList = new ArrayList<>();
    private final String FILE_PATH = "src/main/java/data/jawaban.txt";

    public JawabanRepository() {
        loadFromFile();
    }

    public void addJawaban(Jawaban j) {
        jawabanList.add(j);
        saveToFile();
    }

    public List<Jawaban> getAll() {
        return jawabanList;
    }

    public List<Jawaban> findByTugas(String idTugas) {
        return jawabanList.stream()
                .filter(j -> j.getTugas() != null && j.getTugas().getIdTugas().equals(idTugas))
                .collect(Collectors.toList());
    }

    public List<Jawaban> findByUjian(String idUjian) {
        return jawabanList.stream()
                .filter(j -> j.getUjian() != null && j.getUjian().getIdUjian().equals(idUjian))
                .collect(Collectors.toList());
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Jawaban j : jawabanList) {
                // Format: id;idSiswa;idTugas;idUjian;file;tanggal
                String idTugas = (j.getTugas() != null) ? j.getTugas().getIdTugas() : "-";
                String idUjian = (j.getUjian() != null) ? j.getUjian().getIdUjian() : "-";
                
                bw.write(j.getIdJawaban() + ";" +
                         j.getSiswa().getIdUser() + ";" +
                         idTugas + ";" +
                         idUjian + ";" +
                         j.getFileJawaban() + ";" +
                         j.getTanggalSubmit());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Gagal menyimpan jawaban.txt: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        jawabanList.clear();
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                // id;idSiswa;idTugas;idUjian;file;tanggal
                if (d.length >= 6) {
                    Jawaban j = new Jawaban(d[0], d[4], d[5]);
                    jawabanList.add(j);
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Gagal memuat jawaban.txt: " + e.getMessage());
        }
    }
}