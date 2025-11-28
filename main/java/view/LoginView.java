package view;

import model.*;
import repository.UserRepository;
import utils.InputUtil;

public class LoginView {

    private UserRepository userRepo;

    public LoginView(UserRepository repo) {
        this.userRepo = repo;
    }

    public User login() {
        System.out.println("\n=== LOGIN ===");
        String username = InputUtil.inputString("Username: ");
        String password = InputUtil.inputString("Password: ");

        User u = userRepo.findByUsername(username);
        if (u == null || !u.login(username, password)) {
            System.out.println("Login gagal!");
            return null;
        }

        System.out.println("Login berhasil. Selamat datang, " + u.getNamaLengkap());
        return u;
    }
}
