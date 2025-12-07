package view.panel;

import model.Absensi;
import model.Kelas;
import model.Siswa;
import repository.AbsensiRepository;
import repository.UserRepository;
import utils.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class GuruAbsensiPanel extends JPanel {
    private final Kelas kelas;
    private final AbsensiRepository absensiRepo;
    private final UserRepository userRepo;
    private final DefaultTableModel model;
    private final JTextField txtTanggal;

    public GuruAbsensiPanel(Kelas kelas, AbsensiRepository ar, UserRepository ur) {
        this.kelas = kelas;
        this.absensiRepo = ar;
        this.userRepo = ur;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tanggal (yyyy-MM-dd):"));
        txtTanggal = new JTextField(LocalDate.now().toString(), 10);
        JButton btnLoad = new JButton("Lihat Absensi");
        topPanel.add(txtTanggal);
        topPanel.add(btnLoad);

        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"NIS", "Nama Siswa", "Status", "Waktu Absen"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadData());
        
        loadData(); // Load default today
    }

    public void refreshTable() {
        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            LocalDate tgl = DateUtil.parse(txtTanggal.getText());
            List<Siswa> allSiswa = userRepo.getSiswaByKelas(kelas.getIdKelas());
            List<Absensi> absensiList = absensiRepo.getByKelasAndTanggal(kelas, tgl);

            for (Siswa s : allSiswa) {
                Absensi found = absensiList.stream()
                        .filter(a -> a.getSiswa().getIdUser().equals(s.getIdUser()))
                        .findFirst().orElse(null);

                String status = (found != null) ? "Hadir" : "Tidak Hadir";
                String waktu = (found != null) ? found.getWaktu() : "-";

                model.addRow(new Object[]{s.getNis(), s.getNamaLengkap(), status, waktu});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah (yyyy-MM-dd)");
        }
    }
}