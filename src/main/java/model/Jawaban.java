package model;

public class Jawaban {

    private String idJawaban;
    private Siswa siswa;
    private Tugas tugas;
    private String fileJawaban;
    private String tanggalSubmit;

    public Jawaban(String idJawaban, Siswa siswa, Tugas tugas, String fileJawaban) {
        this.idJawaban = idJawaban;
        this.siswa = siswa;
        this.tugas = tugas;
        this.fileJawaban = fileJawaban;
        this.tanggalSubmit = java.time.LocalDate.now().toString(); // otomatis isi tanggal
    }

    // Tambahkan constructor untuk LOAD dari file
    public Jawaban(String idJawaban, String tanggalSubmit) {
        this.idJawaban = idJawaban;
        this.tanggalSubmit = tanggalSubmit;
    }

    public String getIdJawaban() {
        return idJawaban;
    }

    public Siswa getSiswa() {
        return siswa;
    }

    public Tugas getTugas() {
        return tugas;
    }

    public String getFileJawaban() {
        return fileJawaban;
    }

    public String getTanggalSubmit() {
        return tanggalSubmit;
    }

    public void setSiswa(Siswa siswa) {
        this.siswa = siswa;
    }

    public void setTugas(Tugas tugas) {
        this.tugas = tugas;
    }
}
