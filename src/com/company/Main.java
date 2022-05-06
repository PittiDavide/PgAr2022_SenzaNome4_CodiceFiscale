package com.company;

import javax.xml.stream.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Main {

    /**
     * Legge tutte le persone presenti nel file XML inputPersone.xml e crea un oggetto Persona per ciascuna di esse
     */
    public static ArrayList<Persona> leggiPersone() throws XMLStreamException, IOException, ParseException {
        ArrayList<Persona> persone = new ArrayList<>();
        final String filename = "inputPersone.xml";
        XMLInputFactory xmlif = null;
        XMLStreamReader xmlr = null;
        String nome = null, cognome = null, sesso = null, comuneNascita = null, reading = null;
        int id = 0;
        Date dataNascita = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            xmlif = XMLInputFactory.newInstance();
            xmlr = xmlif.createXMLStreamReader(filename, new FileInputStream(filename));
        } catch (Exception e) {
            System.out.println("Errore nell'inizializzazione del reader:");
            System.out.println(e.getMessage());
        }
        while (xmlr.hasNext()) {
            switch (xmlr.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    reading = xmlr.getLocalName();
                    if(reading.equals("persona")) {
                        id = Integer.parseInt(xmlr.getAttributeValue(0));
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if(xmlr.getLocalName().equals("persona")){
                        persone.add(new Persona(nome, cognome, sesso, comuneNascita, dataNascita, id));
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (xmlr.getText().trim().length() > 0)
                        if(reading.equals("nome")){
                            nome = xmlr.getText();
                        }else if(reading.equals("cognome")){
                            cognome = xmlr.getText();
                        }else if(reading.equals("sesso")){
                            sesso = xmlr.getText();
                        }else if(reading.equals("comune_nascita")) {
                            comuneNascita = xmlr.getText();
                        }else if(reading.equals("data_nascita")) {
                            dataNascita = sdf.parse(xmlr.getText());
                        }
                    break;
            }
            xmlr.next();
        }
        return persone;
    }

    /**
     * Legge dal file codiciFiscali.xml tutti i codici fiscali presenti, li valida e li inserisce in un array di stringhe
     */
    public static Object[] leggiCodiciFiscali() throws XMLStreamException, IOException {
        ArrayList<String> validi = new ArrayList<>();
        ArrayList<String> invalidi = new ArrayList<>();

        final String filename = "codiciFiscali.xml";
        XMLInputFactory xmlif = null;
        XMLStreamReader xmlr = null;
        String reading = null;
        try {
            xmlif = XMLInputFactory.newInstance();
            xmlr = xmlif.createXMLStreamReader(filename, new FileInputStream(filename));
        } catch (Exception e) {
            System.out.println("Errore nell'inizializzazione del reader:");
            System.out.println(e.getMessage());
        }
        while (xmlr.hasNext()) {
            switch (xmlr.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    reading = xmlr.getLocalName();
                    break;

                case XMLStreamConstants.CHARACTERS:
                    if (xmlr.getText().trim().length() > 0)
                        if(reading.equals("codice")) {
                            if(CodiceFiscale.verificaValidita(xmlr.getText())){
                                validi.add(xmlr.getText());
                            }else {
                                invalidi.add(xmlr.getText());
                            }
                        }
                    break;
            }
            xmlr.next();
        }
        return new Object[]{validi, invalidi};
    }


    /**
     * Scrive sul file outputPersone.xml tutte le persone e tutti i codici invalidi e spaiati
     * @param persone ArrayList di persone
     * @param validi ArrayList di codici validi
     * @param invalidi ArrayList di codici invalidi
     * @param spaiati  ArrayList di codici spaiati
     */
    public static void scriviFile(ArrayList<Persona> persone, ArrayList<String> validi , ArrayList<String> invalidi, ArrayList<String> spaiati)  {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String filename = "outputPersone.xml";
        try {
            XMLOutputFactory xmlof = null;
            XMLStreamWriter xmlw = null;
            try {
                xmlof = XMLOutputFactory.newInstance();
                xmlw = xmlof.createXMLStreamWriter(new FileOutputStream(filename), "utf-8");
                xmlw.writeStartDocument("utf-8", "1.0");
            } catch (Exception e) {
                System.out.println("Errore nell'inizializzazione del writer:");
                System.out.println(e.getMessage());
            }
            xmlw.writeStartElement("output");
            xmlw.writeStartElement("persone");
            xmlw.writeAttribute("numero", String.valueOf(persone.size()));
            for (Persona p : persone) {
                xmlw.writeStartElement("persona");
                xmlw.writeAttribute("id", String.valueOf(p.getId()));
                xmlw.writeStartElement("nome");
                xmlw.writeCharacters(p.getNome());
                xmlw.writeEndElement();
                xmlw.writeStartElement("cognome");
                xmlw.writeCharacters(p.getCognome());
                xmlw.writeEndElement();
                xmlw.writeStartElement("sesso");
                xmlw.writeCharacters(p.getSesso());
                xmlw.writeEndElement();
                xmlw.writeStartElement("data_nascita");
                xmlw.writeCharacters(sdf.format(p.getDataNascita()));
                xmlw.writeEndElement();
                xmlw.writeStartElement("codice_fiscale");
                if(validi.contains(p.getCodiceFiscale())){
                    xmlw.writeCharacters(p.getCodiceFiscale());
                }else {
                    xmlw.writeCharacters("ASSENTE");
                }
                xmlw.writeEndElement();
                xmlw.writeEndElement();
            }
            xmlw.writeEndElement();

            xmlw.writeStartElement("invalidi");
            xmlw.writeAttribute("numero", String.valueOf(invalidi.size()));
            for(String s : invalidi){
                xmlw.writeStartElement("codice");
                xmlw.writeCharacters(s);
                xmlw.writeEndElement();
            }
            xmlw.writeEndElement();
            xmlw.writeStartElement("spaiati");
            xmlw.writeAttribute("numero", String.valueOf(spaiati.size()));
            for(String s : spaiati){
                xmlw.writeStartElement("codice");
                xmlw.writeCharacters(s);
                xmlw.writeEndElement();
            }
            xmlw.writeEndElement();
            xmlw.writeEndDocument();
            xmlw.flush();
            xmlw.close();
        } catch (Exception e) {
            System.out.println("Errore nella scrittura");
        }
    }

    public static void main(String[] args) throws XMLStreamException, IOException, ParseException {
        ArrayList<Persona> persone = leggiPersone();
        ArrayList<String> codiciPersone = new ArrayList<>();
        Object[] codici = leggiCodiciFiscali();
        ArrayList<String> validi = (ArrayList<String>) codici[0];
        ArrayList<String> invalidi = (ArrayList<String>) codici[1];
        ArrayList<String> spaiati = new ArrayList<>();
        for(Persona p : persone){
            codiciPersone.add(p.getCodiceFiscale());
        }
        for(String s : validi){
            if(!codiciPersone.contains(s)){
                spaiati.add(s);
            }
        }
        scriviFile(persone, validi, invalidi, spaiati);


    }
}
