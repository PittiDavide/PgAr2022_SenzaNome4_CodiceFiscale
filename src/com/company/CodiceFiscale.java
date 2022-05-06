package com.company;


import javax.xml.stream.*;
import java.io.*;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gruppo Senza Nome 4
 * @version 1.0
 * Classe rappresentante un codice fiscale
 */
public class CodiceFiscale {
    private final String nome;
    private final String cognome;
    private final Date dataNascita;
    private final String sesso;
    private final String comune;
    public static Map<String, Map<String, String>> valori = null;

    /**
     * Controlla la validità di un codice fiscale
     * @param cf il codice fiscale da controllare
     * @return true se il codice fiscale è valido, false altrimenti
     */
    public static boolean verificaValidita(String cf) throws IOException, XMLStreamException {
        if(valori == null){
            caricaRisorse();
        }
        if (cf.length() != 16) {
            return false;
        }
        cf = cf.toUpperCase();
        String regex = "[A-Z]{6}\\d{2}[ABCDEHLMPRST]\\d{2}[A-Z]{1}\\d{3}[A-Z]{1}";
        Pattern pattern = Pattern.compile(regex);
        String codiceFiscale = cf.toUpperCase();
        Matcher matcher = pattern.matcher(codiceFiscale);
        if(!matcher.matches()){
            return false;
        }

        int giorno = Integer.parseInt(cf.substring(9, 11));
        String mese = String.valueOf(cf.charAt(8));
        String sesso;
        if(giorno<=31){
            sesso="M";
            if (giorno < 1) {
                return false;
            }
        }else{
            sesso="F";
            if (giorno < 41 || giorno > 71) {
                return false;
            }
        }

        if(!("ABCDEHLMPRST".contains(mese))){
            return false;
        }
        if(!controllaGiorno(mese, giorno, sesso)){
            return false;
        }
        if(cf.charAt(15)!=buildCodiceControllo(cf.substring(0,15)).charAt(0)){
            return false;
        }
        return true;
    }

    /**
     * Controlla se la data è valida
     * @return true se la data è valida, false altrimenti
     */
    public static boolean controllaGiorno(String mese, int giorno, String sesso) {
        if(sesso.equals("F")){
            giorno-=40;
        }
        int[] massimoGiorni = {31,28,31,30,31,30,31,31,30,31,30,31};
        int numeroMese = getMeseDaLettera(mese);
        if(giorno>massimoGiorni[numeroMese]){
            return false;
        }
        return true;
    }

    public CodiceFiscale(String nome, String cognome, Date dataNascita, String sesso, String comune) throws XMLStreamException, IOException {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.sesso = sesso;
        this.comune = comune;
        if(valori == null){
            caricaRisorse();
        }
    }

    public static String getConsonanti(String s) {
        return s.toLowerCase().replaceAll("[aeiou]","").toUpperCase();
    }

    public static String getVocali(String s){
        return s.toLowerCase().replaceAll("[^aeiou]","").toUpperCase();
    }

    /**
     * Trasforma un nome/cognome nei tre caratteri adatti al formato del codice fiscale
     * MATTIA -> MTT
     * @param nome Nome/Cognome da convertire
     * @param mode Modalità di conversione, può essere "nome" o "cognome"
     * @return Stringa contenente i tre caratteri adatti al formato del codice fiscale
     */
    private static String buildSigla(String nome, String mode){
        StringBuilder consonantiBuilder = new StringBuilder(getConsonanti(nome));
        if(mode.equals("nome")&&consonantiBuilder.length()>3){
            consonantiBuilder.deleteCharAt(1);
        }
        String consonanti = consonantiBuilder.toString();
        String vocali = getVocali(nome);
        char[] res = new char[3];
        int vocali_usate = 0;
        for(int i=0;i<3;i++){
            if(consonanti.length()>i){
                res[i] = consonanti.charAt(i);
            }else if(vocali.length()>vocali_usate){
                res[i] = vocali.charAt(vocali_usate);
                vocali_usate++;
            }else{
                res[i] = 'X';
            }

        }
        return new String(res);
    }

    /**
     * Trasforma l'anno di nascita nell'apposita sigla
     * 2001 -> 01
     * @param dataNascita Data di nascita
     * @return Stringa contenente l'anno di nascita normalizzato
     */
    private static String buildAnno(Date dataNascita) {
        String a = Integer.toString(dataNascita.getYear());
        return a.substring(a.length()-2);
    }

    /**
     * Trasforma il mese nell'apposita sigla
     * 1 -> A
     * @param dataNascita Data di nascita
     * @return Stringa contenente il mese di nascita normalizzato
     */
    private static String buildMese(Date dataNascita){
        String r = switch (dataNascita.getMonth()) {
            case 0 -> "A";
            case 1 -> "B";
            case 2 -> "C";
            case 3 -> "D";
            case 4 -> "E";
            case 5 -> "H";
            case 6 -> "L";
            case 7 -> "M";
            case 8 -> "P";
            case 9 -> "R";
            case 10 -> "S";
            case 11 -> "T";
            default -> throw new IllegalArgumentException("Mese non trovato");
        };
        return r;
    }

