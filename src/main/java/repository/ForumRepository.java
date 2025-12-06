package repository;

import config.DatabaseConnection;
import model.ForumDiskusi;
import model.Kelas;
import model.MataPelajaran;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumRepository {

    public void addPesan(ForumDiskusi f) {
        String sql = "INSERT INTO forum VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getIdPesan());
            stmt.setString(2, f.getPengirim().getIdUser());
            stmt.setString(3, f.getJudul());
            stmt.setString(4, f.getIsiPesan());
            stmt.setString(5, f.getWaktu());
            stmt.setString(6, f.getKelas().getIdKelas());
            stmt.setString(7, f.getMapel().getIdMapel());
            stmt.setString(8, f.getParentId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deletePesan(String idPesan) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM forum WHERE parent_id = ?")) {
                ps.setString(1, idPesan);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM forum WHERE id_pesan = ?")) {
                ps.setString(1, idPesan);
                ps.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<ForumDiskusi> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        List<ForumDiskusi> list = new ArrayList<>();
        String sql = "SELECT * FROM forum WHERE id_mapel = ? AND id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mapel.getIdMapel());
            stmt.setString(2, kelas.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs, mapel, kelas));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<ForumDiskusi> getReplies(String threadId) {
        List<ForumDiskusi> list = new ArrayList<>();
        String sql = "SELECT * FROM forum WHERE parent_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, threadId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs, null, null));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public List<ForumDiskusi> getAll() {
        List<ForumDiskusi> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM forum")) {
            while (rs.next()) list.add(mapRow(rs, null, null));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private ForumDiskusi mapRow(ResultSet rs, MataPelajaran m, Kelas k) throws SQLException {
        User u = new UserRepository().findById(rs.getString("id_user")); 
        
        
        return new ForumDiskusi(
            rs.getString("id_pesan"),
            u, 
            rs.getString("judul"),
            rs.getString("isi_pesan"),
            rs.getString("waktu"),
            k, 
            m,
            rs.getString("parent_id")
        );
    }
}