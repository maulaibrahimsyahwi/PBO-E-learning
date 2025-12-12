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

    public void deleteTugas(String idTugas) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM jawaban WHERE id_tugas = '" + idTugas + "'");
                st.executeUpdate("DELETE FROM nilai WHERE id_tugas = '" + idTugas + "'");
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tugas WHERE id_tugas = ?")) {
                ps.setString(1, idTugas);
                ps.executeUpdate();
            }
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
        List<Tugas> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM tugas")) {
            while(rs.next()) {
                list.add(new Tugas(rs.getString("id_tugas"), rs.getString("judul"), rs.getString("deskripsi"), rs.getDate("deadline").toLocalDate()));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Tugas> getByKelas(Kelas k) {
        List<Tugas> list = new ArrayList<>();
        String sql = "SELECT t.*, mp.nama_mapel, mp.deskripsi AS mp_desk, mp.tingkat " + 
                     "FROM tugas t " +
                     "JOIN mapel mp ON t.id_mapel = mp.id_mapel " +
                     "WHERE t.id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, k.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Tugas t = new Tugas(rs.getString("id_tugas"), rs.getString("judul"), rs.getString("deskripsi"), rs.getDate("deadline").toLocalDate());
                t.setKelas(k);
                MataPelajaran mp = new MataPelajaran(
                    rs.getString("id_mapel"),
                    rs.getString("nama_mapel"),
                    rs.getString("mp_desk"),
                    rs.getString("tingkat")
                );
                t.setMapel(mp);
                list.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}