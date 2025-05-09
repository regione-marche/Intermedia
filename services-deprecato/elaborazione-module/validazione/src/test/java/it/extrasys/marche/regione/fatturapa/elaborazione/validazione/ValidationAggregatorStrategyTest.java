package it.extrasys.marche.regione.fatturapa.elaborazione.validazione;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public class ValidationAggregatorStrategyTest  {

	@Test
	public void testOk() {
		
		Exchange exchange = getExchange("idDoc");
		
		assertTrue(ValidationAggregatorStrategy.checkHeaders(exchange));
		
	}
	
	@Test
	public void testKoForMissingId() {
		
		Exchange exchange = getExchange("");
		
		assertFalse(ValidationAggregatorStrategy.checkHeaders(exchange));
		
	}
	
	@Test
	public void testKoForNullId() {
		
		Exchange exchange = getExchange(null);
		
		assertFalse(ValidationAggregatorStrategy.checkHeaders(exchange));
		
	}
	@Test
	@Ignore("dataDocumento non è più oggetto di validazione")
	public void testKoForMissingDate() {
		
		Exchange exchange = getExchange("idDoc");
		
		assertFalse(ValidationAggregatorStrategy.checkHeaders(exchange));
		
	}
	
	@Test
	@Ignore("dataDocumento non è più oggetto di validazione")
	public void testKoForNullDate() {
		
		Exchange exchange = getExchange("idDoc");
		
		assertFalse(ValidationAggregatorStrategy.checkHeaders(exchange));
		
	}

	private Exchange getExchange(String idDocumento) {
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setHeader("idDocumento", idDocumento);
		//exchange.getIn().setHeader("dataDocumento", dataDocumento);
		return exchange;
	}
}
