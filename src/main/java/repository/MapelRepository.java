package repository;

import config.DatabaseConnection;
import model.MataPelajaran;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MapelRepository {

    public void addMapel(MataPelajaran m) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO mapel VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, m.getIdMapel());
            stmt.setString(2, m.getNamaMapel());
            stmt.setString(3, m.getDeskripsi());
            stmt.setString(4, m.getTingkat());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateMapel(MataPelajaran m) {
        String sql = "UPDATE mapel SET nama_mapel = ?, deskripsi = ?, tingkat = ? WHERE id_mapel = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getNamaMapel());
            stmt.setString(2, m.getDeskripsi());
            stmt.setString(3, m.getTingkat());
            stmt.setString(4, m.getIdMapel());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteGuruRelations(String idMapel) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM guru_mapel WHERE id_mapel = ?")) {
            ps.setString(1, idMapel);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteKelasRelations(String idMapel) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM kelas_mapel WHERE id_mapel = ?")) {
            ps.setString(1, idMapel);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteMapel(String idMapel) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM mapel WHERE id_mapel = ?")) {
            ps.setString(1, idMapel);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<MataPelajaran> getAll() {
        List<MataPelajaran> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM mapel")) {
            while (rs.next()) {
                list.add(new MataPelajaran(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}