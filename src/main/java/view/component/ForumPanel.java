package view.component;

import model.*;
import repository.ForumRepository;
import utils.IdUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ForumPanel extends JPanel {
    
    private User currentUser;
    private Kelas kelas;
    private MataPelajaran mapel;
    private ForumRepository forumRepo;
    
    private CardLayout cardLayout;
    private JPanel mainPanel; 
    
    private JTable tableTopic;
    private DefaultTableModel tableModel;
    
    // Detail Components
    private JLabel lblJudulTopik;
    private JTextArea txtDiskusiArea;
    private JTextField txtReply;
    private ForumThread currentThread; // Berubah dari ForumDiskusi ke ForumThread

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
        // Sembunyikan kolom ID
        tableTopic.getColumnModel().getColumn(0).setMinWidth(0);
        tableTopic.getColumnModel().getColumn(0).setMaxWidth(0);
        tableTopic.getColumnModel().getColumn(0).setWidth(0);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton btnCreate = new JButton("Buat Topik Baru");
        JButton btnOpen = new JButton("Buka Diskusi");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete = new JButton("Hapus Topik");
        
        btnDelete.setBackground(new Color(255, 150, 150));
        
        btnPanel.add(btnCreate);
        btnPanel.add(btnOpen);
        if (currentUser instanceof Guru || currentUser instanceof Admin) {
            btnPanel.add(btnDelete);
        }
        btnPanel.add(btnRefresh);
        
        // Listeners
        btnCreate.addActionListener(e -> actionCreateTopic());
        btnRefresh.addActionListener(e -> loadTopics());
        
        btnOpen.addActionListener(e -> {
            int row = tableTopic.getSelectedRow();
            if (row != -1) {
                String idThread = (String) tableModel.getValueAt(row, 0);
                String judul = (String) tableModel.getValueAt(row, 1);
                // Kita ambil object lengkap nanti di openTopic
                openTopic(idThread, judul);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih topik dulu!");
            }
        });
        
        btnDelete.addActionListener(e -> {
            int row = tableTopic.getSelectedRow();
            if (row == -1) return;
            String idThread = (String) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Hapus topik ini beserta balasannya?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                forumRepo.deleteThread(idThread);
                loadTopics();
            }
        });
        
        panel.add(new JScrollPane(tableTopic), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createDetailPanel() {
        JPanel detailPanel = new JPanel(new BorderLayout());
        
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
        // Mengambil data dari Tabel Header (ForumThread)
        List<ForumThread> threads = forumRepo.getThreadsByKelasMapel(kelas, mapel);
        
        for (ForumThread t : threads) {
            // Hitung jumlah balasan dari Tabel Detail (ForumReply)
            int replyCount = forumRepo.countReplies(t.getIdThread());
            String sender = (t.getPengirim() != null) ? t.getPengirim().getNamaLengkap() : "Unknown";
            
            tableModel.addRow(new Object[]{
                t.getIdThread(), 
                t.getJudul(), 
                sender, 
                t.getWaktu(), 
                replyCount + " balasan"
            });
        }
    }
    
    private void openTopic(String idThread, String judul) {
        // Cari object thread lengkap (bisa dioptimasi dengan caching list, tapi find lagi juga oke)
        this.currentThread = forumRepo.getThreadsByKelasMapel(kelas, mapel).stream()
                .filter(t -> t.getIdThread().equals(idThread))
                .findFirst().orElse(null);
                
        if (currentThread == null) return;
        
        lblJudulTopik.setText(currentThread.getJudul());
        refreshDetailArea();
        
        cardLayout.show(mainPanel, "DETAIL");
    }
    
    private void refreshDetailArea() {
        if (currentThread == null) return;
        txtDiskusiArea.setText("");
        
        // Tampilkan Postingan Utama (Header)
        appendHeader(currentThread);
        txtDiskusiArea.append("--------------------------------------------------\n");
        
        // Tampilkan Balasan (Detail)
        List<ForumReply> replies = forumRepo.getRepliesByThread(currentThread.getIdThread());
        for (ForumReply r : replies) {
            appendReply(r);
            txtDiskusiArea.append("\n");
        }
    }
    
    private void appendHeader(ForumThread t) {
        String sender = (t.getPengirim() != null) ? t.getPengirim().getNamaLengkap() : "User Terhapus";
        String role = (t.getPengirim() instanceof Guru) ? "[GURU]" : "[SISWA]";
        txtDiskusiArea.append(role + " " + sender + " (" + t.getWaktu() + ") - POST UTAMA\n");
        txtDiskusiArea.append(t.getIsiUtama() + "\n");
    }

    private void appendReply(ForumReply r) {
        String sender = (r.getPenjawab() != null) ? r.getPenjawab().getNamaLengkap() : "User Terhapus";
        String role = (r.getPenjawab() instanceof Guru) ? "[GURU]" : "[SISWA]";
        txtDiskusiArea.append(role + " " + sender + " (" + r.getWaktu() + ")\n");
        txtDiskusiArea.append(r.getIsiReply() + "\n");
    }
    
    private void actionCreateTopic() {
        String judul = JOptionPane.showInputDialog(this, "Masukkan Judul Topik:");
        if (judul == null || judul.isBlank()) return;
        
        String isi = JOptionPane.showInputDialog(this, "Isi Postingan Pertama:");
        if (isi == null || isi.isBlank()) return;
        
        ForumThread t = new ForumThread(IdUtil.generate(), currentUser, judul, isi, kelas, mapel);
        forumRepo.createThread(t);
        
        loadTopics();
        JOptionPane.showMessageDialog(this, "Topik berhasil dibuat!");
    }
    
    private void actionReply() {
        String isi = txtReply.getText();
        if (isi.isBlank()) return;
        
        ForumReply r = new ForumReply(IdUtil.generate(), currentThread.getIdThread(), currentUser, isi);
        forumRepo.addReply(r);
        
        txtReply.setText("");
        refreshDetailArea(); 
    }
}