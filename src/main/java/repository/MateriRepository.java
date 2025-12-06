package repository;

import config.DatabaseConnection;
import model.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriRepository {

    public void addMateri(Materi m, File fileAsli) {
        String sql = "INSERT INTO materi (id_materi, judul, deskripsi, file_materi, id_guru, id_kelas, id_mapel, data_file) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getIdMateri());
            stmt.setString(2, m.getJudul());
            stmt.setString(3, m.getDeskripsi());
            stmt.setString(4, m.getFileMateri());
            stmt.setString(5, m.getGuru().getIdUser());
            stmt.setString(6, m.getKelas().getIdKelas());
            stmt.setString(7, m.getMapel().getIdMapel());
            
            if (fileAsli != null) {
                try {
                    FileInputStream fis = new FileInputStream(fileAsli);
                    stmt.setBinaryStream(8, fis, (int) fileAsli.length());
                } catch (FileNotFoundException e) {
                    stmt.setNull(8, Types.BLOB);
                }
            } else {
                stmt.setNull(8, Types.BLOB);
            }
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteMateri(String idMateri) {
        String sql = "DELETE FROM materi WHERE id_materi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idMateri);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- BARU: Method Download File dari Database ---
    public boolean downloadFile(String idMateri, File destination) {
        String sql = "SELECT data_file FROM materi WHERE id_materi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idMateri);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                try (InputStream is = rs.getBinaryStream("data_file")) {
                    if (is == null) return false; // Tidak ada file di DB
                    
                    // Buat folder jika belum ada
                    destination.getParentFile().mkdirs();
                    
                    try (FileOutputStream fos = new FileOutputStream(destination)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    // ------------------------------------------------

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
                m.setKelas(kelas);
                m.setMapel(mapel);
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Materi> getByKelas(Kelas kelas) {
        List<Materi> list = new ArrayList<>();
        // Menggunakan JOIN agar objek Mapel terisi (Mencegah NullPointerException)
        String sql = "SELECT m.*, mp.nama_mapel, mp.deskripsi AS mp_desk, mp.tingkat " + 
                     "FROM materi m " +
                     "JOIN mapel mp ON m.id_mapel = mp.id_mapel " +
                     "WHERE m.id_kelas = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kelas.getIdKelas());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Materi m = new Materi(rs.getString("id_materi"), rs.getString("judul"), rs.getString("deskripsi"), rs.getString("file_materi"));
                m.setKelas(kelas);
                
                MataPelajaran mp = new MataPelajaran(
                    rs.getString("id_mapel"),
                    rs.getString("nama_mapel"),
                    rs.getString("mp_desk"),
                    rs.getString("tingkat")
                );
                m.setMapel(mp);
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}