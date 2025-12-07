package repository;

import config.DatabaseConnection;
import model.Soal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SoalRepository {

    public void addSoal(Soal s) {
        String sql = "INSERT INTO soal (id_soal, id_ujian, tipe_soal, pertanyaan, pil_a, pil_b, pil_c, pil_d, kunci_jawaban, gambar) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            stmt.setString(10, s.getGambar());
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
                list.add(new Soal(
                    rs.getString("id_soal"), 
                    rs.getString("id_ujian"), 
                    rs.getString("tipe_soal"), 
                    rs.getString("pertanyaan"),
                    rs.getString("pil_a"), 
                    rs.getString("pil_b"), 
                    rs.getString("pil_c"), 
                    rs.getString("pil_d"), 
                    rs.getString("kunci_jawaban"),
                    rs.getString("gambar")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}