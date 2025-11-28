package repository;

import model.*;
import java.util.*;
import java.io.*;

public class UserRepository {

    private List<User> users = new ArrayList<>();
    private static final String FILE_PATH = "data/users.txt";

    public UserRepository() {
        loadFromFile();
    }


    public void addUser(User user) {
        users.add(user);
        saveToFile();
    }

    public User findByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAll() {
        return users;
    }

  

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Gagal membuat file users.txt: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(";");
                if (parts.length < 6) continue;
                String id = parts[0];
                String username = parts[1];
                String password = parts[2];
                String nama = parts[3];
                String email = parts[4];
                String role = parts[5];

                User u = null;
                switch (role) {
                    case "ADMIN" -> u = new Admin(id, username, password, nama, email);
                    case "GURU" -> {
                        u = new Guru(id, username, password, nama, email, "-", "-");
                    }
                    case "SISWA" -> {
                        u = new Siswa(id, username, password, nama, email, "-", "-");
                    }
                }

                if (u != null) {
                    users.add(u);
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca users.txt: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User u : users) {
                String role;
                if (u instanceof Admin) role = "ADMIN";
                else if (u instanceof Guru) role = "GURU";
                else if (u instanceof Siswa) role = "SISWA";
                else role = "UNKNOWN";

                bw.write(u.getIdUser() + ";" 
                        + u.getUsername() + ";" 
                        + u.getPassword() + ";" 
                        + u.getNamaLengkap() + ";" 
                        + u.getEmail() + ";" 
                        + role);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Gagal menyimpan users.txt: " + e.getMessage());
        }
    }
}
