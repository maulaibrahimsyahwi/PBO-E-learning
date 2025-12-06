package repository;

import config.DatabaseConnection;
import model.Absensi;
import model.Siswa;
import java.sql.*;
import java.time.LocalDate;

public class AbsensiRepository {

    public void addAbsensi(Absensi a) {
        String sql = "INSERT INTO absensi VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getIdAbsensi());
            stmt.setString(2, a.getSiswa().getIdUser());
            stmt.setString(3, a.getKelas().getIdKelas());
            stmt.setDate(4, Date.valueOf(a.getTanggal()));
            stmt.setString(5, a.getWaktu());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean sudahAbsen(Siswa s, LocalDate tgl) {
        String sql = "SELECT count(*) FROM absensi WHERE id_siswa = ? AND tanggal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getIdUser());
            stmt.setDate(2, Date.valueOf(tgl));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}