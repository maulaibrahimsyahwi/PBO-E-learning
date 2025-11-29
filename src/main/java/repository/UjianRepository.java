package repository;

import model.Kelas;
import model.MataPelajaran;
import model.Ujian;
import utils.DateUtil;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Ujian> getByKelas(Kelas kelas) {
        return ujianList.stream()
                .filter(u -> u.getKelas() != null && u.getKelas().equals(kelas))
                .collect(Collectors.toList());
    }

    public List<Ujian> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        return ujianList.stream()
                .filter(u -> u.getMapel() != null && u.getMapel().equals(mapel))
                .filter(u -> u.getKelas() != null && u.getKelas().equals(kelas))
                .collect(Collectors.toList());
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
        ujianList.clear();
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                
                if (d.length >= 4) {
                    Ujian u = new Ujian(d[0], d[1], LocalDate.parse(d[2]), Integer.parseInt(d[3]));
                    ujianList.add(u);
                }
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat ujian.txt: " + e.getMessage());
        }
    }
}