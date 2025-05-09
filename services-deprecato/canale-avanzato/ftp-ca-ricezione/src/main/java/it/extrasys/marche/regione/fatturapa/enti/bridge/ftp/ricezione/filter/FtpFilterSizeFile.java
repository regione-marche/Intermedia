package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.filter;

import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;

//Prende solo i file di grandezza < 150 Mb
public class FtpFilterSizeFile<T> implements GenericFileFilter<T> {

    private Integer fileMaxZip;

    @Override
    public boolean accept(GenericFile<T> genericFile) {
        if (genericFile.getFileLength() < (fileMaxZip * 1024 * 1024)) {
            return true;
        }
        return false;
    }

    public Integer getFileMaxZip() {
        return fileMaxZip;
    }

    public void setFileMaxZip(Integer fileMaxZip) {
        this.fileMaxZip = fileMaxZip;
    }
}
