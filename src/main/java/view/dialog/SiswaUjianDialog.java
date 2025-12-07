package view.dialog;

import model.*;
import repository.*;
import service.UjianEvaluationService;
import utils.IdUtil;
import utils.UjianHelper;
import view.panel.SiswaTugasUjianPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SiswaUjianDialog extends JDialog {

    private final Siswa siswa;
    private final Ujian ujian;
    private final SoalRepository soalRepo;
    private final NilaiRepository nilaiRepo;
    private final JawabanRepository jawabanRepo;
    private final UjianProgressRepository progressRepo;
    private final SiswaTugasUjianPanel parentPanel;

    private List<Object> inputComponents;
    private List<Soal> soalList;
    private UjianProgress currentProgress;
    
    private int currentSoalIndex = 0;
    private int timeLeft = 0;
    private Timer timer;
    private boolean isSubmitted = false;
    
    private final int MAX_VIOLATIONS = 3;

    public SiswaUjianDialog(JFrame parent, Siswa s, Ujian u, SoalRepository sr, NilaiRepository nr, JawabanRepository jr, SiswaTugasUjianPanel p) {
        super(parent, u.getTipeUjian() + ": " + u.getNamaUjian(), true);
        this.siswa = s;
        this.ujian = u;
        this.soalRepo = sr;
        this.nilaiRepo = nr;
        this.jawabanRepo = jr;
        this.parentPanel = p;
        this.progressRepo = new UjianProgressRepository();

        this.soalList = soalRepo.getByUjian(u.getIdUjian());
        if (soalList.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Soal tidak tersedia.");
            return;
        }

        loadOrInitProgress();
        initUI();
        initSecurity();
        
        setSize(1000, 750);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveProgress();
                int confirm = JOptionPane.showConfirmDialog(SiswaUjianDialog.this, 
                    "Keluar akan menyimpan progress tapi waktu terus berjalan di server (jika online). Yakin?", 
                    "Pause Ujian", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (timer != null) timer.stop();
                    dispose();
                }
            }
        });
        
        setVisible(true);
    }

    private void loadOrInitProgress() {
        currentProgress = progressRepo.getProgress(siswa.getIdUser(), ujian.getIdUjian());
        
        if (currentProgress == null) {
            int duration = "KUIS".equals(ujian.getTipeUjian()) ? ujian.getWaktuPerSoal() : ujian.getDurasiTotal() * 60;
            currentProgress = new UjianProgress(IdUtil.generate(), siswa.getIdUser(), ujian.getIdUjian(), duration);
            
            soalList = UjianHelper.randomizeSoal(soalList, ujian.getMaxSoal());
            soalList = soalList.stream().map(UjianHelper::randomizeOptions).collect(Collectors.toList());
            
            progressRepo.saveOrUpdate(currentProgress);
        } else {
            this.currentSoalIndex = currentProgress.getCurrentIndex();
            this.timeLeft = currentProgress.getSisaWaktu();
            
            if (this.currentSoalIndex >= soalList.size()) this.currentSoalIndex = 0;
        }
        
        this.timeLeft = currentProgress.getSisaWaktu();
        if ("KUIS".equals(ujian.getTipeUjian())) {
             // Logic khusus Kuis per soal
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
        JLabel lblTimer = new JLabel("Waktu: --:--");
        lblTimer.setFont(new Font("Arial", Font.BOLD, 16));
        lblTimer.setForeground(Color.RED);
        topBar.add(new JLabel(" " + ujian.getNamaUjian()), BorderLayout.WEST);
        topBar.add(lblTimer, BorderLayout.EAST);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnNext = new JButton("Selanjutnya");
        JButton btnPrev = new JButton("Sebelumnya");
        JButton btnSubmit = new JButton("Selesai");
        btnSubmit.setBackground(new Color(50, 200, 50));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setVisible(false);

        JPanel navPanel = new JPanel(new FlowLayout());
        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        navPanel.add(btnSubmit);

        add(topBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);

        cardLayout.show(cardPanel, "SOAL_" + currentSoalIndex);
        updateNavButtons(btnPrev, btnNext, btnSubmit);

        // Restore Answers
        Map<Integer, String> savedAnswers = currentProgress.getJawabanSementara();
        for (int i = 0; i < inputComponents.size(); i++) {
            if (savedAnswers.containsKey(i)) {
                String ans = savedAnswers.get(i);
                Object comp = inputComponents.get(i);
                if (comp instanceof JTextArea area) {
                    area.setText(ans);
                } else if (comp instanceof ButtonGroup bg) {
                    java.util.Enumeration<AbstractButton> buttons = bg.getElements();
                    while(buttons.hasMoreElements()){
                        AbstractButton b = buttons.nextElement();
                        if(b.getActionCommand().equals(ans)) {
                            b.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }

        // Timer Logic
        timer = new Timer(1000, e -> {
            timeLeft--;
            currentProgress.setSisaWaktu(timeLeft);
            
            long min = timeLeft / 60;
            long sec = timeLeft % 60;
            lblTimer.setText(String.format("%02d:%02d", min, sec));

            if (timeLeft % 30 == 0) saveProgress(); // Auto save interval

            if (timeLeft <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Waktu Habis!");
                submitUjian();
            }
        });
        timer.start();

        btnNext.addActionListener(e -> {
            captureAnswer(currentSoalIndex);
            if (currentSoalIndex < soalList.size() - 1) {
                currentSoalIndex++;
                cardLayout.show(cardPanel, "SOAL_" + currentSoalIndex);
                updateNavButtons(btnPrev, btnNext, btnSubmit);
                currentProgress.setCurrentIndex(currentSoalIndex);
            }
        });

        btnPrev.addActionListener(e -> {
            captureAnswer(currentSoalIndex);
            if (currentSoalIndex > 0) {
                currentSoalIndex--;
                cardLayout.show(cardPanel, "SOAL_" + currentSoalIndex);
                updateNavButtons(btnPrev, btnNext, btnSubmit);
                currentProgress.setCurrentIndex(currentSoalIndex);
            }
        });

        btnSubmit.addActionListener(e -> {
            captureAnswer(currentSoalIndex);
            int cfm = JOptionPane.showConfirmDialog(this, "Yakin ingin mengumpulkan?", "Submit", JOptionPane.YES_NO_OPTION);
            if(cfm == JOptionPane.YES_OPTION) submitUjian();
        });
    }

    private void initSecurity() {
        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {}

            @Override
            public void windowLostFocus(WindowEvent e) {
                if (isSubmitted || !isDisplayable()) return;
                
                int v = currentProgress.getViolationCount() + 1;
                currentProgress.setViolationCount(v);
                progressRepo.saveOrUpdate(currentProgress);
                
                if (v >= MAX_VIOLATIONS) {
                    timer.stop();
                    JOptionPane.showMessageDialog(SiswaUjianDialog.this, 
                        "Terdeteksi kecurangan (pindah window) melebihi batas!\nUjian otomatis dikumpulkan.", 
                        "Pelanggaran", JOptionPane.ERROR_MESSAGE);
                    submitUjian();
                } else {
                    JOptionPane.showMessageDialog(SiswaUjianDialog.this, 
                        "Peringatan! Jangan pindah aplikasi.\nSisa toleransi: " + (MAX_VIOLATIONS - v), 
                        "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void captureAnswer(int index) {
        Object comp = inputComponents.get(index);
        String ans = "";
        if (comp instanceof JTextArea area) {
            ans = area.getText();
        } else if (comp instanceof ButtonGroup bg) {
            if (bg.getSelection() != null) {
                ans = bg.getSelection().getActionCommand();
            }
        }
        if (!ans.isEmpty()) {
            currentProgress.addJawaban(index, ans);
        }
    }

    private void saveProgress() {
        captureAnswer(currentSoalIndex);
        progressRepo.saveOrUpdate(currentProgress);
    }

    private void submitUjian() {
        isSubmitted = true;
        if (timer != null) timer.stop();
        saveProgress();

        int score = UjianEvaluationService.hitungNilai(soalList, inputComponents);
        String ket = (currentProgress.getViolationCount() >= MAX_VIOLATIONS) ? "Diskualifikasi/Auto-Submit" : "Selesai";
        
        Nilai n = new Nilai(IdUtil.generate(), siswa, ujian, score, ket);
        nilaiRepo.addNilai(n);
        
        Jawaban j = new Jawaban(IdUtil.generate(), siswa, ujian, "Digital (Skor: " + score + ")");
        jawabanRepo.addJawaban(j, null);
        
        progressRepo.deleteProgress(siswa.getIdUser(), ujian.getIdUjian());
        
        dispose();
        JOptionPane.showMessageDialog(getParent(), "Ujian Selesai. Nilai: " + score);
        parentPanel.refreshTable();
    }

    private void updateNavButtons(JButton prev, JButton next, JButton submit) {
        prev.setEnabled(currentSoalIndex > 0);
        next.setVisible(currentSoalIndex < soalList.size() - 1);
        submit.setVisible(currentSoalIndex == soalList.size() - 1);
    }

    private JPanel createSoalPanel(Soal s, int index) {
        JPanel pSoal = new JPanel(new BorderLayout(10, 10));
        pSoal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        if (s.getGambar() != null && !s.getGambar().isBlank()) {
            File imgFile = new File("data/storage/soal_images/" + s.getGambar());
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(new ImageIcon(imgFile.getAbsolutePath()).getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH));
                JLabel lblImg = new JLabel(icon);
                lblImg.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(lblImg);
            }
        }

        JTextArea txtTanya = new JTextArea("No " + (index + 1) + ". " + s.getPertanyaan());
        txtTanya.setLineWrap(true);
        txtTanya.setWrapStyleWord(true);
        txtTanya.setEditable(false);
        txtTanya.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtTanya.setBackground(getBackground());
        content.add(txtTanya);

        pSoal.add(new JScrollPane(content), BorderLayout.CENTER);

        JPanel pJawab = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if ("ESSAY".equals(s.getTipeSoal())) {
            JTextArea area = new JTextArea(5, 40);
            pJawab.add(new JScrollPane(area));
            inputComponents.add(area);
        } else {
            JPanel pGanda = new JPanel(new GridLayout(4, 1));
            ButtonGroup bg = new ButtonGroup();
            String[] labels = {"A. " + s.getPilA(), "B. " + s.getPilB(), "C. " + s.getPilC(), "D. " + s.getPilD()};
            String[] vals = {"A", "B", "C", "D"};
            
            for (int k = 0; k < 4; k++) {
                JRadioButton rb = new JRadioButton(labels[k]);
                rb.setActionCommand(vals[k]);
                rb.setFont(new Font("SansSerif", Font.PLAIN, 16));
                bg.add(rb);
                pGanda.add(rb);
            }
            pJawab.add(pGanda);
            inputComponents.add(bg);
        }
        pSoal.add(pJawab, BorderLayout.SOUTH);
        return pSoal;
    }
}