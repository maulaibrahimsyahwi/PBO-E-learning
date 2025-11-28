package model;

public class Admin extends User {

    public Admin(String idUser, String username, String password,
                 String namaLengkap, String email) {
        super(idUser, username, password, namaLengkap, email);
    }

    @Override
    public void tampilkanMenu() {
        System.out.println("=== Menu Admin ===");
        System.out.println("1. Kelola Data Guru");
        System.out.println("2. Kelola Data Siswa");
        System.out.println("3. Kelola Kelas");
        System.out.println("4. Kelola Mata Pelajaran");
        System.out.println("5. Approve Pendaftaran Akun");
        System.out.println("0. Logout");
    }
}
