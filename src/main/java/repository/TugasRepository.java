package repository;

import config.DatabaseConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TugasRepository {

    public void addTugas(Tugas t) {
        String sql = "INSERT INTO tugas VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getIdTugas());
            stmt.setString(2, t.getJudul());
            stmt.setString(3, t.getDeskripsi());
            stmt.setDate(4, Date.valueOf(t.getDeadline()));
            stmt.setString(5, t.getGuru().getIdUser());
            stmt.setString(6, t.getKelas().getIdKelas());
            stmt.setString(7, t.getMapel().getIdMapel());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Tugas> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        List<Tugas> list = new ArrayList<>();
        String sql = "SELECT * FROM tugas WHERE id_mapel = ? AND id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mapel.getIdMapel());
            stmt.setString(2, kelas.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Tugas t = new Tugas(rs.getString("id_tugas"), rs.getString("judul"), rs.getString("deskripsi"), rs.getDate("deadline").toLocalDate());
                t.setKelas(kelas);
                t.setMapel(mapel);
                list.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Tugas> getAll() {
        return new ArrayList<>();
    }
    
    public List<Tugas> getByKelas(Kelas k) {
        List<Tugas> list = new ArrayList<>();
        String sql = "SELECT * FROM tugas WHERE id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, k.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Tugas t = new Tugas(rs.getString("id_tugas"), rs.getString("judul"), rs.getString("deskripsi"), rs.getDate("deadline").toLocalDate());
                t.setKelas(k);
                list.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}