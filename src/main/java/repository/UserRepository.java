package repository;

import model.*;
import java.util.*;

public class UserRepository {

    private List<User> users = new ArrayList<>();

    public UserRepository() {
        // Default admin
        users.add(new Admin("A001", "admin", "admin123", "Admin Sistem", "admin@lms.com"));
    }

    public void addUser(User user) {
        users.add(user);
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
}
