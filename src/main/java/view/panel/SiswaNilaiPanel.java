package view.panel;

import model.Nilai;
import model.Siswa;
import repository.NilaiRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SiswaNilaiPanel extends JPanel {
    
    private final Siswa siswa;
    private final NilaiRepository nilaiRepo;
    private final DefaultTableModel model;
    
    public SiswaNilaiPanel(Siswa s, NilaiRepository nr) {
        this.siswa = s;
        this.nilaiRepo = nr;
        
        setLayout(new BorderLayout());
        
        model = new DefaultTableModel(new String[]{"Tugas/Ujian", "Nilai", "Ket"}, 0);
        JTable table = new JTable(model);
        
        refreshTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    public void refreshTable() {
        model.setRowCount(0);
        for(Nilai n : nilaiRepo.findBySiswa(siswa.getIdUser())) {
            String sumber = n.getTugas() != null ? "Tugas: " + n.getTugas().getJudul() : 
                            (n.getUjian() != null ? "Ujian: " + n.getUjian().getNamaUjian() : "Unknown");
            model.addRow(new Object[]{sumber, n.getNilaiAngka(), n.getKeterangan()});
        }
    }
}