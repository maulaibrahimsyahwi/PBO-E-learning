package repository;

import config.DatabaseConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UjianRepository {

    public void addUjian(Ujian u) {
        String sql = "INSERT INTO ujian VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getIdUjian());
            stmt.setString(2, u.getNamaUjian());
            stmt.setString(3, u.getTipeUjian());
            stmt.setDate(4, Date.valueOf(u.getTanggal()));
            stmt.setInt(5, u.getDurasiTotal());
            stmt.setInt(6, u.getWaktuPerSoal());
            stmt.setInt(7, u.getMaxSoal());
            stmt.setString(8, u.getGuru().getIdUser());
            stmt.setString(9, u.getKelas().getIdKelas());
            stmt.setString(10, u.getMapel().getIdMapel());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteUjian(String idUjian) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM soal WHERE id_ujian = '" + idUjian + "'");
                st.executeUpdate("DELETE FROM jawaban WHERE id_ujian = '" + idUjian + "'");
                st.executeUpdate("DELETE FROM nilai WHERE id_ujian = '" + idUjian + "'");
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ujian WHERE id_ujian = ?")) {
                ps.setString(1, idUjian);
                ps.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Ujian> getByMapelAndKelas(MataPelajaran mapel, Kelas kelas) {
        List<Ujian> list = new ArrayList<>();
        String sql = "SELECT * FROM ujian WHERE id_mapel = ? AND id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mapel.getIdMapel());
            stmt.setString(2, kelas.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Ujian u = new Ujian(rs.getString("id_ujian"), rs.getString("nama_ujian"), rs.getString("tipe_ujian"),
                        rs.getDate("tanggal").toLocalDate(), rs.getInt("durasi_total"), rs.getInt("waktu_per_soal"), rs.getInt("max_soal"));
                u.setKelas(kelas); u.setMapel(mapel);
                list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Ujian> getAll() { 
        List<Ujian> list = new ArrayList<>();
        String sql = "SELECT * FROM ujian";
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                 Ujian u = new Ujian(rs.getString("id_ujian"), rs.getString("nama_ujian"), rs.getString("tipe_ujian"),
                        rs.getDate("tanggal").toLocalDate(), rs.getInt("durasi_total"), rs.getInt("waktu_per_soal"), rs.getInt("max_soal"));
                 list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list; 
    } 
    
    public List<Ujian> getByKelas(Kelas k) { 
        List<Ujian> list = new ArrayList<>();
        String sql = "SELECT u.*, mp.nama_mapel, mp.deskripsi AS mp_desk, mp.tingkat " + 
                     "FROM ujian u " + 
                     "JOIN mapel mp ON u.id_mapel = mp.id_mapel " + 
                     "WHERE u.id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, k.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Ujian u = new Ujian(rs.getString("id_ujian"), rs.getString("nama_ujian"), rs.getString("tipe_ujian"),
                        rs.getDate("tanggal").toLocalDate(), rs.getInt("durasi_total"), rs.getInt("waktu_per_soal"), rs.getInt("max_soal"));
                u.setKelas(k);
                MataPelajaran mp = new MataPelajaran(
                    rs.getString("id_mapel"),
                    rs.getString("nama_mapel"),
                    rs.getString("mp_desk"),
                    rs.getString("tingkat")
                );
                u.setMapel(mp);
                list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list; 
    }
}