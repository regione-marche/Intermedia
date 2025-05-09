package it.extrasys.marche.regione.fatturapa.core.utils.validator;

/**
 * Created by agosteeno on 14/04/15.
 */
public class ValidatoreNomeNotificaEsitoCommittente {

    /*

        XXX dalla specifica dello SdI:

        [...] il file della notifica di esito committente, esso deve essere "nominato" nel modo seguente affinché possa essere
        accettato dal Sistema di Interscambio:

        [Nome del file ricevuto senza estensione]_EC_[Progressivo univoco].xml

        il Nome del file ricevuto senza estensione deve essere conforme alle regole definite nella sezione Predisporre il file
        FatturaPA. Nel caso in cui il nome file non sia conforme e la sua lunghezza sia superiore ai 36 caratteri il nome sarà
        troncato ed i caratteri oltre il 36-esimo non saranno presenti nella notifica di scarto, EC (valore fisso) indica il
        tipo di messaggio (Esito Committente), Il Progressivo univoco deve essere una stringa alfanumerica di lunghezza massima
        3 caratteri e con valori ammessi da "A" a "Z" e da "0" a "9" che identifica univocamente ogni notifica / ricevuta
        relativa al file inviato. Il separatore degli elementi che compongono il nome file è il carattere underscore ("_"),
        codice ASCII 95. L'estensione è sempre "xml".

        IMPORTANTE
        il nome file ricevuto ha al suo interno un carattere '_'
     */


    private static final String TIPO_MESSAGGIO_NOTIFICA_SCARTO = "EC";
    private static final String XML_EXT = "xml";

    public static boolean validate(String nomeFileNotificaEsitoCommittente){

        if(nomeFileNotificaEsitoCommittente == null || "".equals(nomeFileNotificaEsitoCommittente)){

            return false;
        }

        String[] parti = nomeFileNotificaEsitoCommittente.split("_");

        //ho un file senza estensione, oppure con troppi caratteri '_' e quindi non valido
        if(parti.length != 4){
            return false;
        }

        //l'ultima parte contiene anche l'estenzione, verificare anche quella
        String ultimo = parti[3];

        String[] ultimeParti = ultimo.split("\\.");

        if(ultimeParti.length != 2){
            return false;
        }

        String estensione = ultimeParti[1].trim().toLowerCase();

        if(!XML_EXT.equals(estensione)){
            return false;
        }

        String progressivoUnivoco = ultimeParti[0];

        if(!isCorrettoProggressivoUnivoco(progressivoUnivoco)){
            return false;
        }

        /*
        adesso validiamo la prima parte che deve seguire le regole del nome file fatturapa. Per fare questo c'e' la funzione
        apposita in ValidatoreNomeFattura ATTENZIONE questa si aspetta che ci sia anche l'estensione (xml per esempio) quindi
        e' necessario aggiungerla in coda per poterla utilizzare
         */

        String nomeFileRicevutoSenzaEstensione = parti[0] + "_" + parti[1];

        String nomeFileRicevutoCompleto = nomeFileRicevutoSenzaEstensione + "." + XML_EXT;

        if(!ValidatoreNomeFattura.validate(nomeFileRicevutoCompleto)){
            return false;
        }

        //ora verifico che la seconda parte (il tipo messaggio) sia uguale esattamente alla stringa "EC"
        String tipoMessaggio = parti[2];

        if(!TIPO_MESSAGGIO_NOTIFICA_SCARTO.equals(tipoMessaggio)){
            return false;
        }

        return true;
    }

    private static boolean isCorrettoProggressivoUnivoco(String progressivoUnivoco) {

        if(progressivoUnivoco.length() > 3){
            return false;
        } else {

            for (int i = 0; i < progressivoUnivoco.length(); i++) {
                char c = progressivoUnivoco.charAt(i);
                if (!Character.isDigit(c) && !Character.isLetter(c))
                    return false;
            }

            return true;
        }
    }

}
