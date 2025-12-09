package utils;

import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class LoggerUtil {
    
    private static final String LOG_DIR = "data/logs/";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static Logger applicationLogger;
    private static Logger errorLogger;
    private static Logger auditLogger;
    
    static {
        try {
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            initializeLoggers();
        } catch (Exception e) {
            System.err.println("Failed to initialize loggers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void initializeLoggers() throws IOException {
        applicationLogger = Logger.getLogger("ApplicationLogger");
        FileHandler appHandler = new FileHandler(LOG_DIR + "application.log", true);
        appHandler.setFormatter(new CustomFormatter());
        applicationLogger.addHandler(appHandler);
        applicationLogger.setLevel(Level.INFO);
        applicationLogger.setUseParentHandlers(false);
        
        errorLogger = Logger.getLogger("ErrorLogger");
        FileHandler errorHandler = new FileHandler(LOG_DIR + "error.log", true);
        errorHandler.setFormatter(new CustomFormatter());
        errorLogger.addHandler(errorHandler);
        errorLogger.setLevel(Level.WARNING);
        errorLogger.setUseParentHandlers(false);
        
        auditLogger = Logger.getLogger("AuditLogger");
        FileHandler auditHandler = new FileHandler(LOG_DIR + "audit.log", true);
        auditHandler.setFormatter(new CustomFormatter());
        auditLogger.addHandler(auditHandler);
        auditLogger.setLevel(Level.INFO);
        auditLogger.setUseParentHandlers(false);
    }
    
    public static void logInfo(String message) {
        applicationLogger.info(message);
    }
    
    public static void logInfo(String className, String methodName, String message) {
        applicationLogger.info(formatMessage(className, methodName, message));
    }
    
    public static void logError(String message) {
        errorLogger.severe(message);
    }
    
    public static void logError(String className, String methodName, String message, Exception e) {
        String fullMessage = formatMessage(className, methodName, message);
        if (e != null) {
            fullMessage += "\nException: " + e.getClass().getName() + 
                          "\nMessage: " + e.getMessage() + 
                          "\nStackTrace:\n" + getStackTraceAsString(e);
        }
        errorLogger.severe(fullMessage);
    }
    
    public static void logWarning(String message) {
        errorLogger.warning(message);
    }
    
    public static void logWarning(String className, String methodName, String message) {
        errorLogger.warning(formatMessage(className, methodName, message));
    }
    
    public static void logAudit(String username, String action, String details) {
        String message = String.format("[USER: %s] [ACTION: %s] %s", 
                                      username, action, details);
        auditLogger.info(message);
    }
    
    public static void logLogin(String username, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        logAudit(username, "LOGIN", "Login attempt: " + status);
    }
    
    public static void logLogout(String username) {
        logAudit(username, "LOGOUT", "User logged out");
    }
    
    public static void logDataChange(String username, String entityType, String entityId, String action) {
        String details = String.format("Entity: %s, ID: %s, Action: %s", 
                                      entityType, entityId, action);
        logAudit(username, "DATA_CHANGE", details);
    }
    
    public static void handleDatabaseError(String className, String methodName, Exception e, JComponent parent) {
        String errorMsg = "Terjadi kesalahan database!";
        
        logError(className, methodName, "Database error occurred", e);
        
        if (e.getMessage() != null) {
            if (e.getMessage().contains("Duplicate entry")) {
                errorMsg = "Data sudah ada di database!";
            } else if (e.getMessage().contains("foreign key constraint")) {
                errorMsg = "Tidak bisa dihapus karena masih memiliki relasi dengan data lain!";
            } else if (e.getMessage().contains("Connection")) {
                errorMsg = "Koneksi ke database gagal! Periksa koneksi Anda.";
            }
        }
        
        showErrorDialog(parent, errorMsg);
    }
    
    public static void handleFileError(String className, String methodName, Exception e, JComponent parent) {
        String errorMsg = "Terjadi kesalahan file!";
        
        logError(className, methodName, "File error occurred", e);
        
        if (e instanceof FileNotFoundException) {
            errorMsg = "File tidak ditemukan!";
        } else if (e instanceof IOException) {
            errorMsg = "Gagal membaca/menulis file!";
        }
        
        showErrorDialog(parent, errorMsg);
    }
    
    public static void handleValidationError(String className, String methodName, String message, JComponent parent) {
        logWarning(className, methodName, "Validation failed: " + message);
        showErrorDialog(parent, message);
    }
    
    public static void handleGenericError(String className, String methodName, Exception e, JComponent parent) {
        logError(className, methodName, "Unexpected error", e);
        
        String errorMsg = "Terjadi kesalahan sistem!\n";
        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            errorMsg += "Detail: " + e.getMessage();
        }
        
        showErrorDialog(parent, errorMsg);
    }
    
    private static void showErrorDialog(JComponent parent, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                parent,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }
    
    public static void showErrorDialogWithLog(JComponent parent, String userMessage, String className, String methodName, Exception e) {
        logError(className, methodName, userMessage, e);
        showErrorDialog(parent, userMessage);
    }
    
    private static String formatMessage(String className, String methodName, String message) {
        return String.format("[%s.%s] %s", className, methodName, message);
    }
    
    private static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    public static class PerformanceLogger {
        private String operation;
        private long startTime;
        
        public PerformanceLogger(String operation) {
            this.operation = operation;
            this.startTime = System.currentTimeMillis();
            logInfo("PerformanceLogger", operation, "Operation started");
        }
        
        public void end() {
            long duration = System.currentTimeMillis() - startTime;
            logInfo("PerformanceLogger", operation, 
                   String.format("Operation completed in %d ms", duration));
        }
    }
    
    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append(LocalDateTime.now().format(DATE_FORMAT));
            sb.append(" [").append(record.getLevel()).append("] ");
            sb.append(record.getMessage());
            sb.append("\n");
            
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                sb.append(sw.toString());
            }
            
            return sb.toString();
        }
    }
    
    public static void clearOldLogs(int daysToKeep) {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) return;
        
        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);
        
        File[] logFiles = logDir.listFiles((dir, name) -> name.endsWith(".log"));
        if (logFiles != null) {
            for (File file : logFiles) {
                if (file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        logInfo("LoggerUtil", "clearOldLogs", "Deleted old log file: " + file.getName());
                    }
                }
            }
        }
    }
    
    public static long getLogFileSize() {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) return 0;
        
        long totalSize = 0;
        File[] logFiles = logDir.listFiles((dir, name) -> name.endsWith(".log"));
        if (logFiles != null) {
            for (File file : logFiles) {
                totalSize += file.length();
            }
        }
        return totalSize;
    }
}