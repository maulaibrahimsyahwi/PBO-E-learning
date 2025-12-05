package view.component;

import model.*;
import repository.ForumRepository;
import utils.IdUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ForumPanel extends JPanel {
    
    private User currentUser;
    private Kelas kelas;
    private MataPelajaran mapel;
    private ForumRepository forumRepo;
    
    private CardLayout cardLayout;
    private JPanel mainPanel; 
    
    private JTable tableTopic;
    private DefaultTableModel tableModel;
    
    private JPanel detailPanel;
    private JLabel lblJudulTopik;
    private JTextArea txtDiskusiArea;
    private JTextField txtReply;
    private ForumDiskusi currentTopic; 

    public ForumPanel(User user, Kelas k, MataPelajaran m, ForumRepository repo) {
        this.currentUser = user;
        this.kelas = k;
        this.mapel = m;
        this.forumRepo = repo;
        
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createTopicListPanel(), "LIST");
        mainPanel.add(createDetailPanel(), "DETAIL");
        
        add(mainPanel, BorderLayout.CENTER);
        
        loadTopics(); 
    }
    
    private JPanel createTopicListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] col = {"ID", "Judul Topik", "Dimulai Oleh", "Waktu", "Balasan"};
        tableModel = new DefaultTableModel(col, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableTopic = new JTable(tableModel);
        
        // --- PERBAIKAN TATA LETAK TOMBOL FORUM ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton btnCreate = new JButton("Buat Topik Baru");
        JButton btnOpen = new JButton("Buka Diskusi");
        JButton btnRefresh = new JButton("Refresh");
        
        Dimension btnSize = new Dimension(130, 35);
        btnCreate.setPreferredSize(new Dimension(140, 35));
        btnOpen.setPreferredSize(btnSize);
        btnRefresh.setPreferredSize(new Dimension(100, 35));
        
        btnPanel.add(btnCreate);
        btnPanel.add(btnOpen);
        btnPanel.add(btnRefresh);
        
        btnCreate.addActionListener(e -> actionCreateTopic());
        
        btnOpen.addActionListener(e -> {
            int row = tableTopic.getSelectedRow();
            if (row != -1) {
                String idTopic = (String) tableModel.getValueAt(row, 0);
                openTopic(idTopic);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih topik dulu!");
            }
        });
        
        btnRefresh.addActionListener(e -> loadTopics());
        
        panel.add(new JScrollPane(tableTopic), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createDetailPanel() {
        detailPanel = new JPanel(new BorderLayout());
        
        JPanel header = new JPanel(new BorderLayout());
        JButton btnBack = new JButton("<< Kembali");
        lblJudulTopik = new JLabel("Judul Topik", SwingConstants.CENTER);
        lblJudulTopik.setFont(new Font("Arial", Font.BOLD, 16));
        
        header.add(btnBack, BorderLayout.WEST);
        header.add(lblJudulTopik, BorderLayout.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        txtDiskusiArea = new JTextArea();
        txtDiskusiArea.setEditable(false);
        txtDiskusiArea.setMargin(new Insets(10, 10, 10, 10));
        txtDiskusiArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JPanel replyPanel = new JPanel(new BorderLayout(5, 5));
        replyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        txtReply = new JTextField();
        JButton btnSend = new JButton("Kirim Balasan");
        
        replyPanel.add(new JLabel("Balas:"), BorderLayout.WEST);
        replyPanel.add(txtReply, BorderLayout.CENTER);
        replyPanel.add(btnSend, BorderLayout.EAST);
        
        detailPanel.add(header, BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(txtDiskusiArea), BorderLayout.CENTER);
        detailPanel.add(replyPanel, BorderLayout.SOUTH);
        
        btnBack.addActionListener(e -> {
            cardLayout.show(mainPanel, "LIST");
            loadTopics(); 
        });
        
        btnSend.addActionListener(e -> actionReply());
        
        return detailPanel;
    }
    
    private void loadTopics() {
        tableModel.setRowCount(0);
        List<ForumDiskusi> allData = forumRepo.getByMapelAndKelas(mapel, kelas);
        
        List<ForumDiskusi> topics = allData.stream()
                .filter(ForumDiskusi::isTopic)
                .collect(Collectors.toList());
                
        for (ForumDiskusi t : topics) {
            long replyCount = allData.stream()
                    .filter(f -> f.getParentId().equals(t.getIdPesan()))
                    .count();
            
            String sender = (t.getPengirim() != null) ? t.getPengirim().getNamaLengkap() : "Unknown";
            
            tableModel.addRow(new Object[]{
                t.getIdPesan(), 
                t.getJudul(), 
                sender, 
                t.getWaktu(), 
                replyCount + " balasan"
            });
        }
    }
    
    private void openTopic(String idTopic) {
        this.currentTopic = forumRepo.getAll().stream()
                .filter(f -> f.getIdPesan().equals(idTopic))
                .findFirst().orElse(null);
                
        if (currentTopic == null) return;
        
        lblJudulTopik.setText(currentTopic.getJudul());
        refreshDetailArea();
        
        cardLayout.show(mainPanel, "DETAIL");
    }
    
    private void refreshDetailArea() {
        if (currentTopic == null) return;
        txtDiskusiArea.setText("");
        
        appendPost(currentTopic);
        txtDiskusiArea.append("--------------------------------------------------\n");
        
        List<ForumDiskusi> replies = forumRepo.getReplies(currentTopic.getIdPesan());
        for (ForumDiskusi r : replies) {
            appendPost(r);
            txtDiskusiArea.append("\n");
        }
    }
    
    private void appendPost(ForumDiskusi f) {
        String sender = (f.getPengirim() != null) ? f.getPengirim().getNamaLengkap() : "User Terhapus";
        String role = (f.getPengirim() instanceof Guru) ? "[GURU]" : "[SISWA]";
        
        txtDiskusiArea.append(role + " " + sender + " (" + f.getWaktu() + ")\n");
        txtDiskusiArea.append(f.getIsiPesan() + "\n");
    }
    
    private void actionCreateTopic() {
        String judul = JOptionPane.showInputDialog(this, "Masukkan Judul Topik:");
        if (judul == null || judul.isBlank()) return;
        
        String isi = JOptionPane.showInputDialog(this, "Isi Postingan Pertama:");
        if (isi == null || isi.isBlank()) return;
        
        ForumDiskusi fd = new ForumDiskusi(IdUtil.generate(), currentUser, judul, isi, kelas, mapel);
        forumRepo.addPesan(fd);
        loadTopics();
        JOptionPane.showMessageDialog(this, "Topik berhasil dibuat!");
    }
    
    private void actionReply() {
        String isi = txtReply.getText();
        if (isi.isBlank()) return;
        
        ForumDiskusi reply = new ForumDiskusi(IdUtil.generate(), currentUser, isi, kelas, mapel, currentTopic.getIdPesan());
        forumRepo.addPesan(reply);
        
        txtReply.setText("");
        refreshDetailArea(); 
    }
}