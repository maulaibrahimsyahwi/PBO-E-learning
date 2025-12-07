package view.panel;

import model.Absensi;
import model.Siswa;
import repository.AbsensiRepository;
import utils.IdUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class SiswaAbsensiPanel extends JPanel {
    
    private final Siswa siswa;
    private final AbsensiRepository absensiRepo;
    private final JButton btn;

    public SiswaAbsensiPanel(Siswa s, AbsensiRepository ar) {
        this.siswa = s;
        this.absensiRepo = ar;
        
        setLayout(new GridBagLayout());
        
        btn = new JButton("Presensi Hari Ini");
        btn.setPreferredSize(new Dimension(200, 50));
        
        checkAbsensiStatus();
        
        btn.addActionListener(e -> submitAbsensi());
        
        add(btn);
    }
    
    private void checkAbsensiStatus() {
        if (siswa.getKelas() == null) {
            btn.setEnabled(false);
            btn.setText("Belum Masuk Kelas 🚫");
            btn.setBackground(Color.LIGHT_GRAY);
            return;
        }
        
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(7, 0)) || now.isAfter(LocalTime.of(15, 0))) {
            btn.setEnabled(false);
            btn.setText("Absensi Tutup (07.00-15.00)");
            btn.setBackground(Color.LIGHT_GRAY);
            return;
        }
        
        boolean done = absensiRepo.sudahAbsen(siswa, LocalDate.now());
        if(done) {
            btn.setEnabled(false);
            btn.setText("Sudah Hadir ✅");
            btn.setBackground(new Color(144, 238, 144));
        }
    }
    
    private void submitAbsensi() {
        if (siswa.getKelas() == null) return;
        
        Absensi a = new Absensi(IdUtil.generate(), siswa, siswa.getKelas(), LocalDate.now(), LocalTime.now().toString());
        absensiRepo.addAbsensi(a);
        JOptionPane.showMessageDialog(this, "Berhasil Absen!");
        
        btn.setEnabled(false);
        btn.setText("Sudah Hadir ✅");
        btn.setBackground(new Color(144, 238, 144));
    }
}