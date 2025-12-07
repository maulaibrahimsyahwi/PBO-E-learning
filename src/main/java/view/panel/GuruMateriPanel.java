package view.panel;

import model.Guru;
import model.Kelas;
import model.MataPelajaran;
import model.Materi;
import repository.MateriRepository;
import service.FileService;
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
    private final FileService fileService; 
    private final DefaultTableModel model;
    private final JTable table;
    
    public GuruMateriPanel(Guru g, Kelas k, MataPelajaran m, MateriRepository mr) {
        this.guru = g;
        this.kelas = k;
        this.mapel = m;
        this.materiRepo = mr;
        this.fileService = new FileService();
        
        setLayout(new BorderLayout());
        
        model = new DefaultTableModel(new String[]{"ID", "Judul", "File"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        
        refreshTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton btnAdd = new JButton("Tambah Materi");
        JButton btnDelete = new JButton("Hapus");
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
            if (txtJudul.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Judul tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File fileAsli = fileChooser.getSelectedFile();
                    
                    String ext = "";
                    int i = fileAsli.getName().lastIndexOf('.');
                    if (i > 0) ext = fileAsli.getName().substring(i);
                    String newName = IdUtil.generate() + "_" + System.currentTimeMillis() + ext;
                    
                    String savedFileName = fileService.uploadFile(fileAsli, newName);
                    
                    Materi mat = new Materi(IdUtil.generate(), txtJudul.getText(), txtDesk.getText(), savedFileName);
                    mat.setGuru(guru); 
                    mat.setKelas(kelas); 
                    mat.setMapel(mapel);
                    
                    materiRepo.addMateri(mat, null); 
                    
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Berhasil upload file!");
                    
                } catch (Exception ex) { 
                    JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
                }
            }
        }
    }
    
    private void hapusMateri() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        
        String id = (String) model.getValueAt(table.convertRowIndexToModel(row), 0);
        String filename = (String) model.getValueAt(table.convertRowIndexToModel(row), 2);
        
        if (JOptionPane.showConfirmDialog(this, "Hapus materi?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            fileService.deleteFile(filename);
            materiRepo.deleteMateri(id);
            refreshTable();
        }
    }
}