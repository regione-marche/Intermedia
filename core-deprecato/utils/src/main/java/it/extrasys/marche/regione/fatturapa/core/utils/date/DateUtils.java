package it.extrasys.marche.regione.fatturapa.core.utils.date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 28/01/15.
 */
public class DateUtils {

    private static final String DEFAULT_FORMAT="dd/MM/yyyy";
    private static final Locale DEFAULT_LOCALE=Locale.ITALIAN;


    public static Date parseDate(String date) throws ParseException{
        return parseDate(date,DEFAULT_FORMAT,DEFAULT_LOCALE);
    }

    public static Date parseDate(String date, Locale locale) throws ParseException{
        return parseDate(date, DEFAULT_FORMAT, locale);
    }

    public static Date parseDate(String date, String format) throws ParseException{
        return parseDate(date,format,DEFAULT_LOCALE);
    }

    public static Date parseDate(String date, String format, Locale locale) throws ParseException{
        DateFormat df = new SimpleDateFormat(format, locale);
        return df.parse(date);
    }

    /*
     * Converts java.util.Date to javax.xml.datatype.XMLGregorianCalendar
     */
    public static XMLGregorianCalendar DateToXMLGregorianCalendar(Date date){
        GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(date);
        XMLGregorianCalendar xmlCalendar = null;
        try {
            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
        } catch (DatatypeConfigurationException ex) {
            return null;
        }
        return xmlCalendar;
    }

    /*
     * Converts XMLGregorianCalendar to java.util.Date in Java
     */
    public static Date XMLGregorianCalendarToDate(XMLGregorianCalendar calendar){
        if(calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }

    public static String getDateAsString(String format,Date date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return  dateFormat.format(date);
    }

}
