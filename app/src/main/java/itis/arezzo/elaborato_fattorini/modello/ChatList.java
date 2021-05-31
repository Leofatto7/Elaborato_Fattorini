package itis.arezzo.elaborato_fattorini.modello;

public class ChatList {
    private String userID;
    private String username;
    private String descrizione;
    private String data;
    private String urlProfile;

    public ChatList(){
    }

    public ChatList(String userID, String username, String descrizione, String data, String urlProfile) {
        this.userID = userID;
        this.username = username;
        this.descrizione = descrizione;
        this.data = data;
        this.urlProfile = urlProfile;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }
}
