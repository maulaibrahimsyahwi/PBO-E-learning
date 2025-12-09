package utils;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    public static String validateEmail(String email, JComponent parent) {
        if (email == null || email.trim().isEmpty()) {
            showError(parent, "Email tidak boleh kosong!");
            return null;
        }
        if (!isValidEmail(email)) {
            showError(parent, "Format email tidak valid!\nContoh: user@example.com");
            return null;
        }
        return email.trim();
    }
    
    public static String validateUsername(String username, JComponent parent) {
        if (username == null || username.trim().isEmpty()) {
            showError(parent, "Username tidak boleh kosong!");
            return null;
        }
        if (username.length() < 3) {
            showError(parent, "Username minimal 3 karakter!");
            return null;
        }
        if (username.length() > 20) {
            showError(parent, "Username maksimal 20 karakter!");
            return null;
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError(parent, "Username hanya boleh huruf, angka, dan underscore!");
            return null;
        }
        return username.trim();
    }
    
    public static String validatePassword(String password, JComponent parent) {
        if (password == null || password.isEmpty()) {
            showError(parent, "Password tidak boleh kosong!");
            return null;
        }
        if (password.length() < 6) {
            showError(parent, "Password minimal 6 karakter!");
            return null;
        }
        if (password.length() > 50) {
            showError(parent, "Password maksimal 50 karakter!");
            return null;
        }
        return password;
    }
    
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        return hasUpper && hasLower && hasDigit;
    }
    
    public static String validateNIS(String nis, JComponent parent) {
        if (nis == null || nis.trim().isEmpty()) {
            showError(parent, "NIS tidak boleh kosong!");
            return null;
        }
        if (!nis.matches("^\\d{6,10}$")) {
            showError(parent, "NIS harus 6-10 digit angka!\nContoh: 12345678");
            return null;
        }
        return nis.trim();
    }
    
    public static String validateNIP(String nip, JComponent parent) {
        if (nip == null || nip.trim().isEmpty()) {
            showError(parent, "NIP tidak boleh kosong!");
            return null;
        }
        if (!nip.matches("^\\d{8,18}$")) {
            showError(parent, "NIP harus 8-18 digit angka!\nContoh: 198501012010011001");
            return null;
        }
        return nip.trim();
    }
    
    public static String validateName(String name, JComponent parent) {
        if (name == null || name.trim().isEmpty()) {
            showError(parent, "Nama tidak boleh kosong!");
            return null;
        }
        if (name.trim().length() < 3) {
            showError(parent, "Nama minimal 3 karakter!");
            return null;
        }
        if (name.trim().length() > 100) {
            showError(parent, "Nama maksimal 100 karakter!");
            return null;
        }
        if (!name.matches("^[a-zA-Z\\s.,']+$")) {
            showError(parent, "Nama hanya boleh berisi huruf dan spasi!");
            return null;
        }
        return name.trim();
    }
    
    public static LocalDate validateDate(String dateStr, JComponent parent) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            showError(parent, "Tanggal tidak boleh kosong!");
            return null;
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr.trim(), formatter);
        } catch (DateTimeParseException e) {
            showError(parent, "Format tanggal salah!\nGunakan format: yyyy-MM-dd\nContoh: 2025-12-31");
            return null;
        }
    }
    
    public static boolean isDateInPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }
    
    public static boolean isDateInFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }
    
    public static Integer validateInteger(String value, String fieldName, JComponent parent) {
        if (value == null || value.trim().isEmpty()) {
            showError(parent, fieldName + " tidak boleh kosong!");
            return null;
        }
        
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            showError(parent, fieldName + " harus berupa angka!\nContoh: 100");
            return null;
        }
    }
    
    public static Integer validateIntegerRange(String value, String fieldName, int min, int max, JComponent parent) {
        Integer num = validateInteger(value, fieldName, parent);
        if (num == null) return null;
        
        if (num < min || num > max) {
            showError(parent, fieldName + " harus antara " + min + " dan " + max + "!");
            return null;
        }
        
        return num;
    }
    
    public static String validateNotEmpty(String value, String fieldName, JComponent parent) {
        if (value == null || value.trim().isEmpty()) {
            showError(parent, fieldName + " tidak boleh kosong!");
            return null;
        }
        return value.trim();
    }
    
    public static String validateMinLength(String value, String fieldName, int minLength, JComponent parent) {
        String validated = validateNotEmpty(value, fieldName, parent);
        if (validated == null) return null;
        
        if (validated.length() < minLength) {
            showError(parent, fieldName + " minimal " + minLength + " karakter!");
            return null;
        }
        
        return validated;
    }
    
    public static String validateMaxLength(String value, String fieldName, int maxLength, JComponent parent) {
        String validated = validateNotEmpty(value, fieldName, parent);
        if (validated == null) return null;
        
        if (validated.length() > maxLength) {
            showError(parent, fieldName + " maksimal " + maxLength + " karakter!");
            return null;
        }
        
        return validated;
    }
    
    public static boolean isValidFileExtension(String filename, String[] allowedExtensions) {
        if (filename == null || filename.isEmpty()) return false;
        
        String lowerFilename = filename.toLowerCase();
        for (String ext : allowedExtensions) {
            if (lowerFilename.endsWith(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean validateFileSize(long fileSizeBytes, long maxSizeMB, JComponent parent) {
        long maxSizeBytes = maxSizeMB * 1024 * 1024;
        
        if (fileSizeBytes > maxSizeBytes) {
            showError(parent, "Ukuran file terlalu besar!\nMaksimal: " + maxSizeMB + " MB");
            return false;
        }
        
        return true;
    }
    
    private static void showError(JComponent parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Validasi Error", 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    public static void showSuccess(JComponent parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Sukses", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public static boolean confirmAction(JComponent parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent, 
            message, 
            "Konfirmasi", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    public static class ValidationBuilder {
        private JComponent parent;
        private boolean isValid = true;
        private StringBuilder errors = new StringBuilder();
        
        public ValidationBuilder(JComponent parent) {
            this.parent = parent;
        }
        
        public ValidationBuilder validateEmail(String email, String fieldLabel) {
            if (!isValidEmail(email)) {
                isValid = false;
                errors.append("• ").append(fieldLabel).append(": Format email tidak valid\n");
            }
            return this;
        }
        
        public ValidationBuilder validateNotEmpty(String value, String fieldLabel) {
            if (value == null || value.trim().isEmpty()) {
                isValid = false;
                errors.append("• ").append(fieldLabel).append(": Tidak boleh kosong\n");
            }
            return this;
        }
        
        public ValidationBuilder validateMinLength(String value, String fieldLabel, int minLength) {
            if (value != null && value.length() < minLength) {
                isValid = false;
                errors.append("• ").append(fieldLabel).append(": Minimal ").append(minLength).append(" karakter\n");
            }
            return this;
        }
        
        public boolean validate() {
            if (!isValid) {
                JOptionPane.showMessageDialog(
                    parent,
                    "Terdapat kesalahan pada form:\n\n" + errors.toString(),
                    "Validasi Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            return isValid;
        }
    }
}