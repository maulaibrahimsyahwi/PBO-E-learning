package model;

public class Guru extends User {
    private String nip;
    private String spesialisasi;

    public Guru(String idUser, String username, String password,
                String namaLengkap, String email,
                String nip, String spesialisasi) {

        super(idUser, username, password, namaLengkap, email);
        this.nip = nip;
        this.spesialisasi = spesialisasi;
    }

    @Override
    public void tampilkanMenu() {
        System.out.println("=== Menu Guru ===");
        System.out.println("1. Kelola Materi");
        System.out.println("2. Kelola Tugas");
        System.out.println("3. Kelola Ujian");
        System.out.println("4. Lihat Daftar Siswa");
        System.out.println("5. Nilai Jawaban");
        System.out.println("6. Kelola Profil Guru");
        System.out.println("0. Logout");
    }
}
