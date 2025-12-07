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
            String inputHash = SecurityUtil.hashPassword(password);
            if (user.getPassword().equals(inputHash)) {
                return user;
            }
            
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}