package view.dialog;

import model.Guru;
import model.Kelas;
import model.MataPelajaran;
import model.Soal;
import model.Ujian;
import repository.SoalRepository;
import repository.UjianRepository;
import utils.DateUtil;
import utils.IdUtil;
import view.panel.GuruUjianPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class GuruUjianSoalDialog extends JDialog {
    
    private final Guru guru;
    private final Kelas kelas;
    private final MataPelajaran mapel;
    private final UjianRepository ujianRepo;
    private final SoalRepository soalRepo;
    private final GuruUjianPanel parentPanel;
    private File selectedImageFile;

    public GuruUjianSoalDialog(JFrame parent, Guru g, Kelas k, MataPelajaran m, UjianRepository ur, SoalRepository sr, GuruUjianPanel p) {
        super(parent, "Buat Ujian Baru", true);
        this.guru = g;
        this.kelas = k;
        this.mapel = m;
        this.ujianRepo = ur;
        this.soalRepo = sr;
        this.parentPanel = p;
        
        initTambahUjianUI();
    }

    private void initTambahUjianUI() {
        setSize(450, 550);
        setLayout(new GridLayout(8, 2, 10, 10));
        setLocationRelativeTo(getParent());

        JTextField txtNama = new JTextField();
        String[] types = {"Pilihan Ganda (PG)", "Essay", "Campuran (Hybrid)", "Kuis (Timer per Soal)"};
        JComboBox<String> comboTipe = new JComboBox<>(types);
        
        JTextField txtTgl = new JTextField(); 
        JTextField txtDurasi = new JTextField("60"); 
        JTextField txtWaktuPerSoal = new JTextField("0"); 
        JTextField txtMaxSoal = new JTextField("10"); 
        
        txtWaktuPerSoal.setEnabled(false);

        comboTipe.addActionListener(e -> {
            if (comboTipe.getSelectedIndex() == 3) { 
                txtWaktuPerSoal.setEnabled(true);
                txtDurasi.setEnabled(false); txtDurasi.setText("0");
            } else {
                txtWaktuPerSoal.setEnabled(false); txtWaktuPerSoal.setText("0");
                txtDurasi.setEnabled(true);
            }
        });

        add(new JLabel("Nama Ujian:")); add(txtNama);
        add(new JLabel("Tipe Ujian:")); add(comboTipe);
        add(new JLabel("Tanggal (yyyy-MM-dd):")); add(txtTgl);
        add(new JLabel("Durasi Total (menit):")); add(txtDurasi);
        add(new JLabel("Waktu Per Soal (detik - Kuis):")); add(txtWaktuPerSoal);
        add(new JLabel("Maksimal Soal:")); add(txtMaxSoal);

        JButton btnSimpan = new JButton("Simpan & Lanjut Buat Soal");
        btnSimpan.addActionListener(e -> simpanUjian(txtNama, comboTipe, txtTgl, txtDurasi, txtWaktuPerSoal, txtMaxSoal));
        
        add(new JLabel("")); add(btnSimpan);
    }
    
    private void simpanUjian(JTextField txtNama, JComboBox<String> comboTipe, JTextField txtTgl, JTextField txtDurasi, JTextField txtWaktuPerSoal, JTextField txtMaxSoal) {
        try {
            String idUjian = IdUtil.generate();
            String rawTipe = (String) comboTipe.getSelectedItem();
            String kodeTipe = "PG";
            if(rawTipe.contains("Essay")) kodeTipe = "ESSAY";
            else if(rawTipe.contains("Campuran")) kodeTipe = "HYBRID";
            else if(rawTipe.contains("Kuis")) kodeTipe = "KUIS";

            Ujian ujian = new Ujian(
                idUjian, txtNama.getText(), kodeTipe,
                DateUtil.parse(txtTgl.getText()), 
                Integer.parseInt(txtDurasi.getText()),
                Integer.parseInt(txtWaktuPerSoal.getText()),
                Integer.parseInt(txtMaxSoal.getText())
            );
            ujian.setGuru(guru); ujian.setMapel(mapel); ujian.setKelas(kelas);

            ujianRepo.addUjian(ujian);
            dispose();
            kelolaSoal(ujian); 
            parentPanel.refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format input salah: " + ex.getMessage());
        }
    }

    private void kelolaSoal(Ujian u) {
        JDialog d = new JDialog(this, "Input Soal (" + u.getTipeUjian() + ")", true);
        d.setSize(600, 700);
        d.setLayout(new BorderLayout(10, 10));
        d.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JComboBox<String> comboJenisSoal = new JComboBox<>(new String[]{"PG", "ESSAY"});
        
        if(u.getTipeUjian().equals("PG") || u.getTipeUjian().equals("KUIS")) {
            comboJenisSoal.setSelectedItem("PG"); comboJenisSoal.setEnabled(false);
        } else if (u.getTipeUjian().equals("ESSAY")) {
            comboJenisSoal.setSelectedItem("ESSAY"); comboJenisSoal.setEnabled(false);
        }

        JTextField txtTanya = new JTextField();
        JTextField txtA = new JTextField();
        JTextField txtB = new JTextField();
        JTextField txtC = new JTextField();
        JTextField txtD = new JTextField();
        
        JPanel panelKunci = new JPanel(new CardLayout());
        JComboBox<String> comboKunciPG = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        JTextField txtKunciEssay = new JTextField();
        panelKunci.add(comboKunciPG, "PG");
        panelKunci.add(txtKunciEssay, "ESSAY");
        CardLayout cl = (CardLayout)(panelKunci.getLayout());

        comboJenisSoal.addActionListener(e -> {
            boolean isPG = comboJenisSoal.getSelectedItem().equals("PG");
            txtA.setEnabled(isPG); txtB.setEnabled(isPG);
            txtC.setEnabled(isPG); txtD.setEnabled(isPG);
            cl.show(panelKunci, isPG ? "PG" : "ESSAY");
        });
        
        if(u.getTipeUjian().equals("ESSAY")) {
            txtA.setEnabled(false); txtB.setEnabled(false); 
            txtC.setEnabled(false); txtD.setEnabled(false);
            cl.show(panelKunci, "ESSAY");
        }

        JLabel lblPreview = new JLabel("Tidak ada gambar");
        JButton btnUpload = new JButton("Upload Gambar");
        btnUpload.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(d) == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fc.getSelectedFile();
                lblPreview.setText(selectedImageFile.getName());
            }
        });

        inputPanel.add(new JLabel("Tipe Soal:")); inputPanel.add(comboJenisSoal);
        inputPanel.add(new JLabel("Gambar (Opsional):")); 
        JPanel pImage = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pImage.add(btnUpload);
        pImage.add(lblPreview);
        inputPanel.add(pImage);

        inputPanel.add(new JLabel("Pertanyaan:")); inputPanel.add(txtTanya);
        inputPanel.add(new JLabel("Opsi A:")); inputPanel.add(txtA);
        inputPanel.add(new JLabel("Opsi B:")); inputPanel.add(txtB);
        inputPanel.add(new JLabel("Opsi C:")); inputPanel.add(txtC);
        inputPanel.add(new JLabel("Opsi D:")); inputPanel.add(txtD);
        inputPanel.add(new JLabel("Kunci Jawaban:")); inputPanel.add(panelKunci);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblInfo = new JLabel("Soal tersimpan: " + soalRepo.getByUjian(u.getIdUjian()).size() + " / " + u.getMaxSoal());
        
        JButton btnAdd = new JButton("Simpan Soal");
        JButton btnSelesai = new JButton("Selesai / Tutup"); // Tombol baru
        
        btnAdd.addActionListener(e -> simpanSoal(u, d, lblInfo, lblPreview, comboJenisSoal, comboKunciPG, txtKunciEssay, txtTanya, txtA, txtB, txtC, txtD, btnAdd));
        
        btnSelesai.addActionListener(e -> d.dispose()); // Tutup dialog hanya lewat tombol ini atau X

        d.add(inputPanel, BorderLayout.CENTER);
        
        bottom.add(lblInfo); 
        bottom.add(btnAdd);
        bottom.add(btnSelesai);
        
        d.add(bottom, BorderLayout.SOUTH);
        
        if (soalRepo.getByUjian(u.getIdUjian()).size() >= u.getMaxSoal()) {
            btnAdd.setEnabled(false);
            btnAdd.setText("Batas Tercapai");
        }

        d.setVisible(true);
    }
    
    private void simpanSoal(Ujian u, JDialog d, JLabel lblInfo, JLabel lblPreview, JComboBox<String> comboJenisSoal, JComboBox<String> comboKunciPG, JTextField txtKunciEssay, JTextField txtTanya, JTextField txtA, JTextField txtB, JTextField txtC, JTextField txtD, JButton btnAdd) {
        int currentCount = soalRepo.getByUjian(u.getIdUjian()).size();
        
        if (currentCount >= u.getMaxSoal()) {
            JOptionPane.showMessageDialog(d, "Maksimal soal tercapai!");
            btnAdd.setEnabled(false);
            return;
        }
        
        if(txtTanya.getText().isBlank()) {
            JOptionPane.showMessageDialog(d, "Pertanyaan tidak boleh kosong!");
            return;
        }
        
        String tipe = (String) comboJenisSoal.getSelectedItem();
        String kunci = tipe.equals("PG") ? (String) comboKunciPG.getSelectedItem() : txtKunciEssay.getText();
        String namaGambar = null;

        if (selectedImageFile != null) {
            try {
                File folder = new File("data/storage/soal_images/");
                if (!folder.exists()) folder.mkdirs();
                
                String ext = "";
                int i = selectedImageFile.getName().lastIndexOf('.');
                if (i > 0) ext = selectedImageFile.getName().substring(i);
                
                namaGambar = IdUtil.generate() + ext;
                Files.copy(selectedImageFile.toPath(), new File(folder, namaGambar).toPath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        Soal s = new Soal(IdUtil.generate(), u.getIdUjian(), tipe,
                          txtTanya.getText(), txtA.getText(), txtB.getText(), txtC.getText(), txtD.getText(), 
                          kunci, namaGambar);
        soalRepo.addSoal(s);
        
        txtTanya.setText(""); txtA.setText(""); txtB.setText(""); txtC.setText(""); txtD.setText(""); txtKunciEssay.setText("");
        selectedImageFile = null;
        lblPreview.setText("Tidak ada gambar");
        
        int newCount = currentCount + 1;
        lblInfo.setText("Soal tersimpan: " + newCount + " / " + u.getMaxSoal());
        
        if (newCount >= u.getMaxSoal()) {
            btnAdd.setEnabled(false);
            btnAdd.setText("Batas Tercapai");
            JOptionPane.showMessageDialog(d, "Soal tersimpan! Anda telah mencapai batas maksimal soal (" + u.getMaxSoal() + ").");
        } else {
            JOptionPane.showMessageDialog(d, "Soal tersimpan!");
        }
    }
}