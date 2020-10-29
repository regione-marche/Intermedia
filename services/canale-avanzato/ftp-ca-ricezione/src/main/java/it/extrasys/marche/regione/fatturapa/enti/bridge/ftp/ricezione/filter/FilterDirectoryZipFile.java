package it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.filter;

import it.extrasys.marche.regione.fatturapa.enti.bridge.ftp.ricezione.utils.FtpConstants;

import java.io.File;
import java.io.FileFilter;

//filtra solo le directory degli enti a partire dalla root
public class FilterDirectoryZipFile implements FileFilter {
    @Override
    public boolean accept(File genericFile) {
        if(genericFile.isDirectory() && !FtpConstants.DOWNLOAD.equalsIgnoreCase(genericFile.getName()) ) {
            return true;
        }
        return false;
    }
}
