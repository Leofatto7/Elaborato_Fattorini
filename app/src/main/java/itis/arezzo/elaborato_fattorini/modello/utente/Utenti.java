package itis.arezzo.elaborato_fattorini.modello.utente;

public class Utenti {
    private String userID;
    private String username;
    private String numeroTelefono;
    private String immagineProfilo;
    private String bio;

    public Utenti() {
    }

    public Utenti(String userID, String username, String numeroTelefono, String immagineProfilo, String bio) {
        this.userID = userID;
        this.username = username;
        this.numeroTelefono = numeroTelefono;
        this.immagineProfilo = immagineProfilo;
        this.bio = bio;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getImmagineProfilo() {
        return immagineProfilo;
    }

    public void setImmagineProfilo(String immagineProfilo) {
        this.immagineProfilo = immagineProfilo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
