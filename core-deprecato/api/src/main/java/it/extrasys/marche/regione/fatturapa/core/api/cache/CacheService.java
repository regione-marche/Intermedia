package it.extrasys.marche.regione.fatturapa.core.api.cache;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 24/02/15.
 */
public interface CacheService {
    
    public void put(String cacheName, String key, Object value);

    public Object get(String cacheName, String key);

    public Object remove(String cacheName, String key);

    public void clear(String cacheName);

}
