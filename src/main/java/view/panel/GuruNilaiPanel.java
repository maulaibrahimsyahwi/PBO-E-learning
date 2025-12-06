package view.panel;

import model.Jawaban;
import model.Kelas;
import model.MataPelajaran;
import model.Nilai;
import model.Tugas;
import model.Ujian;
import repository.JawabanRepository;
import repository.NilaiRepository;
import repository.TugasRepository;
import repository.UjianRepository;
import utils.IdUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;

public class GuruNilaiPanel extends JPanel {
    
    private final Kelas kelas;
    private final MataPelajaran mapel;
    private final TugasRepository tugasRepo;
    private final UjianRepository ujianRepo;
    private final JawabanRepository jawabanRepo;
    private final NilaiRepository nilaiRepo;
    private final DefaultTableModel model;
    private final JTable table;
    
    public GuruNilaiPanel(Kelas k, MataPelajaran m, TugasRepository tr, UjianRepository ur, JawabanRepository jr, NilaiRepository nr) {
        this.kelas = k;
        this.mapel = m;
        this.tugasRepo = tr;
        this.ujianRepo = ur;
        this.jawabanRepo = jr;
        this.nilaiRepo = nr;
        
        setLayout(new BorderLayout());
        
        String[] columns = {"ID Jawaban", "Tipe", "Judul Soal", "Siswa", "File Jawaban", "Nilai"};
        model = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int row, int column) { return false; } };
        table = new JTable(model);
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        refreshTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton btnNilai = new JButton("Beri Nilai Manual");
        btnNilai.setPreferredSize(new Dimension(150, 35));
        
        btnPanel.add(btnNilai);
        
        btnNilai.addActionListener(e -> beriNilaiManual());
        
        return btnPanel;
    }
    
    public void refreshTable() {
        model.setRowCount(0);
        
        for(Tugas t : tugasRepo.getByMapelAndKelas(mapel, kelas)) {
            for(Jawaban j : jawabanRepo.findByTugas(t.getIdTugas())) {
                String nilaiStr = "Belum Dinilai";
                Optional<Nilai> nOpt = nilaiRepo.getAll().stream()
                    .filter(n -> n.getTugas() != null && n.getTugas().getIdTugas().equals(t.getIdTugas()) 
                                 && n.getSiswa().getIdUser().equals(j.getSiswa().getIdUser()))
                    .findFirst();
                
                if (nOpt.isPresent()) {
                    nilaiStr = String.valueOf(nOpt.get().getNilaiAngka());
                }
                model.addRow(new Object[]{j.getIdJawaban(), "Tugas", t.getJudul(), j.getSiswa().getNamaLengkap(), j.getFileJawaban(), nilaiStr});
            }
        }
        
        for(Ujian u : ujianRepo.getByMapelAndKelas(mapel, kelas)) {
            for(Jawaban j : jawabanRepo.findByUjian(u.getIdUjian())) {
                String nilaiStr = "Belum Dinilai";
                Optional<Nilai> nOpt = nilaiRepo.getAll().stream()
                    .filter(n -> n.getUjian() != null && n.getUjian().getIdUjian().equals(u.getIdUjian()) 
                                 && n.getSiswa().getIdUser().equals(j.getSiswa().getIdUser()))
                    .findFirst();
                
                if (nOpt.isPresent()) {
                    nilaiStr = String.valueOf(nOpt.get().getNilaiAngka());
                }
                model.addRow(new Object[]{j.getIdJawaban(), "Ujian", u.getNamaUjian(), j.getSiswa().getNamaLengkap(), j.getFileJawaban(), nilaiStr});
            }
        }
    }
    
    private void beriNilaiManual() {
        int row = table.getSelectedRow();
        if (row == -1) { 
            JOptionPane.showMessageDialog(this, "Pilih baris!"); 
            return; 
        }
        int modelRow = table.convertRowIndexToModel(row);
        String idJawaban = (String) model.getValueAt(modelRow, 0);
        
        Optional<Jawaban> selectedJawabOpt = jawabanRepo.getAll().stream()
            .filter(j->j.getIdJawaban().equals(idJawaban))
            .findFirst();
            
        if (selectedJawabOpt.isPresent()) {
            Jawaban selectedJawab = selectedJawabOpt.get();
            String input = JOptionPane.showInputDialog(this, "Masukkan Nilai:");
            if (input != null) {
                try {
                    int val = Integer.parseInt(input);
                    
                    Nilai n = (selectedJawab.getTugas()!=null) 
                        ? new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getTugas(), val, "Manual") 
                        : new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getUjian(), val, "Manual");
                        
                    nilaiRepo.addNilai(n);
                    selectedJawab.getSiswa().tambahNilai(n);
                    refreshTable();
                } catch(NumberFormatException ex) { 
                    JOptionPane.showMessageDialog(this, "Input angka!"); 
                }
            }
        }
    }
}