package it.extrasys.marche.regione.fatturapa.patch.test;

import org.junit.Test;

/**
 * Created by agosteeno on 11/09/15.
 */
public class ProveVarie {

    private static String identificativoSdISingolo = "12345678";
    private static String identificativoSdIMultiplo = "12345678, 87654321, 12344321";

    private static String identificativoSdIMultiploConRobaStrana = "12345678, 87654321, 12344321, asd, 234awexadfgààèè+.     ,";

    @Test
    public void provaSplitString(){

        String [] arraySplitted = identificativoSdIMultiplo.split(",");

        String [] arraySplittedRobaStrana = identificativoSdIMultiploConRobaStrana.split(",");

        for(int i = 0; i < arraySplitted.length; i++){

            System.out.println(" ITERAZIONE " + i + "; identificativoSdI trovato: [" + arraySplitted[i] + "]");
        }

        for(int i = 0; i < arraySplitted.length; i++){

            System.out.println(" CON TRIM, ITERAZIONE " + i + "; identificativoSdI trovato: [" + arraySplitted[i].trim() + "]");
        }

        for(int i = 0; i < arraySplittedRobaStrana.length; i++){

            System.out.println(" CON TRIM e ROBA STRANA, ITERAZIONE " + i + "; identificativoSdI trovato: [" + arraySplittedRobaStrana[i].trim() + "]");
        }

    }
}
