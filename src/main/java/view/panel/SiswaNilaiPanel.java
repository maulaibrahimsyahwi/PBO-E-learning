package view.panel;

import model.Nilai;
import model.Siswa;
import model.Tugas;
import model.Ujian;
import repository.NilaiRepository;
import repository.TugasRepository;
import repository.UjianRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;

public class SiswaNilaiPanel extends JPanel {
    
    private final Siswa siswa;
    private final NilaiRepository nilaiRepo;
    private final TugasRepository tugasRepo;
    private final UjianRepository ujianRepo;
    private final DefaultTableModel model;
    
    public SiswaNilaiPanel(Siswa s, NilaiRepository nr, TugasRepository tr, UjianRepository ur) {
        this.siswa = s;
        this.nilaiRepo = nr;
        this.tugasRepo = tr;
        this.ujianRepo = ur;
        
        setLayout(new BorderLayout());
        
        model = new DefaultTableModel(new String[]{"Tugas/Ujian", "Nilai", "Ket"}, 0);
        JTable table = new JTable(model);
        
        refreshTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    public void refreshTable() {
        model.setRowCount(0);
        
        for(Nilai n : nilaiRepo.findBySiswa(siswa.getIdUser())) {
            String sumber = "Unknown";
            
            if (n.getTugas() != null) {
                Optional<Tugas> tOpt = tugasRepo.getAll().stream()
                    .filter(t -> t.getIdTugas().equals(n.getTugas().getIdTugas()))
                    .findFirst();
                if (tOpt.isPresent()) {
                    sumber = "Tugas: " + tOpt.get().getJudul();
                }
            } else if (n.getUjian() != null) {
                Optional<Ujian> uOpt = ujianRepo.getAll().stream()
                    .filter(u -> u.getIdUjian().equals(n.getUjian().getIdUjian()))
                    .findFirst();
                if (uOpt.isPresent()) {
                    sumber = "Ujian: " + uOpt.get().getNamaUjian();
                }
            }
            
            model.addRow(new Object[]{sumber, n.getNilaiAngka(), n.getKeterangan()});
        }
    }
}