package it.extrasys.marche.regione.fatturapa.persistence.unit.managers;

import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoFatturaMessageRequest;
import it.extrasys.marche.regione.fatturapa.contracts.esito.fattura.from.enti.beans.EsitoFatturaMessageType;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAFatturaNonTrovataException;
import it.extrasys.marche.regione.fatturapa.persistence.unit.converters.NotificaFromEntiToEntityConverter;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.CodificaStatiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.DatiFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.NotificaFromEntiDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.dao.StatoFatturaDao;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.CodificaStatiEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.DatiFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.StatoFatturaEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.notifiche.from.enti.NotificaFromEntiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 19/02/15.
 */
public class FatturazionePassivaNotificheManagerXAImpl {

	private static final Logger LOG = LoggerFactory.getLogger(FatturazionePassivaNotificheManagerXAImpl.class);

	NotificaFromEntiToEntityConverter notificaFromEntiToEntityConverter;

	NotificaFromEntiDao notificaDao;

	StatoFatturaDao statoFatturaDao;
	
	DatiFatturaDao datiFatturaDao;

	CodificaStatiDao codificaStatiDao;
	private EntityManagerFactory entityManagerFactory;

	/*
        REGMA 112: l'array flussoSemplificato stato aggiunto per ottenere un passaggio di parametro per riferimento e ottenere dunque dei dati senza dover fare
        nuovamente una query da parte del chiamante: questo per dire al chiamante se la fattura rientra nel ciclo del flusso semplificato e in questo caso avere anche
        il nome del file fattura originale.
    */
	public List<NotificaFromEntiEntity> salvaNotifiche(EsitoFatturaMessageRequest esitoFatturaMessageRequest, String idComunicazione, String flussoSemplificato[]) throws FatturaPAException, FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException,
			FatturaPAFatturaNonTrovataException {

		if(flussoSemplificato == null || flussoSemplificato.length != 2){
			throw new FatturaPAException("FatturazionePassivaNotificheManagerXAImpl - salvaNotifiche: String array flussoSemplificato NON valido");
		}

		EntityManager entityManager = null;

		StatoFatturaEntity statoFatturaEntity = null;

		NotificaFromEntiEntity notificaFromEntiEntity;

		List<NotificaFromEntiEntity> notificaFromEntiEntityList = new ArrayList<NotificaFromEntiEntity>();

		List<EsitoFatturaMessageType> esitoFatturaMessageList = esitoFatturaMessageRequest.getEsitoFatturaMessage();

		for (EsitoFatturaMessageType esitoFatturaMessage : esitoFatturaMessageList) {

			try {
				notificaFromEntiEntity = notificaFromEntiToEntityConverter.convert(esitoFatturaMessage, idComunicazione);
				entityManager = entityManagerFactory.createEntityManager();

				// RECUPERO LA FATTURA

                DatiFatturaEntity fattura = datiFatturaDao.getFatturaByCodiceUfficioNumeroProtocolloAndNumeroFattura(notificaFromEntiEntity.getCodUfficio(), notificaFromEntiEntity.getNumeroProtocollo(), esitoFatturaMessage.getNumeroFattura(), entityManager);

                statoFatturaEntity = statoFatturaDao.getLastStato(fattura.getIdDatiFattura(), entityManager);
                notificaFromEntiEntity.setIdentificativoSdI(fattura.getIdentificativoSdI());

				//TODO regma 112 check per verificare se si tratti o meno di una fattura che ricade nel flusso semplificato
				if(fattura.getFatturazioneInterna()){

					LOG.info("FatturazionePassivaNotificheManagerXAImpl - salvaNotifiche: flusso semplificato, fatturazione interna, nome file fattura: " + fattura.getNomeFile());
					flussoSemplificato[0] = "T";
					flussoSemplificato[1] = fattura.getNomeFile();
				} else {
					flussoSemplificato[0] = "F";
					flussoSemplificato[1] = fattura.getNomeFile();
				}

			} catch (NoResultException e) {
				if (entityManager != null && entityManager.isOpen()) {
					entityManager.close();
				}
				throw new FatturaPAFatturaNonTrovataException("Fattura  Non Trovata: Numero Protocollo: " + esitoFatturaMessage.getNumeroProtocollo() + " Numero Fattura: " + esitoFatturaMessage.getNumeroFattura());

			} catch (Exception e) {
				if (entityManager != null && entityManager.isOpen()) {
					entityManager.close();
				}
				throw new FatturaPAException("Errore Interno:" + e.getMessage(), e);
			}

			// / Controllo che la fattura non sia sia stata accettata per
			// Decorrenza Termini
			if (statoFatturaEntity.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.ACCETTATA_PER_DECORRENZA_TERMINI)) {
				if (entityManager != null && entityManager.isOpen()) {
					entityManager.close();
				}
				throw new FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException("Fattura Già Accettata Per Decorrenza Termini: Numero Protocollo: " + esitoFatturaMessage.getNumeroProtocollo() + " Numero Fattura: "
						+ esitoFatturaMessage.getNumeroFattura());
			}

			// / Controllo che la fattura non sia Già stata Accettata o
			// Rifiutata
			if (statoFatturaEntity.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATA_ACCETTAZIONE) || statoFatturaEntity.getStato().getCodStato().equals(CodificaStatiEntity.CODICI_STATO_FATTURA.INVIATO_RIFIUTO)) {
				if (entityManager != null && entityManager.isOpen()) {
					entityManager.close();
				}
				throw new FatturaPAFatturaGiaAccettataPerDecorrenzaTerminiException("Fattura Già Accettata Per Decorrenza Termini: Numero Protocollo: " + esitoFatturaMessage.getNumeroProtocollo() + "Numero Fattura: "
						+ esitoFatturaMessage.getNumeroFattura());
			}
			try {
				// Salvo la notifica
				notificaFromEntiEntity = notificaDao.create(notificaFromEntiEntity, entityManager);

				// Modifico Lo Stato
				StatoFatturaEntity nuovoStato = new StatoFatturaEntity();
				nuovoStato.setDatiFattura(statoFatturaEntity.getDatiFattura());
				if (notificaFromEntiEntity.getEsito().equals(NotificaFromEntiEntity.CODICI_ESITO_NOTIFICA.ACCETTATA)) {
					CodificaStatiEntity statoAccettata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTA_ACCETTAZIONE, entityManager);
					nuovoStato.setStato(statoAccettata);
				} else {
					CodificaStatiEntity statoRifiutata = codificaStatiDao.read(CodificaStatiEntity.CODICI_STATO_FATTURA.RICEVUTO_RIFIUTO, entityManager);
					nuovoStato.setStato(statoRifiutata);
				}
				statoFatturaDao.create(nuovoStato, entityManager);

			} catch (Exception e) {
				throw new FatturaPAException(e.getMessage());
			} finally {
				if (entityManager != null && entityManager.isOpen()) {
					entityManager.close();
				}
			}
			notificaFromEntiEntityList.add(notificaFromEntiEntity);
		}
		return notificaFromEntiEntityList;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public NotificaFromEntiToEntityConverter getNotificaFromEntiToEntityConverter() {
		return notificaFromEntiToEntityConverter;
	}

	public void setNotificaFromEntiToEntityConverter(NotificaFromEntiToEntityConverter notificaFromEntiToEntityConverter) {
		this.notificaFromEntiToEntityConverter = notificaFromEntiToEntityConverter;
	}

	public NotificaFromEntiDao getNotificaDao() {
		return notificaDao;
	}

	public void setNotificaDao(NotificaFromEntiDao notificaDao) {
		this.notificaDao = notificaDao;
	}

	public StatoFatturaDao getStatoFatturaDao() {
		return statoFatturaDao;
	}

	public void setStatoFatturaDao(StatoFatturaDao statoFatturaDao) {
		this.statoFatturaDao = statoFatturaDao;
	}

	public CodificaStatiDao getCodificaStatiDao() {
		return codificaStatiDao;
	}

	public void setCodificaStatiDao(CodificaStatiDao codificaStatiDao) {
		this.codificaStatiDao = codificaStatiDao;
	}

	public DatiFatturaDao getDatiFatturaDao() {
		return datiFatturaDao;
	}

	public void setDatiFatturaDao(DatiFatturaDao datiFatturaDao) {
		this.datiFatturaDao = datiFatturaDao;
	}

}
