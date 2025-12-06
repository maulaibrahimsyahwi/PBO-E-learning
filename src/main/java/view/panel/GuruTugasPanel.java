package view.panel;

import model.Guru;
import model.Kelas;
import model.MataPelajaran;
import model.Tugas;
import repository.TugasRepository;
import utils.DateUtil;
import utils.IdUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GuruTugasPanel extends JPanel {
    
    private final Guru guru;
    private final Kelas kelas;
    private final MataPelajaran mapel;
    private final TugasRepository tugasRepo;
    private final DefaultTableModel model;
    private final JTable table;
    private final GuruUjianPanel parentUjianPanel;
    
    public GuruTugasPanel(Guru g, Kelas k, MataPelajaran m, TugasRepository tr, GuruUjianPanel p) {
        this.guru = g;
        this.kelas = k;
        this.mapel = m;
        this.tugasRepo = tr;
        this.parentUjianPanel = p;
        
        setLayout(new BorderLayout());
        
        model = new DefaultTableModel(new String[]{"ID", "Judul", "Deadline"}, 0);
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
        
        JButton btnAdd = new JButton("Buat Tugas");
        JButton btnDelete = new JButton("Hapus");
        
        Dimension btnSize = new Dimension(120, 35);
        btnAdd.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(new Dimension(100, 35));
        btnDelete.setBackground(new Color(255, 150, 150));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        
        btnAdd.addActionListener(e -> tambahTugas());
        btnDelete.addActionListener(e -> hapusTugas());
        
        return btnPanel;
    }
    
    public void refreshTable() {
        model.setRowCount(0);
        for(Tugas t : tugasRepo.getByMapelAndKelas(mapel, kelas)) 
            model.addRow(new Object[]{t.getIdTugas(), t.getJudul(), t.getDeadline()});
    }

    private void tambahTugas() {
        String judul = JOptionPane.showInputDialog(this, "Judul:");
        if (judul == null || judul.isBlank()) return;
        
        String desk = JOptionPane.showInputDialog(this, "Deskripsi:");
        String tgl = JOptionPane.showInputDialog(this, "Deadline (yyyy-MM-dd):");
        
        if (tgl != null) {
            try {
                Tugas t = new Tugas(IdUtil.generate(), judul, desk, DateUtil.parse(tgl));
                t.setGuru(guru); t.setKelas(kelas); t.setMapel(mapel);
                tugasRepo.addTugas(t);
                refreshTable();
                if (parentUjianPanel != null) parentUjianPanel.refreshTable();
            } catch(Exception ex) { 
                JOptionPane.showMessageDialog(this, "Format tanggal salah"); 
            }
        }
    }
    
    private void hapusTugas() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang akan dihapus!");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String id = (String) model.getValueAt(modelRow, 0);
        String judul = (String) model.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus tugas '" + judul + "'? (Data nilai & jawaban siswa akan terhapus)", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tugasRepo.deleteTugas(id);
            refreshTable();
            if (parentUjianPanel != null) parentUjianPanel.refreshTable();
        }
    }
}