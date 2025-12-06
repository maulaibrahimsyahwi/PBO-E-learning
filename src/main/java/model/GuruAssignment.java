package model;

public class GuruAssignment {
    private String idAssign;
    private Guru guru;
    private MataPelajaran mapel;

    public GuruAssignment(String idAssign, Guru guru, MataPelajaran mapel) {
        this.idAssign = idAssign;
        this.guru = guru;
        this.mapel = mapel;
    }
    public String getIdAssign() { return idAssign; }
    public Guru getGuru() { return guru; }
    public MataPelajaran getMapel() { return mapel; }
}
