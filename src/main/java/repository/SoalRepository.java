package repository;

import config.DatabaseConnection;
import model.Soal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SoalRepository {

    public void addSoal(Soal s) {
        String sql = "INSERT INTO soal VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getIdSoal());
            stmt.setString(2, s.getIdUjian());
            stmt.setString(3, s.getTipeSoal());
            stmt.setString(4, s.getPertanyaan());
            stmt.setString(5, s.getPilA());
            stmt.setString(6, s.getPilB());
            stmt.setString(7, s.getPilC());
            stmt.setString(8, s.getPilD());
            stmt.setString(9, s.getKunciJawaban());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Soal> getByUjian(String idUjian) {
        List<Soal> list = new ArrayList<>();
        String sql = "SELECT * FROM soal WHERE id_ujian = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idUjian);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                list.add(new Soal(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}