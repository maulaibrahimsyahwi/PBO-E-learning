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

    public String getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {     
        return password;
    }

    // 🔥 METHOD BARU
    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getEmail() {         
        return email;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public void logout() {
        System.out.println("Anda telah logout.");
    }

    public abstract void tampilkanMenu();
}