package repository;

import config.DatabaseConnection;
import model.Kelas;
import model.MataPelajaran;
import model.Materi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriRepository {

    private static final String STORAGE_DIR = "data/storage/materi/";

    public MateriRepository() {
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void addMateri(Materi m, File fileAsli) {
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
            
            if (fileAsli != null) {
                File destFile = new File(STORAGE_DIR + m.getFileMateri());
                try {
                    Files.copy(fileAsli.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                    return; 
                }
            }
            
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteMateri(String idMateri) {
        String filename = getFilenameById(idMateri);
        if (filename != null) {
            File f = new File(STORAGE_DIR + filename);
            if (f.exists()) f.delete();
        }

        String sql = "DELETE FROM materi WHERE id_materi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idMateri);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean downloadFile(String idMateri, File destination) {
        String filename = getFilenameById(idMateri);
        if (filename == null) return false;

        File sourceFile = new File(STORAGE_DIR + filename);
        if (!sourceFile.exists()) return false;

        try {
            destination.getParentFile().mkdirs();
            Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getFilenameById(String idMateri) {
        String sql = "SELECT file_materi FROM materi WHERE id_materi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idMateri);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("file_materi");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
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
                m.setKelas(kelas);
                m.setMapel(mapel);
                list.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Materi> getByKelas(Kelas kelas) {
        List<Materi> list = new ArrayList<>();
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