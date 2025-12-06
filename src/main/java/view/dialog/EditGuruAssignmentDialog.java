package view.dialog;

import model.Guru;
import model.Kelas;
import model.MataPelajaran;
import repository.KelasRepository;
import repository.MapelRepository;
import repository.UserRepository;
import view.panel.GuruAssignmentPanel;
import view.renderer.KelasListRenderer;
import view.renderer.MapelAssignmentRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EditGuruAssignmentDialog extends JDialog {
    private Guru guru;
    private UserRepository userRepo;
    private MapelRepository mapelRepo;
    private KelasRepository kelasRepo;
    private DefaultTableModel parentTableModel;
    
    private JList<MataPelajaran> listMapel;
    private JList<Kelas> listKelas;

    public EditGuruAssignmentDialog(JFrame parent, Guru guru, UserRepository userRepo, MapelRepository mapelRepo, KelasRepository kelasRepo, DefaultTableModel parentTableModel) {
        super(parent, "Edit Assignment - " + guru.getNamaLengkap(), true);
        this.guru = guru;
        this.userRepo = userRepo;
        this.mapelRepo = mapelRepo;
        this.kelasRepo = kelasRepo;
        this.parentTableModel = parentTableModel;
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
        loadInitialSelection();
    }
    
    private void initComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel Mapel
        JPanel mapelPanel = new JPanel(new BorderLayout());
        mapelPanel.add(new JLabel("Mata Pelajaran yang Diajar:"), BorderLayout.NORTH);
        
        DefaultListModel<MataPelajaran> mapelListModel = new DefaultListModel<>();
        for (MataPelajaran m : mapelRepo.getAll()) {
            mapelListModel.addElement(m);
        }
        listMapel = new JList<>(mapelListModel);
        listMapel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listMapel.setCellRenderer(new MapelAssignmentRenderer());
        
        JScrollPane scrollMapel = new JScrollPane(listMapel);
        scrollMapel.setPreferredSize(new Dimension(400, 120));
        mapelPanel.add(scrollMapel, BorderLayout.CENTER);
        
        // Panel Kelas
        JPanel kelasPanel = new JPanel(new BorderLayout());
        kelasPanel.add(new JLabel("Kelas yang Diajar:"), BorderLayout.NORTH);
        
        DefaultListModel<Kelas> kelasListModel = new DefaultListModel<>();
        for (Kelas k : kelasRepo.getAll()) {
            kelasListModel.addElement(k);
        }
        listKelas = new JList<>(kelasListModel);
        listKelas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listKelas.setCellRenderer(new KelasListRenderer());
        
        JScrollPane scrollKelas = new JScrollPane(listKelas);
        scrollKelas.setPreferredSize(new Dimension(400, 120));
        kelasPanel.add(scrollKelas, BorderLayout.CENTER);
        
        contentPanel.add(mapelPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(kelasPanel);
        
        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSimpan = new JButton("Simpan Perubahan");
        JButton btnBatal = new JButton("Batal");
        
        btnSimpan.addActionListener(e -> saveAssignment());
        btnBatal.addActionListener(e -> dispose());
        
        btnPanel.add(btnSimpan);
        btnPanel.add(btnBatal);
        
        add(contentPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void loadInitialSelection() {
        // Pre-select mapel
        List<Integer> selectedMapelIndices = new ArrayList<>();
        DefaultListModel<MataPelajaran> mapelModel = (DefaultListModel<MataPelajaran>) listMapel.getModel();
        for (int i = 0; i < mapelModel.size(); i++) {
            MataPelajaran mp = mapelModel.get(i);
            if (guru.getMapelDiampu().stream().anyMatch(m -> m.getIdMapel().equals(mp.getIdMapel()))) {
                selectedMapelIndices.add(i);
            }
        }
        int[] arrMapel = selectedMapelIndices.stream().mapToInt(Integer::intValue).toArray();
        listMapel.setSelectedIndices(arrMapel);
        
        // Pre-select kelas
        List<Integer> selectedKelasIndices = new ArrayList<>();
        DefaultListModel<Kelas> kelasModel = (DefaultListModel<Kelas>) listKelas.getModel();
        for (int i = 0; i < kelasModel.size(); i++) {
            Kelas k = kelasModel.get(i);
            if (guru.getDaftarKelas().stream().anyMatch(kls -> kls.getIdKelas().equals(k.getIdKelas()))) {
                selectedKelasIndices.add(i);
            }
        }
        int[] arrKelas = selectedKelasIndices.stream().mapToInt(Integer::intValue).toArray();
        listKelas.setSelectedIndices(arrKelas);
    }
    
    private void saveAssignment() {
        guru.getMapelDiampu().clear();
        guru.getDaftarKelas().clear();
        
        List<MataPelajaran> selectedMapel = listMapel.getSelectedValuesList();
        List<Kelas> selectedKelas = listKelas.getSelectedValuesList();
        
        for (MataPelajaran m : selectedMapel) {
            guru.tambahMapel(m);
        }
        
        for (Kelas k : selectedKelas) {
            guru.tambahKelas(k);
            for (MataPelajaran m : selectedMapel) {
                kelasRepo.addMapelToKelas(k.getIdKelas(), m.getIdMapel());
            }
        }
        
        userRepo.updateGuru(guru);
        
        // Memaksa refresh pada parent panel
        if (getParent() instanceof JFrame) {
            JFrame parentFrame = (JFrame) getParent();
            for (Component comp : parentFrame.getContentPane().getComponents()) {
                if (comp instanceof JTabbedPane tabs) {
                    for (int i = 0; i < tabs.getTabCount(); i++) {
                        if (tabs.getTitleAt(i).equals("Assignment Guru")) {
                            Component panel = tabs.getComponentAt(i);
                            if (panel instanceof GuruAssignmentPanel guruAssignmentPanel) {
                                guruAssignmentPanel.refreshTable();
                            }
                        }
                    }
                }
            }
        }
        
        dispose();
        JOptionPane.showMessageDialog(getParent(), "Assignment berhasil diupdate!");
    }
}