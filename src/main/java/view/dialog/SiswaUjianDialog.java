package view.dialog;

import model.*;
import repository.*;
import service.UjianService;
import view.panel.SiswaTugasUjianPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SiswaUjianDialog extends JDialog {

    private final Siswa siswa;
    private final Ujian ujian;
    private final UjianService ujianService;
    private final SiswaTugasUjianPanel parentPanel;

    private List<Object> inputComponents;
    private List<Soal> soalList;
    private UjianProgress currentProgress;
    
    private int currentSoalIndex = 0;
    private Timer timer;
    
    private final int MAX_VIOLATIONS = 3;
    private boolean isConfirming = false; 

    public SiswaUjianDialog(JFrame parent, Siswa s, Ujian u, SoalRepository sr, NilaiRepository nr, JawabanRepository jr, SiswaTugasUjianPanel p) {
        super(parent, u.getTipeUjian() + ": " + u.getNamaUjian(), true);
        this.siswa = s;
        this.ujian = u;
        this.parentPanel = p;
        
        this.ujianService = new UjianService(new UjianRepository(), new UjianProgressRepository(), sr, nr, jr);

        try {
            this.soalList = ujianService.getSoalUjian(u);
            if (soalList.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Soal tidak tersedia.");
                return;
            }

            this.currentProgress = ujianService.startOrContinueUjian(s, u);
            this.currentSoalIndex = currentProgress.getCurrentIndex();
            if (this.currentSoalIndex >= soalList.size()) this.currentSoalIndex = 0;

            initUI();
            initSecurity();
            
            setSize(1000, 750);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    saveCurrentAnswer();
                    ujianService.saveProgress(currentProgress);
                    
                    isConfirming = true;
                    int cfm = JOptionPane.showConfirmDialog(SiswaUjianDialog.this, 
                        "Keluar akan menyimpan progress tapi waktu terus berjalan di server (jika online). Yakin?", 
                        "Pause Ujian", JOptionPane.YES_NO_OPTION);
                    isConfirming = false;

                    if (cfm == JOptionPane.YES_OPTION) {
                        if (timer != null) timer.stop();
                        dispose();
                    }
                }
            });
            
            setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Terjadi kesalahan sistem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());
        inputComponents = new ArrayList<>();

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        for (int i = 0; i < soalList.size(); i++) {
            cardPanel.add(createSoalPanel(soalList.get(i), i), "SOAL_" + i);
        }

        JPanel topBar = new JPanel(new BorderLayout());
        JLabel lblTimer = new JLabel("--:--");
        lblTimer.setFont(new Font("Arial", Font.BOLD, 16));
        lblTimer.setForeground(Color.RED);
        topBar.add(new JLabel(" " + ujian.getNamaUjian()), BorderLayout.WEST);
        topBar.add(lblTimer, BorderLayout.EAST);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel navPanel = new JPanel();
        JButton btnPrev = new JButton("Sebelumnya");
        JButton btnNext = new JButton("Selanjutnya");
        JButton btnSubmit = new JButton("Selesai");
        btnSubmit.setBackground(new Color(50, 200, 50));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setVisible(false);

        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        navPanel.add(btnSubmit);

        add(topBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);

        cardLayout.show(cardPanel, "SOAL_" + currentSoalIndex);
        restoreAnswers();
        updateNavState(btnPrev, btnNext, btnSubmit);

        timer = new Timer(1000, e -> {
            int sisa = currentProgress.getSisaWaktu() - 1;
            currentProgress.setSisaWaktu(sisa);
            
            if (sisa <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Waktu Habis!");
                submit();
            } else {
                long m = sisa / 60;
                long sec = sisa % 60;
                lblTimer.setText(String.format("%02d:%02d", m, sec));
            }
        });
        timer.start();

        btnNext.addActionListener(e -> {
            saveCurrentAnswer();
            if (currentSoalIndex < soalList.size() - 1) {
                currentSoalIndex++;
                cardLayout.show(cardPanel, "SOAL_" + currentSoalIndex);
                currentProgress.setCurrentIndex(currentSoalIndex);
                updateNavState(btnPrev, btnNext, btnSubmit);
            }
        });

        btnPrev.addActionListener(e -> {
            saveCurrentAnswer();
            if (currentSoalIndex > 0) {
                currentSoalIndex--;
                cardLayout.show(cardPanel, "SOAL_" + currentSoalIndex);
                currentProgress.setCurrentIndex(currentSoalIndex);
                updateNavState(btnPrev, btnNext, btnSubmit);
            }
        });

        btnSubmit.addActionListener(e -> {
            saveCurrentAnswer();
            
            isConfirming = true;
            int result = JOptionPane.showConfirmDialog(this, "Yakin ingin mengumpulkan?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            isConfirming = false;

            if (result == JOptionPane.YES_OPTION) {
                submit();
            }
        });
    }

    private void initSecurity() {
        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {}

            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!isDisplayable() || isConfirming) return;
                
                int v = currentProgress.getViolationCount() + 1;
                currentProgress.setViolationCount(v);
                ujianService.saveProgress(currentProgress);
                
                if (v >= MAX_VIOLATIONS) {
                    timer.stop();
                    JOptionPane.showMessageDialog(SiswaUjianDialog.this, 
                        "Terdeteksi kecurangan (pindah window) melebihi batas!\nUjian otomatis dikumpulkan.", 
                        "Pelanggaran", JOptionPane.ERROR_MESSAGE);
                    submit();
                } else {
                    JOptionPane.showMessageDialog(SiswaUjianDialog.this, 
                        "Peringatan! Jangan pindah aplikasi.\nSisa toleransi: " + (MAX_VIOLATIONS - v), 
                        "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void restoreAnswers() {
        Map<Integer, String> ansMap = currentProgress.getJawabanSementara();
        for (int i = 0; i < inputComponents.size(); i++) {
            if (ansMap.containsKey(i)) {
                String val = ansMap.get(i);
                Object comp = inputComponents.get(i);
                if (comp instanceof JTextArea ta) ta.setText(val);
                else if (comp instanceof ButtonGroup bg) {
                    java.util.Enumeration<AbstractButton> abs = bg.getElements();
                    while(abs.hasMoreElements()) {
                        AbstractButton b = abs.nextElement();
                        if(b.getActionCommand().equals(val)) b.setSelected(true);
                    }
                }
            }
        }
    }

    private void saveCurrentAnswer() {
        Object comp = inputComponents.get(currentSoalIndex);
        String val = "";
        if (comp instanceof JTextArea ta) val = ta.getText();
        else if (comp instanceof ButtonGroup bg && bg.getSelection() != null) val = bg.getSelection().getActionCommand();
        
        if (!val.isEmpty()) currentProgress.addJawaban(currentSoalIndex, val);
    }

    private void submit() {
        if (timer != null) timer.stop();
        ujianService.submitUjian(siswa, ujian, soalList, inputComponents);
        dispose();
        JOptionPane.showMessageDialog(getParent(), "Ujian selesai dikumpulkan.");
        parentPanel.refreshTable();
    }

    private void updateNavState(JButton prev, JButton next, JButton submit) {
        prev.setEnabled(currentSoalIndex > 0);
        next.setVisible(currentSoalIndex < soalList.size() - 1);
        submit.setVisible(currentSoalIndex == soalList.size() - 1);
    }

    private JPanel createSoalPanel(Soal s, int index) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        if (s.getGambar() != null && !s.getGambar().isBlank()) {
            File f = new File("data/storage/soal_images/" + s.getGambar());
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(new ImageIcon(f.getAbsolutePath()).getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH));
                content.add(new JLabel(icon));
            }
        }
        
        JTextArea txt = new JTextArea((index + 1) + ". " + s.getPertanyaan());
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setEditable(false);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txt.setBackground(getBackground());
        content.add(txt);
        p.add(new JScrollPane(content), BorderLayout.CENTER);

        JPanel pJawab = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if ("ESSAY".equals(s.getTipeSoal())) {
            JTextArea area = new JTextArea(5, 40);
            pJawab.add(new JScrollPane(area));
            inputComponents.add(area);
        } else {
            JPanel grid = new JPanel(new GridLayout(4, 1));
            ButtonGroup bg = new ButtonGroup();
            String[] lbls = {s.getPilA(), s.getPilB(), s.getPilC(), s.getPilD()};
            String[] vals = {"A", "B", "C", "D"};
            for (int k = 0; k < 4; k++) {
                JRadioButton rb = new JRadioButton(vals[k] + ". " + lbls[k]);
                rb.setActionCommand(vals[k]);
                bg.add(rb);
                grid.add(rb);
            }
            pJawab.add(grid);
            inputComponents.add(bg);
        }
        p.add(pJawab, BorderLayout.SOUTH);
        return p;
    }
}