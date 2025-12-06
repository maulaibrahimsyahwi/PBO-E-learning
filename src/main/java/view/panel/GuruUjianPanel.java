package view.panel;

import model.Guru;
import model.Kelas;
import model.MataPelajaran;
import model.Ujian;
import repository.SoalRepository;
import repository.UjianRepository;
import view.dialog.GuruUjianSoalDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GuruUjianPanel extends JPanel {
    
    private final Guru guru;
    private final Kelas kelas;
    private final MataPelajaran mapel;
    private final UjianRepository ujianRepo;
    private final SoalRepository soalRepo;
    private final DefaultTableModel model;
    private final JTable table;
    private final JFrame parentFrame;
    
    public GuruUjianPanel(JFrame parent, Guru g, Kelas k, MataPelajaran m, UjianRepository ur, SoalRepository sr) {
        this.parentFrame = parent;
        this.guru = g;
        this.kelas = k;
        this.mapel = m;
        this.ujianRepo = ur;
        this.soalRepo = sr;
        
        setLayout(new BorderLayout());
        
        model = new DefaultTableModel(new String[]{"ID", "Nama", "Tipe", "Tanggal", "Info"}, 0);
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
        
        JButton btnAdd = new JButton("Buat Ujian & Soal");
        JButton btnDelete = new JButton("Hapus");
        
        Dimension btnSize = new Dimension(150, 35);
        btnAdd.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(new Dimension(100, 35));
        btnDelete.setBackground(new Color(255, 150, 150));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        
        btnAdd.addActionListener(e -> new GuruUjianSoalDialog(parentFrame, guru, kelas, mapel, ujianRepo, soalRepo, this).setVisible(true));
        btnDelete.addActionListener(e -> hapusUjian());
        
        return btnPanel;
    }
    
    public void refreshTable() {
        model.setRowCount(0);
        for(Ujian u : ujianRepo.getByMapelAndKelas(mapel, kelas)) {
            String info = u.getTipeUjian().equals("KUIS") ? 
                          u.getWaktuPerSoal() + "s/soal" : u.getDurasiTotal() + " menit";
            model.addRow(new Object[]{u.getIdUjian(), u.getNamaUjian(), u.getTipeUjian(), u.getTanggal(), info});
        }
    }
    
    private void hapusUjian() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih ujian yang akan dihapus!");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String id = (String) model.getValueAt(modelRow, 0);
        String nama = (String) model.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus ujian '" + nama + "'? (Semua soal & nilai akan terhapus)", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ujianRepo.deleteUjian(id);
            refreshTable();
        }
    }
}