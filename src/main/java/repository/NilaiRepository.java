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

    public void updateNilai(Nilai n) {
        String sql = "UPDATE nilai SET nilai_angka = ?, keterangan = ? WHERE id_nilai = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, n.getNilaiAngka());
            stmt.setString(2, n.getKeterangan());
            stmt.setString(3, n.getIdNilai());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Nilai mapRowToNilai(ResultSet rs) throws SQLException {
        Siswa s = new Siswa(rs.getString("id_siswa"), "","","","","","");
        
        String idTugas = rs.getString("id_tugas");
        String idUjian = rs.getString("id_ujian");

        Nilai n = new Nilai(rs.getString("id_nilai"), rs.getInt("nilai_angka"), rs.getString("keterangan"));
        n.setSiswa(s);
        
        if (idTugas != null) {
            Tugas t = new Tugas(idTugas, "", "", null);
            n.setTugas(t);
        }
        if (idUjian != null) {
            Ujian u = new Ujian(idUjian, "", null, 0); 
            n.setUjian(u);
        }
        return n;
    }

    public List<Nilai> getAll() {
        List<Nilai> list = new ArrayList<>();
        String sql = "SELECT * FROM nilai";
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while(rs.next()) {
                list.add(mapRowToNilai(rs));
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
                list.add(mapRowToNilai(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}