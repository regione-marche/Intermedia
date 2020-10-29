package it.extrasys.marche.regione.fatturapa.enti.bridge.giunta.beans;

import it.extrasys.marche.regione.fatturapa.core.api.cache.CacheService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Luigi De Masi <luigi.demasi@extrasys.it> on 24/02/15.
 */
public class MapCacheService implements CacheService {

    private Map<String, Map<String, Object>> caches = new HashMap<String, Map<String, Object>>();

    @Override
    public void put(String cacheName, String key, Object value) {
        getCache(cacheName).put(key, value);
    }

    @Override
    public Object get(String cacheName, String key) {
        return getCache(cacheName).get(key);
    }

    @Override
    public Object remove(String cacheName, String key) {
        return getCache(cacheName).remove(key);
    }

    @Override
    public void clear(String cacheName) {
        getCache(cacheName).clear();
    }

    private Map<String, Object> getCache(String cacheName) {
        if (!caches.containsKey(cacheName)) {
            caches.put(cacheName, new ConcurrentHashMap<String, Object>());
        }
        return caches.get(cacheName);
    }
    
}
