package it.extrasys.marche.regione.fatturapa.persistence.unit.converters;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.ca.beans.EsitoFatturaMessageType;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;

public class NotificaFromEntiToEntityCAConverter {

    public NotificaFromEntiEntity convert(EsitoFatturaMessageType esitoFattura) {

        NotificaFromEntiEntity notificaEntity;
        notificaEntity = new NotificaFromEntiEntity();
        notificaEntity.setIdFiscaleCommittente(esitoFattura.getIdFiscaleCommittente());
        notificaEntity.setCodUfficio(esitoFattura.getCodUfficio());

        return notificaEntity;

    }
}
