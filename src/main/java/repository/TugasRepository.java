package repository;

import model.Kelas;
import model.MataPelajaran;
import model.Tugas;
import utils.DateUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Tugas> getByKelas(Kelas kelas) {
        return tugasList.stream()
                .filter(t -> t.getKelas() != null && t.getKelas().equals(kelas))
                .collect(Collectors.toList());
    }

    public List<Tugas> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        return tugasList.stream()
                .filter(t -> t.getMapel() != null && t.getMapel().equals(mapel))
                .filter(t -> t.getKelas() != null && t.getKelas().equals(kelas))
                .collect(Collectors.toList());
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Tugas t : tugasList) {
                // Perbaikan: Menyertakan deskripsi dalam penyimpanan file
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

    public void loadFromFile() {
        tugasList.clear();

        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {

                if (line.isBlank()) continue;
                String[] d = line.split(";");

                // Perbaikan: Memastikan data cukup untuk konstruktor (min 4 field utama)
                // Format: id;judul;deskripsi;deadline;...
                if (d.length >= 4) {
                    String id = d[0];
                    String judul = d[1];
                    String desk = d[2];
                    String deadlineStr = d[3];

                    // Perbaikan: Parse tanggal dan gunakan konstruktor yang sesuai
                    Tugas t = new Tugas(id, judul, desk, DateUtil.parse(deadlineStr));
                    
                    // Note: Relasi guru, kelas, mapel direkonstruksi di DataReconstructor
                    tugasList.add(t);
                }
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat tugas.txt: " + e.getMessage());
        }
    }
}