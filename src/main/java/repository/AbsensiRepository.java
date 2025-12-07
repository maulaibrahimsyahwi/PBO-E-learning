package repository;

import config.DatabaseConnection;
import model.Absensi;
import model.Kelas;
import model.Siswa;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbsensiRepository {

    public void addAbsensi(Absensi a) {
        String sql = "INSERT INTO absensi VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getIdAbsensi());
            stmt.setString(2, a.getSiswa().getIdUser());
            stmt.setString(3, a.getKelas().getIdKelas());
            stmt.setDate(4, Date.valueOf(a.getTanggal()));
            stmt.setString(5, a.getWaktu());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean sudahAbsen(Siswa s, LocalDate tgl) {
        String sql = "SELECT count(*) FROM absensi WHERE id_siswa = ? AND tanggal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getIdUser());
            stmt.setDate(2, Date.valueOf(tgl));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Absensi> getByKelasAndTanggal(Kelas k, LocalDate tgl) {
        List<Absensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE id_kelas = ? AND tanggal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, k.getIdKelas());
            stmt.setDate(2, Date.valueOf(tgl));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Siswa s = new Siswa(rs.getString("id_siswa"), "", "", "", "", "", "");
                Absensi a = new Absensi(
                    rs.getString("id_absensi"),
                    s,
                    k,
                    rs.getDate("tanggal").toLocalDate(),
                    rs.getString("waktu")
                );
                list.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}