    /**
     * Trasforma la sigla del mese nel numero del mese
     * @param mese
     * @return numero del mese
     */

    private static int getMeseDaLettera(String mese) {
        int r = switch (mese) {
            case "A" -> 0;
            case "B" -> 1;
            case "C" -> 2;
            case "D" -> 3;
            case "E" -> 4;
            case "H" -> 5;
            case "L" -> 6;
            case "M" -> 7;
            case "P" -> 8;
            case "R" -> 9;
            case "S" -> 10;
            case "T" -> 11;
            default -> throw new IllegalArgumentException("Mese non trovato");
        };
        return r;
    }

    /**
     * Trasforma il giorno di nascita nell'apposita sigla
     * @param dataNascita Data di nascita
     * @param sesso Sesso
     * @return Stringa contenente il giorno di nascita normalizzato
     */
    public static String buildGiorno(Date dataNascita, String sesso) {
        int r;
        if (sesso.equals("F")) {
            r = dataNascita.getDate()+40;
        }else if (sesso.equals("M")) {
            r = dataNascita.getDate();
        }else{
            throw new IllegalArgumentException("Sesso non valido");
        }
        return String.format("%02d", r);
    }

    /**
     * Carica i file contenenti le associazioni numeri lettere per il codice di controllo
     * @throws IOException,XMLStreamException In caso di errore di lettura
     */
    private static void caricaRisorse() throws IOException, XMLStreamException {
        String[] files = new String[]{"controllo-dispari.txt","controllo-pari.txt","controllo-resto.txt"};
        valori = new HashMap<>();
        XMLInputFactory xmlif = null;
        XMLStreamReader xmlr = null;
        final String fileComuni="comuni.xml";
        try {
            xmlif = XMLInputFactory.newInstance();
            xmlr = xmlif.createXMLStreamReader(fileComuni, new FileInputStream(fileComuni));
        } catch (Exception e) {
            System.out.println("Errore nell'inizializzazione del reader:");
            System.out.println(e.getMessage());
        }
        Map<String, String> tmp = new HashMap<>();

        String nome = null;
        String codice = null;
        String reading = null;

        /* La lettura avverrà nel seguente modo
            Trovo uno start element : imposto la variabile reading al nome dell'elemento (comune o codice)
            Le letture successive saranno di tipo characters, la leggeremo la variabile reading per impostare i valori
            letti nelle variabili nome e codice, i quali verranno messi nella map tmp una volta trovato un end element
        */

        while (xmlr.hasNext()) {
            switch (xmlr.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    reading = xmlr.getLocalName();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if(xmlr.getLocalName().equals("comune")){
                        tmp.put(nome, codice);
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (xmlr.getText().trim().length() > 0)
                        if(reading.equals("nome")){
                            nome = xmlr.getText();
                        }else if(reading.equals("codice")){
                            codice = xmlr.getText();
                        }
                    break;
            }
            xmlr.next();
        }
        valori.put(fileComuni, tmp);
        for (String file : files) {
            Map<String, String> tmp2 = new HashMap<>();
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            boolean eof = false;
            while (!eof) {
                String c = reader.readLine();
                if (c == null) {
                    valori.put(file, tmp2);
                    eof = true;
                } else {
                    String[] parti = c.split(";");
                    tmp2.put(parti[0], parti[1]);
                }
            }

        }

    }


    private static String buildComune(String comune) {
        Map<String, String> comuni = valori.get("comuni.xml");
        String code = comuni.get(comune.toUpperCase());
        if (code == null) {
            throw new IllegalArgumentException("Comune non trovato");
        }
        return code;
    }

    /**
     * Calcola il codice di controllo
     * @param codice codice fiscale senza codice di controllo finale
     * @return Codice di controllo
     */
    private static String buildCodiceControllo(String codice) {
        Map<String, String> dispari = valori.get("controllo-dispari.txt");
        Map<String, String> pari = valori.get("controllo-pari.txt");
        Map<String, String> resto = valori.get("controllo-resto.txt");
        int somma = 0;
        for(int i=1; i<=codice.length(); i++){
            if(i%2==0){
                somma += Integer.parseInt(pari.get(String.valueOf(codice.charAt(i-1))));
            }else{
                somma += Integer.parseInt(dispari.get(String.valueOf(codice.charAt(i-1))));
            }
        }
        return (resto.get(Integer.toString(somma%26)));
    }


    /**
     * Calcola il codice fiscale
     * @return codice fiscale
     */
    @Override
    public String toString() {
        StringBuilder codice = new StringBuilder();
        codice.append(buildSigla(cognome, "cognome"));
        codice.append(buildSigla(nome, "nome"));
        codice.append(buildAnno(dataNascita));
        codice.append(buildMese(dataNascita));
        codice.append(buildGiorno(dataNascita, sesso));
        codice.append(buildComune(comune));
        codice.append(buildCodiceControllo(codice.toString()));
        return codice.toString();
    }

}