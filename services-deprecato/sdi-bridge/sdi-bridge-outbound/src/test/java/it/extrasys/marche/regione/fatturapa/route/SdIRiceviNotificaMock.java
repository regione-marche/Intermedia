package it.extrasys.marche.regione.fatturapa.route;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.EsitoNotificaType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.FileSdIType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.RispostaSdINotificaEsitoType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.SdIRiceviNotifica;

public class SdIRiceviNotificaMock implements SdIRiceviNotifica{

	EsitoNotificaType esito = EsitoNotificaType.ES_01;
	@Override
	public RispostaSdINotificaEsitoType notificaEsito(FileSdIType parameters) {
		RispostaSdINotificaEsitoType returned = new RispostaSdINotificaEsitoType();
		EsitoNotificaType esito = EsitoNotificaType.ES_01;
		returned.setEsito(esito);
		// TODO Auto-generated method stub
		return returned ;
	}
	public EsitoNotificaType getEsito() {
		return esito;
	}
	public void setEsito(EsitoNotificaType esito) {
		this.esito = esito;
	}
	

}
