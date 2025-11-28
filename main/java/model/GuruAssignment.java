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
}
