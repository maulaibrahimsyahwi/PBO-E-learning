package repository;

import model.*;
import java.io.*;
import java.util.*;

public class UserRepository {

    private List<User> userList = new ArrayList<>();
    private final String FILE_PATH = "src/main/java/data/users.txt";

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
                            a.getEmail() + ";ADMIN;-;-");
                } else if (u instanceof Guru g) {
                    bw.write(g.getIdUser() + ";" + g.getUsername() + ";" +
                            g.getPassword() + ";" + g.getNamaLengkap() + ";" +
                            g.getEmail() + ";GURU;" + g.getNip() + ";" +
                            g.getSpesialisasi());
                } else if (u instanceof Siswa s) {
                    bw.write(s.getIdUser() + ";" + s.getUsername() + ";" +
                            s.getPassword() + ";" + s.getNamaLengkap() + ";" +
                            s.getEmail() + ";SISWA;" + s.getNis() + ";" +
                            s.getAngkatan());
                }
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Gagal menyimpan users.txt: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] d = line.split(";");

                String id = d[0];
                String username = d[1];
                String password = d[2];
                String nama = d[3];
                String email = d[4];
                String role = d[5];

                if (role.equals("ADMIN")) {
                    userList.add(new Admin(id, username, password, nama, email));

                } else if (role.equals("GURU")) {
                    String nip = d[6];
                    String spes = d[7];
                    userList.add(new Guru(id, username, password, nama, email, nip, spes));

                } else if (role.equals("SISWA")) {
                    String nis = d[6];
                    String ang = d[7];
                    userList.add(new Siswa(id, username, password, nama, email, nis, ang));
                }
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat users.txt: " + e.getMessage());
        }
    }
}
