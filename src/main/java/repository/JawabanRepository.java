package repository;

import config.DatabaseConnection;
import model.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JawabanRepository {

    public void addJawaban(Jawaban j, File fileAsli) {
        String sql = "INSERT INTO jawaban (id_jawaban, id_siswa, id_tugas, id_ujian, file_jawaban, tanggal_submit, data_file) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, j.getIdJawaban());
            stmt.setString(2, j.getSiswa().getIdUser());
            stmt.setString(3, j.getTugas() != null ? j.getTugas().getIdTugas() : null);
            stmt.setString(4, j.getUjian() != null ? j.getUjian().getIdUjian() : null);
            stmt.setString(5, j.getFileJawaban());
            stmt.setString(6, j.getTanggalSubmit());
            
            if (fileAsli != null) {
                try {
                    FileInputStream fis = new FileInputStream(fileAsli);
                    stmt.setBinaryStream(7, fis, (int) fileAsli.length());
                } catch (FileNotFoundException e) {
                    stmt.setNull(7, Types.BLOB);
                }
            } else {
                stmt.setNull(7, Types.BLOB);
            }
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Method findBy... biarkan tetap sama
    public List<Jawaban> findByTugas(String idTugas) {
        return findByRef("id_tugas", idTugas);
    }

    public List<Jawaban> findByUjian(String idUjian) {
        return findByRef("id_ujian", idUjian);
    }

    private List<Jawaban> findByRef(String col, String val) {
        List<Jawaban> list = new ArrayList<>();
        String sql = "SELECT * FROM jawaban WHERE " + col + " = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, val);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Siswa s = new Siswa(rs.getString("id_siswa"), "", "", "", "", "", ""); 
                Jawaban j = new Jawaban(rs.getString("id_jawaban"), s, (Tugas)null, rs.getString("file_jawaban"));
                list.add(j);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Jawaban> getAll() { return new ArrayList<>(); }
}