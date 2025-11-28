package model;

import java.time.LocalDateTime;

public class ForumDiskusi {
    private String idPesan;
    private User pengirim;
    private String isiPesan;
    private LocalDateTime waktu;

    public ForumDiskusi(String id, User u, String isi) {
        this.idPesan = id;
        this.pengirim = u;
        this.isiPesan = isi;
        this.waktu = LocalDateTime.now();
    }

    public String getIsiPesan() { return isiPesan; }
}
