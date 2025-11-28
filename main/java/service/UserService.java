package service;

import model.*;
import java.util.*;

public class UserService {

    private List<User> users = new ArrayList<>();

    public UserService() {
        // Admin default
        users.add(new Admin("A001", "admin", "admin123", "Admin Sistem", "admin@sistem.com"));
    }

    public void register(User user) {
        users.add(user);
        System.out.println("Registrasi berhasil! Menunggu approve admin.\n");
    }

    public User login(String username, String password) {
        for (User u : users) {
            if (u.login(username, password)) {
                if (!u.isAktif()) {
                    System.out.println("Akun belum aktif! Hubungi admin.");
                    return null;
                }
                return u;
            }
        }
        System.out.println("Username atau password salah!");
        return null;
    }

    public List<User> getAll() {
        return users;
    }
}
