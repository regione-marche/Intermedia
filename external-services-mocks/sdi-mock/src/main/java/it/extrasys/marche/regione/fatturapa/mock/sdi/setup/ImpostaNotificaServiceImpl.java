package it.extrasys.marche.regione.fatturapa.mock.sdi.setup;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.EsitoNotificaType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.FileSdIBaseType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevinotifica.beans.RispostaSdINotificaEsitoType;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;

public class ImpostaNotificaServiceImpl implements ImpostaNotifica{

	private static RispostaSdINotificaEsitoType notificaImpostata;

	public ImpostaNotificaServiceImpl(){
		
		notificaImpostata = new RispostaSdINotificaEsitoType();
		notificaImpostata.setEsito(EsitoNotificaType.ES_00);
	}
	@Override
	public RispostaSdINotificaEsitoType getNotificaImpostata() {
		// TODO Auto-generated method stub
		return notificaImpostata;
	}

	@Override
	public RispostaSdINotificaEsitoType setNotificaImpostata(RispostaSdINotificaEsitoType notifica) {
		// TODO Auto-generated method stub
		notificaImpostata = notifica;
		return notificaImpostata;
	}
	

	public static RispostaSdINotificaEsitoType getNotifica() {
		return notificaImpostata;
	}

	public static void setNotifica(RispostaSdINotificaEsitoType notificaImpostata) {
		ImpostaNotificaServiceImpl.notificaImpostata = notificaImpostata;
	}
	

}
