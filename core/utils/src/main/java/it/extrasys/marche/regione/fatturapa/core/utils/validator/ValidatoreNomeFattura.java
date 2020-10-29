package it.extrasys.marche.regione.fatturapa.core.utils.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by agosteeno on 15/03/15.
 */
public class ValidatoreNomeFattura {

    private static final String XML_EXT = "xml";
    private static final String ZIP_EXT = "zip";
    private static final String P7M_EXT = "p7m";

    public static boolean validate(String nomeFattura){

        if(nomeFattura == null || "".equals(nomeFattura)){

            return false;
        }

        String[] parts = nomeFattura.split("\\.");

        //ho un file senza estensione, oppure con troppi caratteri '.' e quindi non valido
        if(parts.length <2 || parts.length >3){
            return false;
        }

        //CASO 1: una sola estensione (ad esempio ITAAABBB99T99X999W_00001.xml o ITAAABBB99T99X999W_00001.zip )
        if(parts.length == 2){
            String ext = parts[1].trim().toLowerCase();

            if(!(ext.equals(XML_EXT) || ext.equals(ZIP_EXT))){
                return false;
            }
        }

        //CASO 2: due estensioni (ad esempio IT99999999999_00002.xml.p7m)
        if(parts.length == 3){
            String ext1 = parts[1].trim().toLowerCase();
            String ext2 = parts[2].trim().toLowerCase();

            if(!ext1.equals(XML_EXT) && !ext2.equals(P7M_EXT)){
                return false;
            }
        }

        //VALIDAZIONE NOME: se ho passato i controlli di validazione su estensioni valido il nome
        String[] nameParts = parts[0].split("_");

        //devo avere IDFiscale e progressivo
        if(nameParts.length != 2)
            return false;
        //il progressivo deve essere di al massimo 5 caratteri
        if(nameParts[1].length() < 1 || nameParts[1].length() > 5)
            return false;

        String isocode =  nameParts[0].substring(0,2).toUpperCase();

        String[] countryCodes = Locale.getISOCountries();

        List<String> listCountryCodes = new ArrayList<>();

        for(int i = 0; i < countryCodes.length; i++){

            listCountryCodes.add(countryCodes[i]);
        }

        if(!listCountryCodes.contains(isocode)){
            return false;
        }

        //CASO ISOCODE = IT
        if(isocode.equals("IT")){
            // in questo caso la dimensione della stringa deve essere compresa tra IT+11 e IT+16
            if(nameParts[0].length() <13 || nameParts[0].length() >18){
                return false;
            }
        }
        else{
            // se l'isocode non è IT allora deve essere 2+2 e 2+28
            if(nameParts[0].length() <4 || nameParts[0].length() >30){
                return false;
            }
        }

        return true;
    }
}
