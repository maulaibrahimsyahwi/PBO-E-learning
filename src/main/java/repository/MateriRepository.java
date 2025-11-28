package repository;

import model.*;

import java.io.*;
import java.util.*;

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

    public List<Materi> findByKelas(Kelas kelas) {
        List<Materi> hasil = new ArrayList<>();
        for (Materi m : materiList) {
            if (m.getKelas() != null &&
                m.getKelas().getIdKelas().equals(kelas.getIdKelas())) {
                hasil.add(m);
            }
        }
        return hasil;
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Materi m : materiList) {
                bw.write(
                        m.getIdMateri() + ";" +
                        m.getJudul() + ";" +
                        m.getDeskripsi() + ";" +
                        m.getFileMateri() + ";" +
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

    private void loadFromFile() {
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;

            while ((line = br.readLine()) != null) {
                String[] d = line.split(";");

                Materi m = new Materi(d[0], d[1], d[2], d[3]);
                materiList.add(m);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat materi.txt: " + e.getMessage());
        }
    }
}
