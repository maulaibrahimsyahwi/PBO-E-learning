package view.panel;

import model.Jawaban;
import model.Kelas;
import model.MataPelajaran;
import model.Nilai;
import model.Siswa;
import model.Tugas;
import model.Ujian;
import repository.JawabanRepository;
import repository.NilaiRepository;
import repository.TugasRepository;
import repository.UjianRepository;
import repository.UserRepository;
import utils.IdUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuruNilaiPanel extends JPanel {
    
    private final Kelas kelas;
    private final MataPelajaran mapel;
    private final TugasRepository tugasRepo;
    private final UjianRepository ujianRepo;
    private final JawabanRepository jawabanRepo;
    private final NilaiRepository nilaiRepo;
    private final UserRepository userRepo;
    private final DefaultTableModel model;
    private final JTable table;
    
    public GuruNilaiPanel(Kelas k, MataPelajaran m, TugasRepository tr, UjianRepository ur, JawabanRepository jr, NilaiRepository nr, UserRepository urp) {
        this.kelas = k;
        this.mapel = m;
        this.tugasRepo = tr;
        this.ujianRepo = ur;
        this.jawabanRepo = jr;
        this.nilaiRepo = nr;
        this.userRepo = urp;
        
        setLayout(new BorderLayout());
        
        String[] columns = {"ID Jawaban", "Tipe", "Judul Soal", "Siswa", "File Jawaban", "Nilai"};
        model = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int row, int column) { return false; } };
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
        
        JButton btnNilai = new JButton("Beri Nilai Manual");
        btnNilai.setPreferredSize(new Dimension(150, 35));
        
        btnPanel.add(btnNilai);
        
        btnNilai.addActionListener(e -> beriNilaiManual());
        
        return btnPanel;
    }
    
    public void refreshTable() {
        model.setRowCount(0);
        
        for(Tugas t : tugasRepo.getByMapelAndKelas(mapel, kelas)) {
            for(Jawaban j : jawabanRepo.findByTugas(t.getIdTugas())) {
                String nilaiStr = "Belum Dinilai";
                
                Siswa s = (Siswa) userRepo.findById(j.getSiswa().getIdUser());
                String namaSiswa = (s != null) ? s.getNamaLengkap() : "Unknown";

                Optional<Nilai> nOpt = nilaiRepo.getAll().stream()
                    .filter(n -> n.getTugas() != null && n.getTugas().getIdTugas().equals(t.getIdTugas()) 
                                 && n.getSiswa().getIdUser().equals(j.getSiswa().getIdUser()))
                    .findFirst();
                
                if (nOpt.isPresent()) {
                    nilaiStr = String.valueOf(nOpt.get().getNilaiAngka());
                }
                model.addRow(new Object[]{j.getIdJawaban(), "Tugas", t.getJudul(), namaSiswa, j.getFileJawaban(), nilaiStr});
            }
        }
        
        for(Ujian u : ujianRepo.getByMapelAndKelas(mapel, kelas)) {
            for(Jawaban j : jawabanRepo.findByUjian(u.getIdUjian())) {
                String nilaiStr = "Belum Dinilai";

                Siswa s = (Siswa) userRepo.findById(j.getSiswa().getIdUser());
                String namaSiswa = (s != null) ? s.getNamaLengkap() : "Unknown";

                Optional<Nilai> nOpt = nilaiRepo.getAll().stream()
                    .filter(n -> n.getUjian() != null && n.getUjian().getIdUjian().equals(u.getIdUjian()) 
                                 && n.getSiswa().getIdUser().equals(j.getSiswa().getIdUser()))
                    .findFirst();
                
                if (nOpt.isPresent()) {
                    nilaiStr = String.valueOf(nOpt.get().getNilaiAngka());
                }
                model.addRow(new Object[]{j.getIdJawaban(), "Ujian", u.getNamaUjian(), namaSiswa, j.getFileJawaban(), nilaiStr});
            }
        }
    }
    
    private void beriNilaiManual() {
        int row = table.getSelectedRow();
        if (row == -1) { 
            JOptionPane.showMessageDialog(this, "Pilih baris!"); 
            return; 
        }
        int modelRow = table.convertRowIndexToModel(row);
        String idJawaban = (String) model.getValueAt(modelRow, 0);
        String tipe = (String) model.getValueAt(modelRow, 1);

        Jawaban selectedJawab = null;
        Tugas finalTugas = null;
        Ujian finalUjian = null;

        // PERBAIKAN PENTING: Set objek Tugas/Ujian ke Jawaban saat ditemukan agar tidak NullPointerException
        if (tipe.equals("Tugas")) {
            for (Tugas t : tugasRepo.getByMapelAndKelas(mapel, kelas)) {
                for (Jawaban j : jawabanRepo.findByTugas(t.getIdTugas())) {
                    if (j.getIdJawaban().equals(idJawaban)) {
                        selectedJawab = j;
                        finalTugas = t; 
                        selectedJawab.setTugas(t); // Mencegah NPE saat getTugas() dipanggil
                        break;
                    }
                }
                if (selectedJawab != null) break;
            }
        } else if (tipe.equals("Ujian")) {
            for (Ujian u : ujianRepo.getByMapelAndKelas(mapel, kelas)) {
                for (Jawaban j : jawabanRepo.findByUjian(u.getIdUjian())) {
                    if (j.getIdJawaban().equals(idJawaban)) {
                        selectedJawab = j;
                        finalUjian = u;
                        selectedJawab.setUjian(u); // Mencegah NPE saat getUjian() dipanggil
                        break;
                    }
                }
                if (selectedJawab != null) break;
            }
        }
        
        if (selectedJawab == null) {
             JOptionPane.showMessageDialog(this, "Jawaban tidak ditemukan (Internal Error).", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        Siswa fullSiswa = (Siswa) userRepo.findById(selectedJawab.getSiswa().getIdUser());
        if (fullSiswa == null) {
            JOptionPane.showMessageDialog(this, "Data Siswa tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        selectedJawab.setSiswa(fullSiswa);

        // Cari Nilai Lama (Untuk fitur Edit)
        final Tugas tCek = finalTugas;
        final Ujian uCek = finalUjian;

        Optional<Nilai> existingNilai = nilaiRepo.getAll().stream()
            .filter(n -> n.getSiswa().getIdUser().equals(fullSiswa.getIdUser()) &&
                   ( (n.getTugas() != null && tCek != null && n.getTugas().getIdTugas().equals(tCek.getIdTugas())) ||
                     (n.getUjian() != null && uCek != null && n.getUjian().getIdUjian().equals(uCek.getIdUjian())) )
            ).findFirst();

        String initialValue = existingNilai.map(nilai -> String.valueOf(nilai.getNilaiAngka())).orElse("");
        String input = JOptionPane.showInputDialog(this, "Masukkan Nilai (0-100):", initialValue);
        
        if (input != null) {
            try {
                int val = Integer.parseInt(input);
                
                if (existingNilai.isPresent()) {
                    // Update Nilai Lama
                    Nilai n = existingNilai.get();
                    n.setNilaiAngka(val);
                    n.setKeterangan("Manual (Edited)");
                    nilaiRepo.updateNilai(n);
                    JOptionPane.showMessageDialog(this, "Nilai berhasil diupdate!");
                } else {
                    // Buat Nilai Baru
                    Nilai n = (selectedJawab.getTugas() != null) 
                        ? new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getTugas(), val, "Manual") 
                        : new Nilai(IdUtil.generate(), selectedJawab.getSiswa(), selectedJawab.getUjian(), val, "Manual");
                    
                    nilaiRepo.addNilai(n);
                    fullSiswa.tambahNilai(n);
                    JOptionPane.showMessageDialog(this, "Nilai berhasil disimpan!");
                }
                
                refreshTable();
            } catch(NumberFormatException ex) { 
                JOptionPane.showMessageDialog(this, "Input angka valid!", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }
}