package it.extrasys.marche.regione.fatturapa.persistence.unit.converters;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoFatturaMessageType;
import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 16/02/15.
 */
public class NotificaFromEntiToEntityConverter {

    public NotificaFromEntiEntity convert(EsitoFatturaMessageType esitoFattura, String idComunicazione) {

        NotificaFromEntiEntity notificaEntity;
        notificaEntity = new NotificaFromEntiEntity();
        notificaEntity.setNumeroProtocollo(esitoFattura.getNumeroProtocollo());
        notificaEntity.setIdFiscaleCommittente(esitoFattura.getIdFiscaleCommittente());
        notificaEntity.setCodUfficio(esitoFattura.getCodUfficio());
        notificaEntity.setMessageIdCommittente(esitoFattura.getMessageIdCommittente());
        notificaEntity.setDataFattura(DateUtils.XMLGregorianCalendarToDate(esitoFattura.getDataFattura()));
        notificaEntity.setNumeroFattura(esitoFattura.getNumeroFattura());
        notificaEntity.setEsito(NotificaFromEntiEntity.CODICI_ESITO_NOTIFICA.parse(esitoFattura.getEsito().value()));
        notificaEntity.setDescrizione(esitoFattura.getDescrizione());
        notificaEntity.setIdComunicazione(idComunicazione);

        return notificaEntity;

    }
}
