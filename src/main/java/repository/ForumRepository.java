package repository;

import config.DatabaseConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumRepository {

    // --- BAGIAN THREAD (HEADER) ---

    public void createThread(ForumThread t) {
        String sql = "INSERT INTO forum_thread VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getIdThread());
            stmt.setString(2, t.getPengirim().getIdUser());
            stmt.setString(3, t.getJudul());
            stmt.setString(4, t.getIsiUtama());
            stmt.setString(5, t.getWaktu());
            stmt.setString(6, t.getKelas().getIdKelas());
            stmt.setString(7, t.getMapel().getIdMapel());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteThread(String idThread) {
        String sql = "DELETE FROM forum_thread WHERE id_thread = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idThread);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<ForumThread> getThreadsByKelasMapel(Kelas k, MataPelajaran m) {
        List<ForumThread> list = new ArrayList<>();
        String sql = "SELECT * FROM forum_thread WHERE id_kelas = ? AND id_mapel = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, k.getIdKelas());
            stmt.setString(2, m.getIdMapel());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                User u = new UserRepository().findById(rs.getString("id_user"));
                list.add(new ForumThread(
                    rs.getString("id_thread"), u,
                    rs.getString("judul"), rs.getString("isi_utama"),
                    rs.getString("waktu"), k, m
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- BAGIAN REPLY (DETAIL) ---

    public void addReply(ForumReply r) {
        String sql = "INSERT INTO forum_reply VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getIdReply());
            stmt.setString(2, r.getIdThread());
            stmt.setString(3, r.getPenjawab().getIdUser());
            stmt.setString(4, r.getIsiReply());
            stmt.setString(5, r.getWaktu());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<ForumReply> getRepliesByThread(String idThread) {
        List<ForumReply> list = new ArrayList<>();
        String sql = "SELECT * FROM forum_reply WHERE id_thread = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idThread);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                User u = new UserRepository().findById(rs.getString("id_user"));
                list.add(new ForumReply(
                    rs.getString("id_reply"),
                    rs.getString("id_thread"),
                    u,
                    rs.getString("isi_reply"),
                    rs.getString("waktu")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public int countReplies(String idThread) {
        String sql = "SELECT count(*) FROM forum_reply WHERE id_thread = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idThread);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}