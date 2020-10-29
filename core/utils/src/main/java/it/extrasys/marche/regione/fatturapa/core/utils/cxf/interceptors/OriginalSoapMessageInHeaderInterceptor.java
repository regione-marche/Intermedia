package it.extrasys.marche.regione.fatturapa.core.utils.cxf.interceptors;

import org.apache.cxf.io.CachedOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

import java.io.InputStream;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 18/02/15.
 * 
 * Cxf Interceptor  che inserisce negli header del messaggio cxf in ingresso il
 * messaggio soap oringinale,
 *  
 */
public class OriginalSoapMessageInHeaderInterceptor extends AbstractSoapInterceptor{


    public OriginalSoapMessageInHeaderInterceptor()
    {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage ( SoapMessage message ) throws Fault
    {
        String originalSoapMessage = "";
        try
        {
            // now get the request xml
            InputStream is = message.getContent(InputStream.class );
            CachedOutputStream os = new CachedOutputStream();
            IOUtils.copy(is, os);
            os.flush ( );
            message.setContent (  InputStream.class, os.getInputStream ( ) );
            is.close ( );
            originalSoapMessage = IOUtils.toString ( os.getInputStream ( ));
            os.close ( );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace ();
        }
        message.getExchange().put("originalSoapMessage", originalSoapMessage);

    }

}
