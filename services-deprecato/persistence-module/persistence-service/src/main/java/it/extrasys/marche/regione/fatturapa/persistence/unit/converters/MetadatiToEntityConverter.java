package it.extrasys.marche.regione.fatturapa.persistence.unit.converters;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 27/01/15.
 */

import it.extrasys.marche.regione.fatturapa.contracts.messaggi.types.beans.MetadatiInvioFileType;
import it.extrasys.marche.regione.fatturapa.persistence.unit.entities.metadata.MetadatiFatturaEntity;

public class MetadatiToEntityConverter {


    public MetadatiFatturaEntity convert(MetadatiInvioFileType metadatiInvioFileType, String nomeFileMetadati, byte[] metadatiFile)  {

        if (metadatiInvioFileType == null){
            return null;
        }

        MetadatiFatturaEntity metadatiFatturaEntity = new MetadatiFatturaEntity();

        metadatiFatturaEntity.setCodiceDestinatario(metadatiInvioFileType.getCodiceDestinatario());
        metadatiFatturaEntity.setFormato(metadatiInvioFileType.getFormato());
        metadatiFatturaEntity.setIdentificativoSdI(metadatiInvioFileType.getIdentificativoSdI());
        metadatiFatturaEntity.setMessageId(metadatiInvioFileType.getMessageId());
        metadatiFatturaEntity.setNomeFile(metadatiInvioFileType.getNomeFile());
        metadatiFatturaEntity.setNote(metadatiInvioFileType.getNote());
        metadatiFatturaEntity.setTentativiInvio(metadatiInvioFileType.getTentativiInvio());
        metadatiFatturaEntity.setNomeFileMetadati(nomeFileMetadati);
        metadatiFatturaEntity.setContenutoFile(metadatiFile);
       return metadatiFatturaEntity;

    }
}
