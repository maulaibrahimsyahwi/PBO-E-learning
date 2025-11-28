package repository;

import model.*;

import java.io.*;
import java.util.*;

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
        List<Jawaban> hasil = new ArrayList<>();
        for (Jawaban j : jawabanList) {
            if (j.getTugas().getIdTugas().equals(idTugas)) hasil.add(j);
        }
        return hasil;
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Jawaban j : jawabanList) {
                bw.write(
                        j.getIdJawaban() + ";" +
                        j.getSiswa().getIdUser() + ";" +
                        j.getTugas().getIdTugas() + ";" +
                        j.getFileJawaban() + ";" +
                        j.getTanggalSubmit()
                );
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Gagal menyimpan jawaban.txt: " + e.getMessage());
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
                // rekonstruksi objek lengkap dilakukan AFTER load
                jawabanList.add(new Jawaban(d[0], null, null, d[3]));
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat jawaban.txt: " + e.getMessage());
        }
    }
}
