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
                // Mapel dan Kelas null karena kita hanya butuh isi reply
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

    // Perbaikan di sini: Menambahkan rs.getString("waktu") dan melengkapi logic User/Kelas/Mapel
    private ForumDiskusi mapRow(ResultSet rs, MataPelajaran m, Kelas k) throws SQLException {
        // Ambil user
        User u = new UserRepository().findByUsername(rs.getString("id_user")); 
        if (u == null) {
            // Fallback jika findByUsername gagal (misal data user terhapus), pakai id_user sementara
            // Atau biarkan null, nanti di View ditangani sebagai "User Terhapus"
        }
        
        // Jika m atau k null (misal dari getAll/getReplies), coba fetch dari DB agar objek lengkap
        if (m == null) {
            // Logika sederhana: ambil ID mapel, tapi untuk efisiensi kita biarkan null jika tidak krusial
            // Jika mau lengkap: m = new MapelRepository().findById(rs.getString("id_mapel"));
        }
        
        return new ForumDiskusi(
            rs.getString("id_pesan"),
            u,
            rs.getString("judul"),
            rs.getString("isi_pesan"),
            rs.getString("waktu"), // INI YANG DITAMBAHKAN
            k, 
            m,
            rs.getString("parent_id")
        );
    }
}