package view.panel;

import model.Kelas;
import model.MataPelajaran;
import repository.KelasRepository;
import repository.MapelRepository;
import utils.IdUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class KelasManagementPanel extends JPanel {
    private KelasRepository kelasRepo;
    private MapelRepository mapelRepo;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public KelasManagementPanel(KelasRepository kelasRepo, MapelRepository mapelRepo) {
        this.kelasRepo = kelasRepo;
        this.mapelRepo = mapelRepo;
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Nama Kelas", "Tingkat"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        refreshTable();
        setupLayout();
    }
    
    private void setupLayout() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.add(new JLabel(" Cari Kelas: "), BorderLayout.WEST);
        JTextField txtSearch = new JTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton btnAdd = new JButton("Tambah Kelas");
        JButton btnEdit = new JButton("Edit Kelas");
        JButton btnDelete = new JButton("Hapus"); 
        
        Dimension btnSize = new Dimension(120, 35);
        btnAdd.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setBackground(new Color(255, 150, 150));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        addListeners(btnAdd, btnEdit, btnDelete);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void addListeners(JButton btnAdd, JButton btnEdit, JButton btnDelete) {
        btnAdd.addActionListener(e -> {
            JTextField txtNama = new JTextField();
            JTextField txtTingkat = new JTextField();
            Object[] message = {"Nama Kelas:", txtNama, "Tingkat (10/11/12):", txtTingkat};

            if (JOptionPane.showConfirmDialog(this, message, "Tambah Kelas", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String idKelas = IdUtil.generate();
                Kelas kBaru = new Kelas(idKelas, txtNama.getText(), txtTingkat.getText());
                kelasRepo.addKelas(kBaru);
                
                for (MataPelajaran m : mapelRepo.getAll()) {
                    if (m.getTingkat().equals(kBaru.getTingkat())) {
                        kelasRepo.addMapelToKelas(idKelas, m.getIdMapel());
                    }
                }
                
                refreshTable();
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih kelas yang mau diedit!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String idKelas = (String) model.getValueAt(modelRow, 0);
            String namaLama = (String) model.getValueAt(modelRow, 1);
            String tingkatLama = (String) model.getValueAt(modelRow, 2);

            JTextField txtNama = new JTextField(namaLama);
            JTextField txtTingkat = new JTextField(tingkatLama);
            Object[] message = {"ID: " + idKelas, "Nama Kelas:", txtNama, "Tingkat (10/11/12):", txtTingkat};

            if (JOptionPane.showConfirmDialog(this, message, "Edit Kelas", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                Kelas kBaru = new Kelas(idKelas, txtNama.getText(), txtTingkat.getText());
                kelasRepo.updateKelas(kBaru);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Data Kelas berhasil diubah!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih kelas yang mau dihapus!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) model.getValueAt(modelRow, 0);
            String nama = (String) model.getValueAt(modelRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this, "Hapus kelas " + nama + "? (Data siswa di kelas ini mungkin terpengaruh)", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                kelasRepo.deleteKelas(id);
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Kelas k : kelasRepo.getAll()) model.addRow(new Object[]{k.getIdKelas(), k.getNamaKelas(), k.getTingkat()});
    }
}