package model;

public abstract class User {
    protected String idUser;
    protected String username;
    protected String password;
    protected String namaLengkap;
    protected String email;
    protected boolean aktif;

    public User(String idUser, String username, String password,
                String namaLengkap, String email) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.aktif = true;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public void logout() {
        System.out.println("Logout berhasil.\n");
    }

    // --- GETTER YANG DIPERBAIKI ---
    public String getUsername() { return username; }
    public String getIdUser() { return idUser; }
    // ------------------------------

    public void setNamaLengkap(String nama) { this.namaLengkap = nama; }
    public void setEmail(String email) { this.email = email; }
    public String getNamaLengkap() { return namaLengkap; }
    public boolean isAktif() { return aktif; }

    public abstract void tampilkanMenu();
}