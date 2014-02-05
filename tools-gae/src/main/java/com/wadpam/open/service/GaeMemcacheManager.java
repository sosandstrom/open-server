/***/
package com.wadpam.open.service;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * @author sophea <a href='mailto:sm@goldengekko.com'> sophea </a>
 * @version $id$ - $Revision$
 * @date 2014
 */
public class GaeMemcacheManager {

    // Using the synchronous cache
    private static final MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    
   // Using the asynchronous cache
    private static final AsyncMemcacheService asyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
    
    
    public static void put(Object key, Object value) {
        asyncCache.put(key, value);
    }
    
    public static void putAll(Map<?,?> paramMap) {
        asyncCache.putAll(paramMap);
    }
    public static void putAll(Map<?,?> paramMap, long millis) {
        asyncCache.putAll(paramMap,Expiration.onDate(new Date(millis)));
        
    }
    
    public static void put(Object key, Object value, long millis) {
        asyncCache.put(key, value, Expiration.onDate(new Date(millis)));
    }
    
    
    public static Object get(Object key) {
        return syncCache.get(key);
    }
    
    public static <T> Map<T, Object> getAll(Collection<T> keys) {
        return syncCache.getAll(keys);
    }
    public static void removeAll(Collection<?> keys) {
        syncCache.deleteAll(keys);
    }
    public static void remove(Object key) {
        syncCache.delete(key);
    }
    public static void clearAll() {
        syncCache.clearAll();
    }
    
    public static AsyncMemcacheService getAsyncMemcacheService() {
        return asyncCache;
    }
    
    public static MemcacheService getMemcacheService() {
        return syncCache;
    }

}
