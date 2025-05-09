package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca;

import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAEnteNonTrovatoException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPAException;
import it.extrasys.marche.regione.fatturapa.core.exceptions.FatturaPaPersistenceException;
import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ca.model.FatturaFtpModel;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.fattura.EnteEntity;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.DatiFatturaManager;
import it.extrasys.marche.regione.fatturapa.persistence.unit.managers.FileFatturaManager;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GestioneDatiFatturaManager {
    private static final Logger LOG = LoggerFactory.getLogger(GestioneDatiFatturaManager.class);

    private DatiFatturaManager datiFatturaManager;
    private FileFatturaManager fileFatturaManager;


    public void getFattureRicevuteByCodDest(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException, FatturaPAEnteNonTrovatoException, IOException, JAXBException {
        EnteEntity ente = (EnteEntity) exchange.getIn().getBody();

        String codiceUfficio = ente.getCodiceUfficio();
        List<Object[]> fatture = datiFatturaManager.getStatoFatturaUltimoStatoRicevutaByCodiceDestinatario(codiceUfficio);

        List<FatturaFtpModel> fatturaModels = new ArrayList<>();

        if (fatture.size() > 0) {
            fatturaModels = mapDatiFatturaToFatturaModel(fatture, codiceUfficio);
        }
        exchange.getIn().setBody(fatturaModels);
    }

    public void getFattureUltimoStatoProtocolloByCodDest(Exchange exchange) throws FatturaPAException, FatturaPaPersistenceException {
        EnteEntity ente = (EnteEntity) exchange.getIn().getBody();
        String codiceUfficio = ente.getCodiceUfficio();
        List<FatturaFtpModel> fatturaModels = new ArrayList<>();

        List<Object[]> fatture = datiFatturaManager.getStatoFatturaUltimoStatoProtocolloByCodiceDestinatario(ente.getCodiceUfficio());

        if (fatture.size() > 0) {
            fatturaModels = mapDatiFatturaToFatturaModel(fatture, codiceUfficio);
        }
        exchange.getIn().setBody(fatturaModels);
    }


    private List<FatturaFtpModel> mapDatiFatturaToFatturaModel(List<Object[]> datiFattura, String codiceUfficio) {
        List<FatturaFtpModel> fatturaFtpModelList = new ArrayList<>();
        String codEnte = (String) datiFattura.get(0)[4];
        for (Object[] obj : datiFattura) {
            FatturaFtpModel fm = new FatturaFtpModel();
            fm.setIdFattura(BigInteger.valueOf((Long) obj[0]));
            fm.setTipoFattura(FatturaFtpModel.FATTURA_PASSIVA);
            fm.setNomeFile((String) obj[1]);
            fm.setIdFiscaleEnte(codEnte);
            fm.setContenutoFattura((byte[]) obj[2]);
            fm.setCodiceUfficio(codiceUfficio);
            fm.setIdentificativoSdI(BigInteger.valueOf((Long) obj[3]));
            fm.setContenutoMetadati((byte[]) obj[5]);
            fm.setNomeFileMetadati((String) obj[6]);
            fatturaFtpModelList.add(fm);
        }
        return fatturaFtpModelList;
    }


    public DatiFatturaManager getDatiFatturaManager() {
        return datiFatturaManager;
    }

    public void setDatiFatturaManager(DatiFatturaManager datiFatturaManager) {
        this.datiFatturaManager = datiFatturaManager;
    }

    public FileFatturaManager getFileFatturaManager() {
        return fileFatturaManager;
    }

    public void setFileFatturaManager(FileFatturaManager fileFatturaManager) {
        this.fileFatturaManager = fileFatturaManager;
    }
}
