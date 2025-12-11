package view.panel;

import model.Jawaban;
import model.Nilai;
import model.Siswa;
import model.Tugas;
import model.Ujian;
import repository.JawabanRepository;
import repository.NilaiRepository;
import repository.SoalRepository;
import repository.TugasRepository;
import repository.UjianRepository;
import utils.IdUtil;
import view.dialog.SiswaUjianDialog;
import view.GuiSiswa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Optional;

public class SiswaTugasUjianPanel extends JPanel {
    
    private final Siswa siswa;
    private final TugasRepository tugasRepo;
    private final UjianRepository ujianRepo;
    private final JawabanRepository jawabanRepo;
    private final NilaiRepository nilaiRepo;
    private final SoalRepository soalRepo;
    private final JTable table;
    private final DefaultTableModel tugasModel;
    private final JFrame parentFrame;

    public SiswaTugasUjianPanel(JFrame parent, Siswa s, TugasRepository tr, UjianRepository ur, JawabanRepository jr, NilaiRepository nr, SoalRepository sr) {
        this.parentFrame = parent;
        this.siswa = s;
        this.tugasRepo = tr;
        this.ujianRepo = ur;
        this.jawabanRepo = jr;
        this.nilaiRepo = nr;
        this.soalRepo = sr;
        
        setLayout(new BorderLayout());
        
        // 1. Tambahkan kolom "Deskripsi"
        String[] cols = {"ID", "Tipe", "Mapel", "Judul", "Deskripsi", "Tanggal/Deadline", "Status"};
        tugasModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tugasModel);
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        // Atur lebar kolom deskripsi agar lebih nyaman dibaca
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        refreshTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createButtonPanel() {
        JButton btnSubmit = new JButton("Kerjakan / Submit");
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSubmit.setBackground(new Color(100, 200, 100)); 
        btnSubmit.setForeground(Color.WHITE);
        
        btnSubmit.addActionListener(e -> handleSubmission());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.add(btnSubmit);
        
        return btnPanel;
    }

    public void refreshTable() {
        tugasModel.setRowCount(0);
        if(siswa.getKelas() == null) return;

        for(Tugas t : tugasRepo.getByKelas(siswa.getKelas())) {
            String status = "Belum Dikerjakan";
            boolean submitted = jawabanRepo.findByTugas(t.getIdTugas()).stream()
                    .anyMatch(j -> j.getSiswa().getIdUser().equals(siswa.getIdUser()));
            
            if (submitted) {
                status = "Sudah Submit";
                Optional<Nilai> n = nilaiRepo.getAll().stream()
                    .filter(nil -> nil.getTugas() != null && nil.getTugas().getIdTugas().equals(t.getIdTugas()) 
                            && nil.getSiswa().getIdUser().equals(siswa.getIdUser()))
                    .findFirst();
                if(n.isPresent()) {
                    status = "Selesai (Nilai: " + n.get().getNilaiAngka() + ")";
                }
            }
            String namaMapel = (t.getMapel() != null) ? t.getMapel().getNamaMapel() : "-";
            
            // 2. Isi kolom Deskripsi dengan t.getDeskripsi()
            tugasModel.addRow(new Object[]{
                t.getIdTugas(), 
                "Tugas", 
                namaMapel, 
                t.getJudul(), 
                t.getDeskripsi(), 
                t.getDeadline(), 
                status
            });
        }

        for(Ujian u : ujianRepo.getByKelas(siswa.getKelas())) {
            String status = "Belum Dikerjakan";
            boolean submitted = jawabanRepo.findByUjian(u.getIdUjian()).stream()
                    .anyMatch(j -> j.getSiswa().getIdUser().equals(siswa.getIdUser()));
            
            if (submitted) {
                status = "Sudah Submit";
                Optional<Nilai> n = nilaiRepo.getAll().stream()
                    .filter(nil -> nil.getUjian() != null && nil.getUjian().getIdUjian().equals(u.getIdUjian()) 
                            && nil.getSiswa().getIdUser().equals(siswa.getIdUser()))
                    .findFirst();
                if(n.isPresent()) {
                    status = "Selesai (Nilai: " + n.get().getNilaiAngka() + ")";
                }
            }
            String namaMapel = (u.getMapel() != null) ? u.getMapel().getNamaMapel() : "-";
            
            // 3. Buat Deskripsi untuk Ujian (Informasi Durasi/Waktu)
            String deskripsiUjian = u.getTipeUjian().equals("KUIS") ? 
                                    "Timer: " + u.getWaktuPerSoal() + " dtk/soal" : 
                                    "Durasi: " + u.getDurasiTotal() + " menit";
            
            tugasModel.addRow(new Object[]{
                u.getIdUjian(), 
                "Ujian", 
                namaMapel, 
                u.getNamaUjian(), 
                deskripsiUjian, 
                u.getTanggal(), 
                status
            });
        }
    }
    
    private void handleSubmission() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Pilih tugas atau ujian terlebih dahulu.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String id = (String) tugasModel.getValueAt(modelRow, 0);
        String tipe = (String) tugasModel.getValueAt(modelRow, 1);
        
        // 4. Update index pengambilan Status (karena ada kolom baru)
        // Kolom: 0=ID, 1=Tipe, 2=Mapel, 3=Judul, 4=Deskripsi, 5=Tanggal, 6=Status
        String status = (String) tugasModel.getValueAt(modelRow, 6);

        if (!status.equals("Belum Dikerjakan")) {
            JOptionPane.showMessageDialog(parentFrame, "Anda sudah mengerjakan/mengumpulkan ini!\nStatus: " + status, 
                                          "Sudah Selesai", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tipe.equals("Tugas")) {
            Tugas tFound = tugasRepo.getAll().stream().filter(t->t.getIdTugas().equals(id)).findFirst().orElse(null);
            if(tFound != null) submitTugasFile(tFound);
        } else {
            Ujian uFound = ujianRepo.getAll().stream().filter(u->u.getIdUjian().equals(id)).findFirst().orElse(null);
            if(uFound != null) new SiswaUjianDialog(parentFrame, siswa, uFound, soalRepo, nilaiRepo, jawabanRepo, this);
        }
    }

    private void submitTugasFile(Tugas t) {
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            File fileAsli = fc.getSelectedFile();
            Jawaban j = new Jawaban(IdUtil.generate(), siswa, t, fileAsli.getName());
            jawabanRepo.addJawaban(j, fileAsli);
            JOptionPane.showMessageDialog(parentFrame, "Jawaban Tugas Terkirim!");
            refreshTable(); 
            
            if (parentFrame instanceof GuiSiswa guiSiswa) {
                guiSiswa.refreshNotification();
            }
        }
    }
}