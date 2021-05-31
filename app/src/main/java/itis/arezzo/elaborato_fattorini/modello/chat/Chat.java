package itis.arezzo.elaborato_fattorini.modello.chat;

public class Chat {
    private String dateTime;
    private String messaggio;
    private String tipo;
    private String mittente;
    private String destinatario;

    public Chat() {
    }

    public Chat(String dateTime, String messaggio, String tipo, String mittente, String destinatario) {
        this.dateTime = dateTime;
        this.messaggio = messaggio;
        this.tipo = tipo;
        this.mittente = mittente;
        this.destinatario = destinatario;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMittente() {
        return mittente;
    }

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
}
