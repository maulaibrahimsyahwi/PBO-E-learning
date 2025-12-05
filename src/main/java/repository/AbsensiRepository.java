package repository;
import model.Absensi;
import model.Siswa;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbsensiRepository {
    private List<Absensi> absensiList = new ArrayList<>();
    private final String FILE_PATH = "data/absensi.txt"; // Path folder data di root

    public AbsensiRepository() { loadFromFile(); }

    public void addAbsensi(Absensi a) {
        absensiList.add(a);
        saveToFile();
    }

    public boolean sudahAbsen(Siswa s, LocalDate tgl) {
        return absensiList.stream().anyMatch(a -> 
            a.getSiswa() != null && 
            a.getSiswa().getIdUser().equals(s.getIdUser()) && 
            a.getTanggal().equals(tgl));
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Absensi a : absensiList) {
                bw.write(a.getIdAbsensi() + ";" +
                         (a.getSiswa() != null ? a.getSiswa().getIdUser() : "-") + ";" +
                         (a.getKelas() != null ? a.getKelas().getIdKelas() : "-") + ";" +
                         a.getTanggal() + ";" + a.getWaktu());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadFromFile() {
        absensiList.clear();
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) return;
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.isBlank()) continue;
                String[] d = line.split(";");
                if (d.length >= 5) absensiList.add(new Absensi(d[0], d[3], d[4]));
            }
            br.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}