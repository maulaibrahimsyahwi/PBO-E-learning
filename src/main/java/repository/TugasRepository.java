package repository;

import model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class TugasRepository {

    private List<Tugas> tugasList = new ArrayList<>();
    private final String FILE_PATH = "src/main/java/data/tugas.txt";

    public TugasRepository() {
        loadFromFile();
    }

    public void addTugas(Tugas t) {
        tugasList.add(t);
        saveToFile();
    }

    public List<Tugas> getAll() {
        return tugasList;
    }

    public List<Tugas> findByKelas(Kelas k) {
        List<Tugas> hasil = new ArrayList<>();
        for (Tugas t : tugasList) {
            if (t.getKelas() != null &&
                t.getKelas().getIdKelas().equals(k.getIdKelas())) hasil.add(t);
        }
        return hasil;
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Tugas t : tugasList) {
                bw.write(
                        t.getIdTugas() + ";" +
                        t.getJudul() + ";" +
                        t.getDeskripsi() + ";" +
                        t.getDeadline() + ";" +
                        (t.getGuru() != null ? t.getGuru().getIdUser() : "-") + ";" +
                        (t.getKelas() != null ? t.getKelas().getIdKelas() : "-") + ";" +
                        (t.getMapel() != null ? t.getMapel().getIdMapel() : "-")
                );
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Gagal menyimpan tugas.txt: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;

            while ((line = br.readLine()) != null) {
                String[] d = line.split(";");

                Tugas t = new Tugas(d[0], d[1], d[2], LocalDate.parse(d[3]));
                tugasList.add(t);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat tugas.txt: " + e.getMessage());
        }
    }
}
