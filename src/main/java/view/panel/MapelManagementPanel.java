package view.panel;

import model.Kelas;
import model.MataPelajaran;
import repository.KelasRepository;
import repository.MapelRepository;
import repository.UserRepository;
import utils.IdUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class MapelManagementPanel extends JPanel {
    private MapelRepository mapelRepo;
    private KelasRepository kelasRepo;
    private UserRepository userRepo;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public MapelManagementPanel(MapelRepository mapelRepo, KelasRepository kelasRepo, UserRepository userRepo) {
        this.mapelRepo = mapelRepo;
        this.kelasRepo = kelasRepo;
        this.userRepo = userRepo;
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Nama Mapel", "Deskripsi", "Tingkat"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
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
        searchPanel.add(new JLabel("Cari"), BorderLayout.WEST);
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
        
        JButton btnAdd = new JButton("Tambah Mapel");
        JButton btnEdit = new JButton("Edit Mapel");
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
            JTextField txtDesk = new JTextField();
            JTextField txtTingkat = new JTextField();
            Object[] msg = {"Nama:", txtNama, "Deskripsi:", txtDesk, "Tingkat:", txtTingkat};
            
            if (JOptionPane.showConfirmDialog(this, msg, "Tambah Mapel", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                MataPelajaran m = new MataPelajaran(IdUtil.generate(), txtNama.getText(), txtDesk.getText(), txtTingkat.getText());
                mapelRepo.addMapel(m);
                
                int count = 0;
                for (Kelas k : kelasRepo.getAll()) {
                    if (k.getTingkat().equals(m.getTingkat())) {
                        k.tambahMapel(m); 
                        kelasRepo.addMapelToKelas(k.getIdKelas(), m.getIdMapel());
                        count++;
                    }
                }
                refreshTable();
                JOptionPane.showMessageDialog(this, "Mapel ditambahkan dan didistribusikan ke " + count + " kelas.");
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih mapel yang mau diedit!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) model.getValueAt(modelRow, 0);
            String nama = (String) model.getValueAt(modelRow, 1);
            String desk = (String) model.getValueAt(modelRow, 2);
            String tkt = (String) model.getValueAt(modelRow, 3);

            JTextField txtNama = new JTextField(nama);
            JTextField txtDesk = new JTextField(desk);
            JTextField txtTingkat = new JTextField(tkt);
            Object[] msg = {"Nama:", txtNama, "Deskripsi:", txtDesk, "Tingkat:", txtTingkat};
            
            if (JOptionPane.showConfirmDialog(this, msg, "Edit Mapel", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                MataPelajaran mBaru = new MataPelajaran(id, txtNama.getText(), txtDesk.getText(), txtTingkat.getText());
                mapelRepo.updateMapel(mBaru);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Mapel berhasil diupdate!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih mapel yang mau dihapus!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) model.getValueAt(modelRow, 0);
            String nama = (String) model.getValueAt(modelRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this, "Hapus mapel " + nama + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mapelRepo.deleteMapel(id);
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (MataPelajaran m : mapelRepo.getAll()) model.addRow(new Object[]{m.getIdMapel(), m.getNamaMapel(), m.getDeskripsi(), m.getTingkat()});
    }
}