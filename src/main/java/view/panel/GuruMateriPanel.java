package view.panel;

import model.Guru;
import model.Kelas;
import model.MataPelajaran;
import model.Materi;
import repository.MateriRepository;
import utils.IdUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class GuruMateriPanel extends JPanel {
    
    private final Guru guru;
    private final Kelas kelas;
    private final MataPelajaran mapel;
    private final MateriRepository materiRepo;
    private final DefaultTableModel model;
    private final JTable table;
    
    public GuruMateriPanel(Guru g, Kelas k, MataPelajaran m, MateriRepository mr) {
        this.guru = g;
        this.kelas = k;
        this.mapel = m;
        this.materiRepo = mr;
        
        setLayout(new BorderLayout());
        
        model = new DefaultTableModel(new String[]{"ID", "Judul", "File"}, 0);
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
        
        JButton btnAdd = new JButton("Tambah Materi (Upload)");
        JButton btnDelete = new JButton("Hapus");
        
        Dimension btnSize = new Dimension(180, 35);
        btnAdd.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(new Dimension(100, 35));
        btnDelete.setBackground(new Color(255, 150, 150));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        
        btnAdd.addActionListener(e -> tambahMateri());
        btnDelete.addActionListener(e -> hapusMateri());
        
        return btnPanel;
    }
    
    public void refreshTable() {
        model.setRowCount(0);
        for(Materi mat : materiRepo.getByMapelAndKelas(mapel, kelas)) 
            model.addRow(new Object[]{mat.getIdMateri(), mat.getJudul(), mat.getFileMateri()});
    }

    private void tambahMateri() {
        JTextField txtJudul = new JTextField();
        JTextField txtDesk = new JTextField();
        Object[] message = { "Judul Materi:", txtJudul, "Deskripsi:", txtDesk };

        if (JOptionPane.showConfirmDialog(this, message, "Tambah Materi", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File fileAsli = fileChooser.getSelectedFile();
                    String namaFile = fileAsli.getName();
                    
                    Materi mat = new Materi(IdUtil.generate(), txtJudul.getText(), txtDesk.getText(), namaFile);
                    mat.setGuru(guru); 
                    mat.setKelas(kelas); 
                    mat.setMapel(mapel);
                    
                    materiRepo.addMateri(mat, fileAsli); 
                    
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Berhasil upload ke Database!");
                    
                } catch (Exception ex) { 
                    JOptionPane.showMessageDialog(this, "Gagal upload: " + ex.getMessage()); 
                }
            }
        }
    }
    
    private void hapusMateri() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih materi yang akan dihapus!");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String id = (String) model.getValueAt(modelRow, 0);
        String judul = (String) model.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus materi '" + judul + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            materiRepo.deleteMateri(id);
            refreshTable();
        }
    }
}