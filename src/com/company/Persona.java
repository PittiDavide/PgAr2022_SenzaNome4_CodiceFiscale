package com.company;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Date;

public class Persona {
    private String nome;
    private String cognome;
    private String sesso;
    private String comuneNascita;
    private Date dataNascita;
    private String codiceFiscale;
    private int id;

    public Persona(String nome, String cognome, String sesso, String comuneNascita, Date dataNascita, int id) throws XMLStreamException, IOException {
        this.nome = nome;
        this.cognome = cognome;
        this.sesso = sesso;
        this.comuneNascita = comuneNascita;
        this.dataNascita = dataNascita;
        this.id = id;
        try {
            this.codiceFiscale = new CodiceFiscale(nome, cognome, dataNascita, sesso, comuneNascita).toString();
        } catch (Exception e) {
            this.codiceFiscale = "ASSENTE";
        }
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getSesso() {
        return sesso;
    }

    public String getComuneNascita() {
        return comuneNascita;
    }

    public Date getDataNascita() {
        return dataNascita;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public int getId() {
        return id;
    }
    @Override
    public String toString() {
        return "Persona{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", sesso='" + sesso + '\'' +
                ", comuneNascita='" + comuneNascita + '\'' +
                ", dataNascita=" + dataNascita +
                ", codiceFiscale='" + codiceFiscale + '\'' +
                ", id=" + id +
                '}';
    }
}
