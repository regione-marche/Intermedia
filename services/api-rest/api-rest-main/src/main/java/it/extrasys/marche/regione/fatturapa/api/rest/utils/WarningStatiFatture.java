package it.extrasys.marche.regione.fatturapa.api.rest.utils;

import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.monitoraggio.CodificaFlagWarningEntity;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class WarningStatiFatture {
    private static final Logger LOG = LoggerFactory.getLogger(WarningStatiFatture.class);

    public static CodificaFlagWarningEntity.CODICI_FLAG_WARNING calcolaWarningFatturePassive(String stato, Date dataRicezione, int intervalDecTermini) {

        CodificaFlagWarningEntity.CODICI_FLAG_WARNING flag = null;

        if (CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PROTOCOLLATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.REGISTRATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA_ACCETTAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTO_RIFIUTO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATA_ACCETTAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATO_RIFIUTO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.ACCETTATA_PER_DECORRENZA_TERMINI.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.INOLTRATA_MAIL.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_ACCETTATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.RIFIUTATA_PER_VALIDAZIONE_FALLITA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_ACCETTATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_FATTURA_CONSEGNATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_ACCETTATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_DECORRENZA_CONSEGNATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA_CONSEGNATA.equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.NOTIFICA_SCARTATA_PER_REINVIO.getValue().equals(stato)) {
            //TODO
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_ACCETTATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_CONSEGNATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA_DECORRENZA_TERMINI.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_INOLTRATA_PROTOCOLLO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_INVIO_UNICO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_INOLTRATA_PROTOCOLLO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_FATTURA_INOLTRATA_REGISTRAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_INVIO_UNICO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_INOLTRATA_PROTOCOLLO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_INOLTRATA_REGISTRAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_ACCETTATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_DEC_TERMINI_CONSEGNATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_ACCETTATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_CONSEGNATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_INVIO_UNICO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_INOLTRATA_PROTOCOLLO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_INOLTRATA_REGISTRAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_ACCETTATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_SCARTO_EC_CONSEGNATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_RICEVUTA_ACCETTAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.PEC_CA_EC_RICEVUTO_RIFIUTO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_FATTURA_INVIO_UNICO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_FATTURA_PROTOCOLLATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_FATTURA_REGISTRATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_DEC_TERMINI_INVIO_UNICO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_DEC_TERMINI_PROTOCOLLATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_DEC_TERMINI_REGISTRATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_EC_PROTOCOLLATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_SCARTO_EC_INVIO_UNICO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_SCARTO_EC_PROTOCOLLATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_SCARTO_EC_REGISTRATA.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_EC_RICEVUTA_ACCETTAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.WS_CA_EC_RICEVUTO_RIFIUTO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_FATTURA_INVIO_UNICO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_FATTURA_INVIO_PROTOCOLLO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_FATTURA_INVIO_REGISTRAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_DEC_TERMINI_INVIO_UNICO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_DEC_TERMINI_INVIO_PROTOCOLLO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_DEC_TERMINI_INVIO_REGISTRAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_SCARTO_EC_INVIO_UNICO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_SCARTO_EC_INVIO_PROTOCOLLO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_SCARTO_EC_INVIO_REGISTRAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_NOTIFICA_ESITO_COMMITTENTE_INVIO_PROTOCOLLO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.VERDE;
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_EC_RICEVUTA_ACCETTAZIONE.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else if (CodificaStatiEntity.CODICI_STATO_FATTURA.FTP_CA_EC_RICEVUTO_RIFIUTO.getValue().equals(stato)) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.GIALLO;
            flag = checkDecorrenzaTermini(dataRicezione, flag, intervalDecTermini);
        } else {
            LOG.warn("WARNING STATI FATTURA - Stato " + stato + " non gestito");
        }

        return flag;
    }


    /*
    Setta 'flag'= FLAG_ROSSO(true) se sono passati più di 16 giorni dalla ricezione della fattura, altrimenti lo lascia invariato
     */
    public static CodificaFlagWarningEntity.CODICI_FLAG_WARNING checkDecorrenzaTermini(Date dataRicezione, CodificaFlagWarningEntity.CODICI_FLAG_WARNING flag, int intervalDecTermini) {
        Days days = Days.daysBetween(new DateTime(dataRicezione), DateTime.now());
        if (days.isGreaterThan(Days.days(intervalDecTermini))) {
            flag = CodificaFlagWarningEntity.CODICI_FLAG_WARNING.ROSSO;
        }
        return flag;
    }
}
