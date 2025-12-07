package repository;

import config.DatabaseConnection;
import model.UjianProgress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UjianProgressRepository {

    public void saveOrUpdate(UjianProgress p) {
        if (exists(p.getIdSiswa(), p.getIdUjian())) {
            update(p);
        } else {
            insert(p);
        }
    }

    public UjianProgress getProgress(String idSiswa, String idUjian) {
        String sql = "SELECT * FROM ujian_progress WHERE id_siswa = ? AND id_ujian = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idSiswa);
            stmt.setString(2, idUjian);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UjianProgress(
                    rs.getString("id_progress"),
                    rs.getString("id_siswa"),
                    rs.getString("id_ujian"),
                    rs.getInt("current_index"),
                    rs.getInt("sisa_waktu"),
                    rs.getInt("violation_count"),
                    rs.getString("jawaban_sementara")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteProgress(String idSiswa, String idUjian) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM ujian_progress WHERE id_siswa = ? AND id_ujian = ?")) {
            stmt.setString(1, idSiswa);
            stmt.setString(2, idUjian);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean exists(String idSiswa, String idUjian) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM ujian_progress WHERE id_siswa = ? AND id_ujian = ?")) {
            stmt.setString(1, idSiswa);
            stmt.setString(2, idUjian);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    private void insert(UjianProgress p) {
        String sql = "INSERT INTO ujian_progress VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getIdProgress());
            stmt.setString(2, p.getIdSiswa());
            stmt.setString(3, p.getIdUjian());
            stmt.setInt(4, p.getCurrentIndex());
            stmt.setInt(5, p.getSisaWaktu());
            stmt.setString(6, p.getJawabanString());
            stmt.setInt(7, p.getViolationCount());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(UjianProgress p) {
        String sql = "UPDATE ujian_progress SET current_index = ?, sisa_waktu = ?, jawaban_sementara = ?, violation_count = ? WHERE id_siswa = ? AND id_ujian = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getCurrentIndex());
            stmt.setInt(2, p.getSisaWaktu());
            stmt.setString(3, p.getJawabanString());
            stmt.setInt(4, p.getViolationCount());
            stmt.setString(5, p.getIdSiswa());
            stmt.setString(6, p.getIdUjian());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}