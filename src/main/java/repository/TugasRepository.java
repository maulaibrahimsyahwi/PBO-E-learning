package repository;

import model.Kelas;
import model.MataPelajaran;
import model.Tugas;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Tugas t : tugasList) {
                bw.write(
                        t.getIdTugas() + ";" +
                        t.getJudul() + ";" +
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

                String id = d[0];
                String judul = d[1];
                String deadline = d[2];

                Tugas t = new Tugas(id, judul, deadline);
                // guru, kelas, mapel direkonstruksi di DataReconstructor

                tugasList.add(t);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat tugas.txt: " + e.getMessage());
        }
    }

    // ==============================================
    // 🔥 Tambahan method untuk fitur SiswaView
    // ==============================================

    /** Ambil tugas berdasarkan kelas siswa */
    public List<Tugas> getByKelas(Kelas kelas) {
        return tugasList.stream()
                .filter(t -> t.getKelas() != null && t.getKelas().equals(kelas))
                .toList();
    }

    /** Ambil tugas berdasarkan kelas + mapel */
    public List<Tugas> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        return tugasList.stream()
                .filter(t -> t.getMapel() != null && t.getMapel().equals(mapel))
                .filter(t -> t.getKelas() != null && t.getKelas().equals(kelas))
                .toList();
    }
}
