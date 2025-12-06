package repository;

import config.DatabaseConnection;
import model.Kelas;
import model.MataPelajaran;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KelasRepository {

    public void addKelas(Kelas k) {
        String sql = "INSERT INTO kelas (id_kelas, nama_kelas, tingkat) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, k.getIdKelas());
            stmt.setString(2, k.getNamaKelas());
            stmt.setString(3, k.getTingkat());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateKelas(Kelas k) {
        String sql = "UPDATE kelas SET nama_kelas = ?, tingkat = ? WHERE id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, k.getNamaKelas());
            stmt.setString(2, k.getTingkat());
            stmt.setString(3, k.getIdKelas());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- BARU: Method Delete Kelas ---
    public void deleteKelas(String idKelas) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Hapus relasi di kelas_mapel dulu
            try (PreparedStatement psRel = conn.prepareStatement("DELETE FROM kelas_mapel WHERE id_kelas = ?")) {
                psRel.setString(1, idKelas);
                psRel.executeUpdate();
            }
            // Hapus data kelas utama
            try (PreparedStatement psMain = conn.prepareStatement("DELETE FROM kelas WHERE id_kelas = ?")) {
                psMain.setString(1, idKelas);
                psMain.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    // --------------------------------

    public List<Kelas> getAll() {
        List<Kelas> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM kelas")) {
            while (rs.next()) {
                Kelas k = new Kelas(rs.getString("id_kelas"), rs.getString("nama_kelas"), rs.getString("tingkat"));
                loadMapelKelas(k); 
                list.add(k);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Kelas findById(String id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM kelas WHERE id_kelas = ?")) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Kelas k = new Kelas(rs.getString("id_kelas"), rs.getString("nama_kelas"), rs.getString("tingkat"));
                loadMapelKelas(k);
                return k;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private void loadMapelKelas(Kelas k) {
        String sql = "SELECT m.* FROM mapel m JOIN kelas_mapel km ON m.id_mapel = km.id_mapel WHERE km.id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, k.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                k.tambahMapel(new MataPelajaran(rs.getString("id_mapel"), rs.getString("nama_mapel"), rs.getString("deskripsi"), rs.getString("tingkat")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public void addMapelToKelas(String idKelas, String idMapel) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO kelas_mapel VALUES (?, ?)")) {
            stmt.setString(1, idKelas);
            stmt.setString(2, idMapel);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}