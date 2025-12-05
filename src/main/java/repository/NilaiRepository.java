package repository;

import config.DatabaseConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NilaiRepository {

    public void addNilai(Nilai n) {
        String sql = "INSERT INTO nilai VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, n.getIdNilai());
            stmt.setString(2, n.getSiswa().getIdUser());
            stmt.setString(3, n.getTugas() != null ? n.getTugas().getIdTugas() : null);
            stmt.setString(4, n.getUjian() != null ? n.getUjian().getIdUjian() : null);
            stmt.setInt(5, n.getNilaiAngka());
            stmt.setString(6, n.getKeterangan());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Nilai> getAll() {
        List<Nilai> list = new ArrayList<>();
        String sql = "SELECT * FROM nilai";
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while(rs.next()) {
                Siswa s = new Siswa(rs.getString("id_siswa"), "","","","","","");
                Nilai n = new Nilai(rs.getString("id_nilai"), s, (Tugas)null, rs.getInt("nilai_angka"), rs.getString("keterangan"));
                list.add(n);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Nilai> findBySiswa(String idSiswa) {
        List<Nilai> list = new ArrayList<>();
        String sql = "SELECT * FROM nilai WHERE id_siswa = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idSiswa);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Siswa s = new Siswa(rs.getString("id_siswa"), "","","","","","");
                Nilai n = new Nilai(rs.getString("id_nilai"), s, (Tugas)null, rs.getInt("nilai_angka"), rs.getString("keterangan"));
                list.add(n);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}