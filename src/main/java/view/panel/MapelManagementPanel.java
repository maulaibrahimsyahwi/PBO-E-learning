package view.panel;

import model.Kelas;
import model.MataPelajaran;
import model.User;
import model.Guru;
import repository.KelasRepository;
import repository.MapelRepository;
import repository.UserRepository;
import utils.IdUtil;
import view.renderer.GuruListRenderer;
import view.renderer.KelasListRenderer;
import view.renderer.MapelAssignmentRenderer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

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
        searchPanel.add(new JLabel(" Cari Mapel: "), BorderLayout.WEST);
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
        JButton btnAssign = new JButton("Assign Guru");
        JButton btnDelete = new JButton("Hapus"); 
        
        Dimension btnSize = new Dimension(120, 35);
        btnAdd.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnAssign.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setBackground(new Color(255, 150, 150));
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnAssign);
        btnPanel.add(btnDelete);

        addListeners(btnAdd, btnEdit, btnAssign, btnDelete);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void addListeners(JButton btnAdd, JButton btnEdit, JButton btnAssign, JButton btnDelete) {
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

        btnAssign.addActionListener(e -> {
            // 1. Setup Guru Dropdown (Single Select - JComboBox)
            JComboBox<User> comboGuru = new JComboBox<>();
            for(User u : userRepo.getAll()) {
                if(u instanceof Guru) comboGuru.addItem(u);
            }
            comboGuru.setRenderer(new GuruListRenderer());
            
            // 2. Setup Mapel List (Multi-Select - JList)
            DefaultListModel<MataPelajaran> mapelListModel = new DefaultListModel<>();
            for(MataPelajaran m : mapelRepo.getAll()) mapelListModel.addElement(m);
            JList<MataPelajaran> listMapel = new JList<>(mapelListModel);
            listMapel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            listMapel.setCellRenderer(new MapelAssignmentRenderer());
            JScrollPane scrollMapel = new JScrollPane(listMapel);
            scrollMapel.setPreferredSize(new Dimension(300, 150));

            // 3. Setup Kelas List (Multi-Select - JList)
            DefaultListModel<Kelas> kelasListModel = new DefaultListModel<>();
            for(Kelas k : kelasRepo.getAll()) kelasListModel.addElement(k);
            JList<Kelas> listKelas = new JList<>(kelasListModel);
            listKelas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            listKelas.setCellRenderer(new KelasListRenderer());
            JScrollPane scrollKelas = new JScrollPane(listKelas);
            scrollKelas.setPreferredSize(new Dimension(300, 150));

            // 4. Combine components in a dialog panel 
            JPanel panelAssign = new JPanel();
            panelAssign.setLayout(new BoxLayout(panelAssign, BoxLayout.Y_AXIS));
            panelAssign.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel guruPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            guruPanel.add(new JLabel("Pilih Guru:"));
            guruPanel.add(comboGuru);
            guruPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            panelAssign.add(guruPanel);
            panelAssign.add(Box.createRigidArea(new Dimension(0, 10)));
            panelAssign.add(new JLabel("Pilih Mapel (Ctrl+Klik untuk multi-pilih):"));
            panelAssign.add(scrollMapel);
            panelAssign.add(Box.createRigidArea(new Dimension(0, 10)));
            panelAssign.add(new JLabel("Pilih Kelas (Ctrl+Klik untuk multi-pilih):"));
            panelAssign.add(scrollKelas);

            int result = JOptionPane.showConfirmDialog(this, panelAssign, "Assign Guru Mengajar (Multi-Select)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                Guru g = (Guru) comboGuru.getSelectedItem();
                List<MataPelajaran> selectedMapel = listMapel.getSelectedValuesList();
                List<Kelas> selectedKelas = listKelas.getSelectedValuesList();
                
                if (g != null && !selectedMapel.isEmpty() && !selectedKelas.isEmpty()) {
                    
                    int mapelCount = 0;
                    int kelasCount = 0;
                    
                    for (MataPelajaran m : selectedMapel) {
                        g.tambahMapel(m);
                        mapelCount++;
                    }

                    for (Kelas k : selectedKelas) {
                        g.tambahKelas(k);
                        kelasCount++;
                        
                        for (MataPelajaran m : selectedMapel) {
                            kelasRepo.addMapelToKelas(k.getIdKelas(), m.getIdMapel());
                        }
                    }
                    
                    userRepo.updateGuru(g); 
                    
                    JOptionPane.showMessageDialog(this, 
                        "Sukses assign Guru " + g.getNamaLengkap() + "\n" +
                        "Mengajar " + mapelCount + " Mapel di " + kelasCount + " Kelas."
                    );
                } else if (g == null) {
                    JOptionPane.showMessageDialog(this, "Pilih Guru yang akan di-assign.");
                } else {
                    JOptionPane.showMessageDialog(this, "Pilih minimal 1 Mapel dan 1 Kelas.");
                }
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