package it.extrasys.marche.regione.fatturapa.mock;

import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevi.file.beans.FileSdIBaseType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevi.file.beans.RispostaSdIRiceviFileType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevi.file.beans.SdIRiceviFile;

import java.math.BigInteger;

public class SdIRiceviFileImpl implements SdIRiceviFile {
	
	BigInteger identificativoSdI;
	
	

	public SdIRiceviFileImpl(BigInteger identificativoSdI) {
		super();
		this.identificativoSdI = identificativoSdI;
	}



	public RispostaSdIRiceviFileType riceviFile(FileSdIBaseType parametersIn) {
		RispostaSdIRiceviFileType returned = new RispostaSdIRiceviFileType();
		// TODO Auto-generated method stub
		returned.setIdentificativoSdI(identificativoSdI);
		return returned;

	}

}
