package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ForumReply {
    private String idReply;
    private String idThread;
    private User penjawab;
    private String isiReply;
    private String waktu;

    // Constructor Baru
    public ForumReply(String idReply, String idThread, User penjawab, String isi) {
        this.idReply = idReply;
        this.idThread = idThread;
        this.penjawab = penjawab;
        this.isiReply = isi;
        this.waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public ForumReply(String idReply, String idThread, User penjawab, String isi, String waktu) {
        this.idReply = idReply;
        this.idThread = idThread;
        this.penjawab = penjawab;
        this.isiReply = isi;
        this.waktu = waktu;
    }

    public String getIdReply() { return idReply; }
    public String getIdThread() { return idThread; }
    public User getPenjawab() { return penjawab; }
    public String getIsiReply() { return isiReply; }
    public String getWaktu() { return waktu; }
}