package it.extrasys.marche.regione.fatturapa.mock.sdi.ricevi.fatture;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevi.file.beans.RispostaSdIRiceviFileType;
import it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevi.file.beans.SdIRiceviFile;
import it.extrasys.marche.regione.fatturapa.core.utils.date.DateUtils;
import it.extrasys.marche.regione.fatturapa.mock.sdi.ricevi.DBUtils;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SdIRiceviFileMock implements SdIRiceviFile {

	private String connectionUrl;
	private String driverName;
	private String log;
	private String transactionMode;
	private String connectionFactoryProperties;
	private String connectionUserName;
	private String connectionPassword;

	@Override
	public RispostaSdIRiceviFileType riceviFile(it.extrasys.marche.regione.fatturapa.contracts.sdi.ricevi.file.beans.FileSdIBaseType parametersIn) {
		HashMap<String, String> properties = new HashMap<String, String>();

		properties.put("openjpa.ConnectionURL", connectionUrl);
		properties.put("openjpa.ConnectionDriverName", driverName);
		properties.put("openjpa.Log", log);
		properties.put("openjpa.TransactionMode", transactionMode);
		properties.put("openjpa.ConnectionFactoryProperties", connectionFactoryProperties);
		properties.put("openjpa.ConnectionUserName", connectionUserName);
		properties.put("openjpa.ConnectionPassword", connectionPassword);

		RispostaSdIRiceviFileType returned = new RispostaSdIRiceviFileType();
		BigInteger identificativoSdI = BigInteger.valueOf(DBUtils.getNextIdentificativoAttivaSdI(properties));
		returned.setDataOraRicezione(DateUtils.DateToXMLGregorianCalendar(new Date()));
		returned.setIdentificativoSdI(identificativoSdI);

		return returned;
	}

	public String getConnectionUrl() {
		return connectionUrl;
	}

	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getConnectionFactoryProperties() {
		return connectionFactoryProperties;
	}

	public void setConnectionFactoryProperties(String connectionFactoryProperties) {
		this.connectionFactoryProperties = connectionFactoryProperties;
	}

	public String getConnectionUserName() {
		return connectionUserName;
	}

	public void setConnectionUserName(String connectionUserName) {
		this.connectionUserName = connectionUserName;
	}

	public String getConnectionPassword() {
		return connectionPassword;
	}

	public void setConnectionPassword(String connectionPassword) {
		this.connectionPassword = connectionPassword;
	}
}
