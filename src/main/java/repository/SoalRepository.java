package repository;
import model.Soal;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SoalRepository {
    private List<Soal> soalList = new ArrayList<>();
    private final String FILE_PATH = "data/soal.txt";

    public SoalRepository() { loadFromFile(); }

    public void addSoal(Soal s) {
        soalList.add(s);
        saveToFile();
    }

    public List<Soal> getByUjian(String idUjian) {
        return soalList.stream().filter(s -> s.getIdUjian().equals(idUjian)).collect(Collectors.toList());
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Soal s : soalList) {
                // Format: id;idUjian;tipe;tanya;A;B;C;D;kunci
                bw.write(s.getIdSoal() + ";" + 
                         s.getIdUjian() + ";" + 
                         s.getTipeSoal() + ";" +
                         s.getPertanyaan() + ";" +
                         s.getPilA() + ";" + s.getPilB() + ";" + s.getPilC() + ";" + s.getPilD() + ";" + 
                         s.getKunciJawaban());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadFromFile() {
        soalList.clear();
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) return;
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.isBlank()) continue;
                String[] d = line.split(";");
                // Format baru (9 kolom) vs lama (8 kolom)
                if (d.length >= 9) {
                    soalList.add(new Soal(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8]));
                } else if (d.length >= 8) {
                    // Migrasi data lama
                    soalList.add(new Soal(d[0], d[1], "PG", d[2], d[3], d[4], d[5], d[6], d[7]));
                }
            }
            br.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}