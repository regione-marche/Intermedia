package it.extrasys.marche.regione.fatturapa.core.utils.validator;

import java.math.BigInteger;

public class ValidatoreSubjectNotificaEsitoCommittente {

    public static boolean validate(String subject) {

        if (subject == null || "".equals(subject)) {

            return false;
        }

        String[] subjectSplit = subject.split("-");

        if(subjectSplit.length == 1 || subjectSplit.length > 2)
            return false;

        String idSdi = subjectSplit[0].trim();
        String nomeFileEC = subjectSplit[1].trim();

        boolean idSdiIsValid;

        try{
            new BigInteger(idSdi);
            idSdiIsValid = true;
        }catch (Exception e){
            idSdiIsValid = false;
        }

        boolean nomeFileECIsValid = ValidatoreNomeNotificaEsitoCommittente.validate(nomeFileEC);

        if(idSdiIsValid && nomeFileECIsValid){
            return true;
        }else{
            return false;
        }
    }
}