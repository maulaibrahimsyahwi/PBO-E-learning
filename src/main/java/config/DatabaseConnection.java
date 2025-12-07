package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/pbo_elearning");
        config.setUsername("root");
        config.setPassword("");
        
        // Konfigurasi Pool
        config.setMaximumPoolSize(10); // Maksimal 10 koneksi aktif
        config.setMinimumIdle(5);      // Minimal 5 koneksi standby
        config.setIdleTimeout(30000);  // Waktu tunggu sebelum koneksi idle ditutup
        config.setMaxLifetime(1800000); // Umur maksimal koneksi

        // Optimasi Performa
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}