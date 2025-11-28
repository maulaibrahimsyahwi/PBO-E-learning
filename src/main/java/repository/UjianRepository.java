package repository;

import model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class UjianRepository {

    private List<Ujian> ujianList = new ArrayList<>();
    private final String FILE_PATH = "src/main/java/data/ujian.txt";

    public UjianRepository() {
        loadFromFile();
    }

    public void addUjian(Ujian u) {
        ujianList.add(u);
        saveToFile();
    }

    public List<Ujian> getAll() {
        return ujianList;
    }

    public List<Ujian> findByKelas(Kelas k) {
        List<Ujian> hasil = new ArrayList<>();
        for (Ujian u : ujianList) {
            if (u.getKelas() != null &&
                u.getKelas().getIdKelas().equals(k.getIdKelas())) hasil.add(u);
        }
        return hasil;
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Ujian u : ujianList) {
                bw.write(
                        u.getIdUjian() + ";" +
                        u.getJenisUjian() + ";" +
                        u.getTanggal() + ";" +
                        u.getDurasi() + ";" +
                        (u.getGuru() != null ? u.getGuru().getIdUser() : "-") + ";" +
                        (u.getKelas() != null ? u.getKelas().getIdKelas() : "-") + ";" +
                        (u.getMapel() != null ? u.getMapel().getIdMapel() : "-")
                );
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Gagal menyimpan ujian.txt: " + e.getMessage());
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
                Ujian u = new Ujian(d[0], d[1], LocalDate.parse(d[2]), Integer.parseInt(d[3]));
                ujianList.add(u);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat ujian.txt: " + e.getMessage());
        }
    }
}
