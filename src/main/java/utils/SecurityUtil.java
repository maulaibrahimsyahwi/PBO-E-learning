package utils;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtil {
    
    // Hash password baru
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    // Cek password
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false; 
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}