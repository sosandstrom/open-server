package net.sf.jsr107cache;

/**
 *
 * @author os
 */
public class CacheManager {
    private static CacheManager _INSTANCE;
    
    private final CacheFactory _FACTORY;
    
    private CacheManager(CacheFactory factory) {
        this._FACTORY = factory;
        CacheManager._INSTANCE = this;
    }
    
    public static CacheManager getInstance() {
        return _INSTANCE;
    }
    
    public CacheFactory getCacheFactory() throws CacheException {
        return _FACTORY;
    }
}
