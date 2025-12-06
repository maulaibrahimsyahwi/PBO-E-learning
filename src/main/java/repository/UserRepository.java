package repository;

import config.DatabaseConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public void addUser(User u) {
        String sql = "INSERT INTO users (id_user, username, password, nama_lengkap, email, role, nip, spesialisasi, nis, angkatan, id_kelas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getIdUser());
            stmt.setString(2, u.getUsername());
            stmt.setString(3, u.getPassword());
            stmt.setString(4, u.getNamaLengkap());
            stmt.setString(5, u.getEmail());

            if (u instanceof Admin) {
                stmt.setString(6, "ADMIN");
                stmt.setNull(7, Types.VARCHAR); stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.VARCHAR); stmt.setNull(10, Types.VARCHAR); stmt.setNull(11, Types.VARCHAR);
            } else if (u instanceof Guru g) {
                stmt.setString(6, "GURU");
                stmt.setString(7, g.getNip());
                stmt.setString(8, g.getSpesialisasi());
                stmt.setNull(9, Types.VARCHAR); stmt.setNull(10, Types.VARCHAR); stmt.setNull(11, Types.VARCHAR);
                saveGuruRelations(g); 
            } else if (u instanceof Siswa s) {
                stmt.setString(6, "SISWA");
                stmt.setNull(7, Types.VARCHAR); stmt.setNull(8, Types.VARCHAR);
                stmt.setString(9, s.getNis());
                stmt.setString(10, s.getAngkatan());
                stmt.setString(11, s.getKelas() != null ? s.getKelas().getIdKelas() : null);
            }
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateSiswa(Siswa s) {
        String sql = "UPDATE users SET username = ?, nama_lengkap = ?, email = ?, nis = ?, angkatan = ?, id_kelas = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getUsername());
            stmt.setString(2, s.getNamaLengkap());
            stmt.setString(3, s.getEmail());
            stmt.setString(4, s.getNis());
            stmt.setString(5, s.getAngkatan());
            
            if (s.getKelas() != null) {
                stmt.setString(6, s.getKelas().getIdKelas());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            
            stmt.setString(7, s.getIdUser());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- BARU: Method Update Guru ---
    public void updateGuru(Guru g) {
        String sql = "UPDATE users SET username = ?, nama_lengkap = ?, email = ?, nip = ?, spesialisasi = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, g.getUsername());
            stmt.setString(2, g.getNamaLengkap());
            stmt.setString(3, g.getEmail());
            stmt.setString(4, g.getNip());
            stmt.setString(5, g.getSpesialisasi());
            stmt.setString(6, g.getIdUser());
            stmt.executeUpdate();
            
            saveGuruRelations(g); // Update juga relasi mapel & kelas
        } catch (SQLException e) { e.printStackTrace(); }
    }
    // -------------------------------

    public void deleteUser(String idUser) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM guru_mapel WHERE id_guru = ?");
                 PreparedStatement ps2 = conn.prepareStatement("DELETE FROM guru_kelas WHERE id_guru = ?")) {
                ps1.setString(1, idUser); ps1.executeUpdate();
                ps2.setString(1, idUser); ps2.executeUpdate();
            }
            try (PreparedStatement ps3 = conn.prepareStatement("DELETE FROM users WHERE id_user = ?")) {
                ps3.setString(1, idUser);
                ps3.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void saveToFile() {}

    private void saveGuruRelations(Guru g) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.createStatement().executeUpdate("DELETE FROM guru_mapel WHERE id_guru='" + g.getIdUser() + "'");
            conn.createStatement().executeUpdate("DELETE FROM guru_kelas WHERE id_guru='" + g.getIdUser() + "'");
            
            PreparedStatement psMapel = conn.prepareStatement("INSERT INTO guru_mapel VALUES (?, ?)");
            for (MataPelajaran m : g.getMapelDiampu()) {
                psMapel.setString(1, g.getIdUser());
                psMapel.setString(2, m.getIdMapel());
                psMapel.executeUpdate();
            }
            
            PreparedStatement psKelas = conn.prepareStatement("INSERT INTO guru_kelas VALUES (?, ?)");
            for (Kelas k : g.getDaftarKelas()) {
                psKelas.setString(1, g.getIdUser());
                psKelas.setString(2, k.getIdKelas());
                psKelas.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRowToUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User findById(String idUser) {
        String sql = "SELECT * FROM users WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRowToUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Siswa> getAllSiswa() {
        List<Siswa> list = new ArrayList<>();
        String sql = "SELECT u.*, k.nama_kelas, k.tingkat FROM users u LEFT JOIN kelas k ON u.id_kelas = k.id_kelas WHERE u.role = 'SISWA'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Siswa s = new Siswa(rs.getString("id_user"), rs.getString("username"), rs.getString("password"),
                                    rs.getString("nama_lengkap"), rs.getString("email"), rs.getString("nis"), rs.getString("angkatan"));
                String idKelas = rs.getString("id_kelas");
                if (idKelas != null) {
                    Kelas k = new Kelas(idKelas, rs.getString("nama_kelas"), rs.getString("tingkat"));
                    s.setKelas(k);
                }
                list.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                User u = mapRowToUser(rs);
                if(u != null) list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    private User mapRowToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        String id = rs.getString("id_user");
        if ("ADMIN".equals(role)) {
            return new Admin(id, rs.getString("username"), rs.getString("password"), rs.getString("nama_lengkap"), rs.getString("email"));
        } else if ("GURU".equals(role)) {
            Guru g = new Guru(id, rs.getString("username"), rs.getString("password"), rs.getString("nama_lengkap"), rs.getString("email"), rs.getString("nip"), rs.getString("spesialisasi"));
            loadGuruRelations(g); return g;
        } else if ("SISWA".equals(role)) {
            Siswa s = new Siswa(id, rs.getString("username"), rs.getString("password"), rs.getString("nama_lengkap"), rs.getString("email"), rs.getString("nis"), rs.getString("angkatan"));
            String idKelas = rs.getString("id_kelas");
            if (idKelas != null) s.setKelas(new KelasRepository().findById(idKelas));
            return s;
        }
        return null;
    }

    private void loadGuruRelations(Guru g) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs1 = conn.createStatement().executeQuery(
                "SELECT m.* FROM mapel m JOIN guru_mapel gm ON m.id_mapel = gm.id_mapel WHERE gm.id_guru = '" + g.getIdUser() + "'");
            while(rs1.next()) g.tambahMapel(new MataPelajaran(rs1.getString("id_mapel"), rs1.getString("nama_mapel"), rs1.getString("deskripsi"), rs1.getString("tingkat")));
            ResultSet rs2 = conn.createStatement().executeQuery(
                "SELECT k.* FROM kelas k JOIN guru_kelas gk ON k.id_kelas = gk.id_kelas WHERE gk.id_guru = '" + g.getIdUser() + "'");
            while(rs2.next()) g.tambahKelas(new Kelas(rs2.getString("id_kelas"), rs2.getString("nama_kelas"), rs2.getString("tingkat")));
        } catch (SQLException e) { e.printStackTrace(); }
    }
}