package com.wadpam.open.cache;

import java.util.Map;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author os
 */
public class EhCacheFactory implements CacheFactory {
    
    @Autowired
    private net.sf.ehcache.CacheManager ehManager;
    
    @Autowired
    private String cacheName;
    private net.sf.ehcache.Cache cache;
    
    public EhCacheFactory() {
        
    }
    
    public Cache createCache(Map map) {
        if (null == cache) {
            cache = ehManager.getCache(cacheName);
        }
        return new EhCacheWrapper(cache);
    }
}
