package repository;

import config.DatabaseConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriRepository {

    public void addMateri(Materi m) {
        String sql = "INSERT INTO materi (id_materi, judul, deskripsi, file_materi, id_guru, id_kelas, id_mapel) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getIdMateri());
            stmt.setString(2, m.getJudul());
            stmt.setString(3, m.getDeskripsi());
            stmt.setString(4, m.getFileMateri());
            stmt.setString(5, m.getGuru().getIdUser());
            stmt.setString(6, m.getKelas().getIdKelas());
            stmt.setString(7, m.getMapel().getIdMapel());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Materi> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        List<Materi> list = new ArrayList<>();
        String sql = "SELECT * FROM materi WHERE id_mapel = ? AND id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mapel.getIdMapel());
            stmt.setString(2, kelas.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Materi m = new Materi(rs.getString("id_materi"), rs.getString("judul"), rs.getString("deskripsi"), rs.getString("file_materi"));
                User u = new UserRepository().findByUsername(rs.getString("id_guru")); 
                if(u instanceof Guru) m.setGuru((Guru)u);
                m.setKelas(kelas);
                m.setMapel(mapel);
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Materi> getByKelas(Kelas kelas) {
        List<Materi> list = new ArrayList<>();
        String sql = "SELECT * FROM materi WHERE id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kelas.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Materi m = new Materi(rs.getString("id_materi"), rs.getString("judul"), rs.getString("deskripsi"), rs.getString("file_materi"));
                m.setKelas(kelas);
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}