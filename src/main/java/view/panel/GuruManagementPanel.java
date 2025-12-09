package view.panel;

import model.Guru;
import model.User;
import repository.UserRepository;
import utils.IdUtil;
import utils.SecurityUtil;
import utils.ValidationUtil;
import utils.LoadingUtil;
import utils.LoggerUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class GuruManagementPanel extends JPanel {
    private UserRepository userRepo;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public GuruManagementPanel(UserRepository userRepo) {
        this.userRepo = userRepo;
        setLayout(new BorderLayout());
        
        String[] columns = {"ID", "Username", "Nama", "NIP", "Spesialisasi"};
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
        
        JButton btnAdd = new JButton("Tambah Guru");
        JButton btnResetPass = new JButton("Reset Password");
        JButton btnDelete = new JButton("Hapus"); 
        
        Dimension btnSize = new Dimension(120, 35);
        btnAdd.setPreferredSize(btnSize);
        btnResetPass.setPreferredSize(new Dimension(140, 35));
        btnDelete.setPreferredSize(btnSize);
        
        btnResetPass.setBackground(new Color(255, 200, 100));
        btnDelete.setBackground(new Color(255, 150, 150)); 

        btnPanel.add(btnAdd);
        btnPanel.add(btnResetPass);
        btnPanel.add(btnDelete);
        
        addListeners(btnAdd, btnResetPass, btnDelete);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void addListeners(JButton btnAdd, JButton btnResetPass, JButton btnDelete) {
        btnAdd.addActionListener(e -> {
            JTextField txtUser = new JTextField();
            JTextField txtPass = new JTextField();
            JTextField txtNama = new JTextField();
            JTextField txtEmail = new JTextField();
            JTextField txtNip = new JTextField();
            JTextField txtSpes = new JTextField();
            
            Object[] message = {
                "Username:", txtUser, "Password:", txtPass, "Nama:", txtNama,
                "Email:", txtEmail, "NIP:", txtNip, "Spesialisasi:", txtSpes
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Tambah Guru", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String username = ValidationUtil.validateUsername(txtUser.getText(), this);
                    if (username == null) return;
                    
                    String password = ValidationUtil.validatePassword(txtPass.getText(), this);
                    if (password == null) return;
                    
                    String nama = ValidationUtil.validateName(txtNama.getText(), this);
                    if (nama == null) return;
                    
                    String email = ValidationUtil.validateEmail(txtEmail.getText(), this);
                    if (email == null) return;
                    
                    String nip = ValidationUtil.validateNIP(txtNip.getText(), this);
                    if (nip == null) return;
                    
                    String spesialisasi = ValidationUtil.validateNotEmpty(txtSpes.getText(), "Spesialisasi", this);
                    if (spesialisasi == null) return;
                    
                    LoadingUtil.executeWithLoading(this, "Menyimpan Data", "Menyimpan data guru...", () -> {
                        String passHash = SecurityUtil.hashPassword(password);
                        Guru g = new Guru(IdUtil.generate(), username, passHash, 
                                          nama, email, nip, spesialisasi);
                        userRepo.addUser(g);
                        
                        LoggerUtil.logAudit("admin", "CREATE_GURU", "Created guru: " + g.getIdUser());
                    });
                    
                    refreshTable();
                    ValidationUtil.showSuccess(this, "Guru berhasil ditambahkan!");
                    
                } catch (Exception ex) {
                    LoggerUtil.handleDatabaseError("GuruManagementPanel", "addGuru", ex, this);
                }
            }
        });

        btnResetPass.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih guru yang akan di-reset passwordnya!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) model.getValueAt(modelRow, 0);
            String nama = (String) model.getValueAt(modelRow, 2);

            String newPass = JOptionPane.showInputDialog(this, "Masukkan Password Baru untuk " + nama + ":");
            
            String validatedPass = ValidationUtil.validatePassword(newPass, this);
            if (validatedPass == null) return;
            
            try {
                LoadingUtil.executeWithLoading(this, "Reset Password", "Mengubah password...", () -> {
                    String passHash = SecurityUtil.hashPassword(validatedPass);
                    userRepo.updatePassword(id, passHash);
                    
                    LoggerUtil.logAudit("admin", "RESET_PASSWORD", "Reset password for guru ID: " + id);
                });
                
                ValidationUtil.showSuccess(this, "Password berhasil diubah!");
                
            } catch (Exception ex) {
                LoggerUtil.handleDatabaseError("GuruManagementPanel", "resetPassword", ex, this);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih guru yang akan dihapus!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) model.getValueAt(modelRow, 0);
            String nama = (String) model.getValueAt(modelRow, 2);
            
            if (!ValidationUtil.confirmAction(this, "Hapus guru " + nama + "?")) {
                return;
            }
            
            try {
                LoadingUtil.executeWithLoading(this, "Menghapus Data", "Menghapus guru...", () -> {
                    userRepo.deleteUser(id);
                    
                    LoggerUtil.logAudit("admin", "DELETE_GURU", "Deleted guru ID: " + id);
                });
                
                refreshTable();
                ValidationUtil.showSuccess(this, "Guru berhasil dihapus!");
                
            } catch (Exception ex) {
                LoggerUtil.handleDatabaseError("GuruManagementPanel", "deleteGuru", ex, this);
            }
        });
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (User u : userRepo.getAll()) {
            if (u instanceof Guru g) {
                model.addRow(new Object[]{g.getIdUser(), g.getUsername(), g.getNamaLengkap(), g.getNip(), g.getSpesialisasi()});
            }
        }
    }
}