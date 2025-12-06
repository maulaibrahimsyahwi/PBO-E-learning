package view.panel;

import model.Kelas;
import model.Siswa;
import repository.KelasRepository;
import repository.UserRepository;
import utils.IdUtil;
import utils.SecurityUtil;
import view.renderer.KelasListRenderer;
import view.renderer.SiswaAssignmentRenderer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class SiswaManagementPanel extends JPanel {
    private UserRepository userRepo;
    private KelasRepository kelasRepo;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public SiswaManagementPanel(UserRepository userRepo, KelasRepository kelasRepo) {
        this.userRepo = userRepo;
        this.kelasRepo = kelasRepo;
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Username", "Nama", "NIS", "Kelas", "Angkatan", "Email"};
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
        searchPanel.add(new JLabel(" Cari Siswa: "), BorderLayout.WEST);
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

        JButton btnAdd = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit Data"); 
        JButton btnAssign = new JButton("Assign ke Kelas");
        JButton btnDelete = new JButton("Hapus"); 
        
        Dimension btnSize = new Dimension(130, 35);
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
            JTextField txtUser = new JTextField();
            JTextField txtPass = new JTextField();
            JTextField txtNama = new JTextField();
            JTextField txtEmail = new JTextField();
            JTextField txtNis = new JTextField();
            JTextField txtAngkatan = new JTextField();

            Object[] message = {
                "Username:", txtUser, "Password:", txtPass, "Nama:", txtNama,
                "Email:", txtEmail, "NIS:", txtNis, "Angkatan:", txtAngkatan
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Tambah Siswa", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String passHash = SecurityUtil.hashPassword(txtPass.getText());
                Siswa s = new Siswa(IdUtil.generate(), txtUser.getText(), passHash, 
                                    txtNama.getText(), txtEmail.getText(), txtNis.getText(), txtAngkatan.getText());
                userRepo.addUser(s);
                refreshTable();
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih siswa dulu!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) model.getValueAt(modelRow, 0);
            String user = (String) model.getValueAt(modelRow, 1);
            String nama = (String) model.getValueAt(modelRow, 2);
            String nis = (String) model.getValueAt(modelRow, 3);
            String angk = (String) model.getValueAt(modelRow, 5);
            String email = (String) model.getValueAt(modelRow, 6);

            JTextField txtUser = new JTextField(user);
            JTextField txtNama = new JTextField(nama);
            JTextField txtNis = new JTextField(nis);
            JTextField txtAngk = new JTextField(angk);
            JTextField txtEmail = new JTextField(email);

            Object[] message = {
                "Username:", txtUser, "Nama Lengkap:", txtNama,
                "NIS:", txtNis, "Angkatan:", txtAngk, "Email:", txtEmail
            };

            int opt = JOptionPane.showConfirmDialog(this, message, "Edit Siswa", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                Siswa sBaru = new Siswa(id, txtUser.getText(), "", txtNama.getText(), txtEmail.getText(), txtNis.getText(), txtAngk.getText());
                userRepo.updateSiswa(sBaru);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Data Siswa berhasil diupdate!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih siswa dulu!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) model.getValueAt(modelRow, 0);
            String nama = (String) model.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this, "Hapus siswa " + nama + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                userRepo.deleteUser(id);
                refreshTable();
            }
        });

        btnAssign.addActionListener(e -> {
            // 1. Setup Siswa List (Multi-Select - JList)
            DefaultListModel<Siswa> siswaListModel = new DefaultListModel<>();
            for (Siswa s : userRepo.getAllSiswa()) siswaListModel.addElement(s);
            JList<Siswa> listSiswa = new JList<>(siswaListModel);
            listSiswa.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            listSiswa.setCellRenderer(new SiswaAssignmentRenderer());
            JScrollPane scrollSiswa = new JScrollPane(listSiswa);
            scrollSiswa.setPreferredSize(new Dimension(350, 200));

            // 2. Setup Kelas Dropdown (Single Select - JComboBox)
            JComboBox<Kelas> comboKelas = new JComboBox<>();
            for(Kelas k : kelasRepo.getAll()) comboKelas.addItem(k);
            comboKelas.setRenderer(new KelasListRenderer());

            // 3. Combine components in a dialog panel (BoxLayout for vertical alignment)
            JPanel panelAssign = new JPanel();
            panelAssign.setLayout(new BoxLayout(panelAssign, BoxLayout.Y_AXIS));
            panelAssign.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel kelasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            kelasPanel.add(new JLabel("Pilih Kelas Tujuan:"));
            kelasPanel.add(comboKelas);
            kelasPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            panelAssign.add(kelasPanel);
            panelAssign.add(Box.createRigidArea(new Dimension(0, 10)));
            panelAssign.add(new JLabel("Pilih Siswa (Ctrl+Klik untuk multi-pilih):"));
            panelAssign.add(scrollSiswa);

            int result = JOptionPane.showConfirmDialog(this, panelAssign, "Assign Siswa ke Kelas (Multi-Select)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                Kelas k = (Kelas) comboKelas.getSelectedItem();
                List<Siswa> selectedSiswa = listSiswa.getSelectedValuesList();
                
                if (k != null && !selectedSiswa.isEmpty()) {
                    int count = 0;
                    for (Siswa s : selectedSiswa) {
                        s.setKelas(k);
                        userRepo.updateSiswa(s); 
                        count++;
                    }
                    
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Berhasil assign " + count + " Siswa ke kelas " + k.getNamaKelas());
                } else if (k == null) {
                    JOptionPane.showMessageDialog(this, "Pilih Kelas Tujuan.");
                } else {
                    JOptionPane.showMessageDialog(this, "Pilih minimal 1 Siswa.");
                }
            }
        });
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Siswa s : userRepo.getAllSiswa()) {
            String kls = (s.getKelas() != null) ? s.getKelas().getNamaKelas() : "-";
            model.addRow(new Object[]{s.getIdUser(), s.getUsername(), s.getNamaLengkap(), s.getNis(), kls, s.getAngkatan(), s.getEmail()});
        }
    }
}