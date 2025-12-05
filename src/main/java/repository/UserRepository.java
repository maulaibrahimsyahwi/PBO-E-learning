package repository;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepository {

    private List<User> userList = new ArrayList<>();
    private final String FILE_PATH = "data/users.txt";

    public UserRepository() {
        loadFromFile();
    }

    public void addUser(User u) {
        userList.add(u);
        saveToFile();
    }

    public List<User> getAll() {
        return userList;
    }

    public User findByUsername(String username) {
        for (User u : userList) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (User u : userList) {
                if (u instanceof Admin a) {
                    bw.write(a.getIdUser() + ";" + a.getUsername() + ";" +
                             a.getPassword() + ";" + a.getNamaLengkap() + ";" +
                             a.getEmail() + ";ADMIN;-;-;-;-");
                } else if (u instanceof Guru g) {
                    String listMapel = "-";
                    if (!g.getMapelDiampu().isEmpty()) {
                        listMapel = g.getMapelDiampu().stream()
                                .map(MataPelajaran::getIdMapel)
                                .collect(Collectors.joining(","));
                    }

                    String listKelas = "-";
                    if (!g.getDaftarKelas().isEmpty()) {
                        listKelas = g.getDaftarKelas().stream()
                                .map(Kelas::getIdKelas)
                                .collect(Collectors.joining(","));
                    }

                    bw.write(g.getIdUser() + ";" + g.getUsername() + ";" +
                             g.getPassword() + ";" + g.getNamaLengkap() + ";" +
                             g.getEmail() + ";GURU;" + g.getNip() + ";" +
                             g.getSpesialisasi() + ";" + listMapel + ";" + listKelas);
                } else if (u instanceof Siswa s) {
                    String idKelas = s.getIdKelas() != null ? s.getIdKelas() : "-";
                    bw.write(s.getIdUser() + ";" + s.getUsername() + ";" +
                             s.getPassword() + ";" + s.getNamaLengkap() + ";" +
                             s.getEmail() + ";SISWA;" + s.getNis() + ";" +
                             s.getAngkatan() + ";" + idKelas + ";-");
                }
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Gagal menyimpan users.txt: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        userList.clear();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(";");
                if (d.length < 6) continue;

                String id      = d[0];
                String username= d[1];
                String password= d[2];
                String nama    = d[3];
                String email   = d[4];
                String role    = d[5];

                if ("ADMIN".equals(role)) {
                    userList.add(new Admin(id, username, password, nama, email));

                } else if ("GURU".equals(role)) {
                    String nip  = d.length > 6 ? d[6] : "-";
                    String spes = d.length > 7 ? d[7] : "-";
                    
                    Guru g = new Guru(id, username, password, nama, email, nip, spes);
                    
                    if (d.length > 8 && !d[8].equals("-")) {
                        String[] mapelIds = d[8].split(",");
                        for(String mid : mapelIds) g.addTempIdMapel(mid);
                    }
                    
                    if (d.length > 9 && !d[9].equals("-")) {
                        String[] kelasIds = d[9].split(",");
                        for(String kid : kelasIds) g.addTempIdKelas(kid);
                    }
                    
                    userList.add(g);

                } else if ("SISWA".equals(role)) {
                    String nis      = d.length > 6 ? d[6] : "-";
                    String angkatan = d.length > 7 ? d[7] : "-";
                    String idKelas  = d.length > 8 ? d[8] : "-";

                    Siswa s = new Siswa(id, username, password, nama, email, nis, angkatan);
                    if (!"-".equals(idKelas)) {
                        s.setIdKelas(idKelas);
                    }
                    userList.add(s);
                }
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat users.txt: " + e.getMessage());
        }
    }
}