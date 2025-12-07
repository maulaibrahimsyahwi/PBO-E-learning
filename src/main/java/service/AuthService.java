package service;

import model.User;
import repository.UserRepository;
import utils.SecurityUtil;

public class AuthService {
    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User login(String username, String password) {
        User user = userRepo.findByUsername(username);
        
        if (user != null) {
            if (SecurityUtil.checkPassword(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}