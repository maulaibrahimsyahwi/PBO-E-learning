package view.panel;

import model.Materi;
import model.Siswa;
import repository.MateriRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class SiswaMateriPanel extends JPanel {
    
    private final Siswa siswa;
    private final MateriRepository materiRepo;
    private final JTable table;
    private final DefaultTableModel model;
    private final JFrame parentFrame;

    public SiswaMateriPanel(JFrame parent, Siswa s, MateriRepository mr) {
        this.parentFrame = parent;
        this.siswa = s;
        this.materiRepo = mr;
        
        setLayout(new BorderLayout());
        
        model = new DefaultTableModel(new String[]{"ID", "Mapel", "Judul", "Deskripsi", "File"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        refreshTable();
        
        JButton btnOpen = new JButton("Buka File Materi");
        btnOpen.addActionListener(e -> openFileMateri());
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnOpen, BorderLayout.SOUTH);
    }
    
    public void refreshTable() {
        model.setRowCount(0);
        if(siswa.getKelas() != null) {
            for(Materi m : materiRepo.getByKelas(siswa.getKelas())) {
                String namaMapel = (m.getMapel() != null) ? m.getMapel().getNamaMapel() : "-";
                model.addRow(new Object[]{m.getIdMateri(), namaMapel, m.getJudul(), m.getDeskripsi(), m.getFileMateri()});
            }
        }
    }
    
    private void openFileMateri() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int modelRow = table.convertRowIndexToModel(row);
            String idMateri = (String) model.getValueAt(modelRow, 0);
            String filename = (String) model.getValueAt(modelRow, 4);
            
            File folderUploads = new File("data/uploads/");
            if (!folderUploads.exists()) folderUploads.mkdirs();
            
            File fileTujuan = new File(folderUploads, filename);
            
            if (!fileTujuan.exists()) {
                boolean success = materiRepo.downloadFile(idMateri, fileTujuan);
                if (!success) {
                    JOptionPane.showMessageDialog(parentFrame, "Gagal mengunduh file dari database atau file tidak ada.");
                    return;
                }
            }
            
            try {
                if (fileTujuan.exists()) Desktop.getDesktop().open(fileTujuan);
                else JOptionPane.showMessageDialog(parentFrame, "File error.");
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(parentFrame, "Error membuka file: " + ex.getMessage()); 
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Pilih materi terlebih dahulu.");
        }
    }
}