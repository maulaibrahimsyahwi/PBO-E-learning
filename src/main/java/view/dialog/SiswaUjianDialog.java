package view.dialog;

import model.Jawaban;
import model.Nilai;
import model.Siswa;
import model.Ujian;
import repository.JawabanRepository;
import repository.NilaiRepository;
import repository.SoalRepository;
import service.UjianEvaluationService;
import utils.IdUtil;
import view.panel.SiswaTugasUjianPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SiswaUjianDialog extends JDialog {
    
    private final Siswa siswa;
    private final Ujian ujian;
    private final SoalRepository soalRepo;
    private final NilaiRepository nilaiRepo;
    private final JawabanRepository jawabanRepo;
    private final SiswaTugasUjianPanel parentPanel; 
    
    private List<Object> inputComponents;
    private final List<model.Soal> soalList;
    private final int[] currentSoalIndex = {0};
    private final int[] timeLeft = {0}; 
    private Timer timer;
    private final Runnable submitAction;

    public SiswaUjianDialog(JFrame parent, Siswa s, Ujian u, SoalRepository sr, NilaiRepository nr, JawabanRepository jr, SiswaTugasUjianPanel p) {
        super(parent, u.getTipeUjian() + ": " + u.getNamaUjian(), true);
        
        // 1. Initialize all primary final fields
        this.siswa = s;
        this.ujian = u;
        this.soalRepo = sr;
        this.nilaiRepo = nr;
        this.jawabanRepo = jr;
        this.parentPanel = p;
        
        // 2. Initialize secondary final fields
        this.soalList = soalRepo.getByUjian(u.getIdUjian());

        // 3. Initialize the final Runnable (must be initialized before any return)
        this.submitAction = () -> {
            int score = UjianEvaluationService.hitungNilai(soalList, inputComponents);
            
            Nilai n = new Nilai(IdUtil.generate(), siswa, ujian, score, "Selesai");
            nilaiRepo.addNilai(n);
            siswa.tambahNilai(n);
            
            Jawaban j = new Jawaban(IdUtil.generate(), siswa, ujian, "Digital (Skor: "+score+")");
            jawabanRepo.addJawaban(j, null);

            dispose();
            JOptionPane.showMessageDialog(getParent(), "Ujian Selesai! Nilai Anda: " + score);
            parentPanel.refreshTable();
        };

        if (soalList.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Soal belum tersedia untuk ujian ini.");
            return;
        }

        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        inputComponents = new ArrayList<>();
        
        initUI();
        
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirm = JOptionPane.showConfirmDialog(SiswaUjianDialog.this, 
                    "Jika keluar sekarang, jawaban akan dikumpulkan apa adanya. Yakin?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (timer != null) timer.stop();
                    submitAction.run();
                }
            }
        });
        
        if (!soalList.isEmpty()) {
            setVisible(true);
        }
    }
    
    private void initUI() {
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        
        JLabel lblTimer = new JLabel("Waktu: -");
        lblTimer.setFont(new Font("Arial", Font.BOLD, 14));
        lblTimer.setForeground(Color.RED);
        
        JButton btnFinish = new JButton("Selesai & Kumpulkan");
        btnFinish.setBackground(new Color(50, 200, 50));
        btnFinish.setForeground(Color.WHITE);
        btnFinish.setVisible(false);
        btnFinish.addActionListener(e -> submitAction.run());

        JButton btnNext = new JButton("Selanjutnya >");
        btnNext.addActionListener(e -> nextSoal(cardLayout, cardPanel, btnNext, btnFinish));

        for (int i = 0; i < soalList.size(); i++) {
            model.Soal s = soalList.get(i);
            JPanel pSoal = createSoalPanel(s, i);
            cardPanel.add(pSoal, "SOAL_" + i);
        }

        JPanel navPanel = new JPanel(new FlowLayout());
        navPanel.add(btnNext);
        navPanel.add(btnFinish);

        JPanel topBar = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel(" " + ujian.getNamaUjian() + " (" + ujian.getTipeUjian() + ")");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(lblTimer, BorderLayout.EAST);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(topBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);
        
        initTimerAndListeners(lblTimer, btnNext, btnFinish, cardLayout, cardPanel);
    }
    
    private void nextSoal(CardLayout cl, JPanel cardPanel, JButton btnNext, JButton btnFinish) {
        if (currentSoalIndex[0] < soalList.size() - 1) {
            currentSoalIndex[0]++;
            cl.show(cardPanel, "SOAL_" + currentSoalIndex[0]);
            
            if ("KUIS".equals(ujian.getTipeUjian())) {
                timeLeft[0] = ujian.getWaktuPerSoal(); 
            }
            
            if (currentSoalIndex[0] == soalList.size() - 1) {
                btnNext.setVisible(false);
                btnFinish.setVisible(true);
            }
        }
    }
    
    private void initTimerAndListeners(JLabel lblTimer, JButton btnNext, JButton btnFinish, CardLayout cl, JPanel cardPanel) {
        if ("KUIS".equals(ujian.getTipeUjian())) {
            timeLeft[0] = ujian.getWaktuPerSoal();
            timer = new Timer(1000, e -> {
                timeLeft[0]--;
                lblTimer.setText("Sisa Waktu Soal: " + timeLeft[0] + " detik");
                if (timeLeft[0] <= 0) {
                    if (currentSoalIndex[0] < soalList.size() - 1) {
                        nextSoal(cl, cardPanel, btnNext, btnFinish);
                        timeLeft[0] = ujian.getWaktuPerSoal();
                    } else { 
                        if (timer != null) timer.stop();
                        JOptionPane.showMessageDialog(this, "Waktu Habis!");
                        submitAction.run(); 
                    }
                }
            });
        } else {
            timeLeft[0] = ujian.getDurasiTotal() * 60;
            timer = new Timer(1000, e -> {
                timeLeft[0]--;
                long min = timeLeft[0] / 60;
                long sec = timeLeft[0] % 60;
                lblTimer.setText(String.format("Sisa Waktu: %02d:%02d", min, sec));
                
                if (timeLeft[0] <= 0) {
                    if (timer != null) timer.stop();
                    JOptionPane.showMessageDialog(this, "Waktu Ujian Habis! Jawaban otomatis dikumpulkan.");
                    submitAction.run();
                }
            });
        }
        if (timer != null) timer.start();
    }
    
    private JPanel createSoalPanel(model.Soal s, int index) {
        JPanel pSoal = new JPanel(new BorderLayout(10, 10));
        pSoal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea txtTanya = new JTextArea("No " + (index + 1) + ".\n" + s.getPertanyaan());
        txtTanya.setWrapStyleWord(true); 
        txtTanya.setLineWrap(true);
        txtTanya.setEditable(false); 
        txtTanya.setFont(new Font("SansSerif", Font.BOLD, 16));
        txtTanya.setBackground(new Color(240, 240, 240));
        pSoal.add(new JScrollPane(txtTanya), BorderLayout.NORTH);

        JPanel pJawab = new JPanel();
        
        if ("ESSAY".equals(s.getTipeSoal())) {
            JTextArea txtJawab = new JTextArea(10, 20);
            pJawab.setLayout(new BorderLayout());
            pJawab.add(new JLabel("Jawaban Anda:"), BorderLayout.NORTH);
            pJawab.add(new JScrollPane(txtJawab), BorderLayout.CENTER);
            inputComponents.add(txtJawab);
        } else {
            pJawab.setLayout(new GridLayout(4, 1, 5, 5));
            ButtonGroup bg = new ButtonGroup();
            String[] ops = {s.getPilA(), s.getPilB(), s.getPilC(), s.getPilD()};
            String[] keys = {"A", "B", "C", "D"};
            
            for(int k=0; k<4; k++) {
                JRadioButton rb = new JRadioButton(keys[k] + ". " + ops[k]);
                rb.setActionCommand(keys[k]);
                rb.setFont(new Font("SansSerif", Font.PLAIN, 14));
                bg.add(rb); 
                pJawab.add(rb);
            }
            inputComponents.add(bg);
        }
        pSoal.add(pJawab, BorderLayout.CENTER);
        return pSoal;
    }
}