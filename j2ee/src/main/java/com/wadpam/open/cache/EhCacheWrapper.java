package com.wadpam.open.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 *
 * @author os
 */
public class EhCacheWrapper implements net.sf.jsr107cache.Cache {
    
    private final Cache ehCache;

    public EhCacheWrapper(Cache cache) {
        this.ehCache = cache;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsKey(Object key) {
        return null != ehCache.get(key);
    }

    @Override
    public boolean containsValue(Object key) {
        return null != ehCache.get(key);
    }

    @Override
    public Object get(Object key) {
        return ehCache.get(key);
    }

    @Override
    public Object put(Object k, Object v) {
        Element element = new Element(k, v);
        ehCache.put(element);
        return element;
    }

    @Override
    public Object remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void putAll(Map plainMap) {
        Map<Serializable, Serializable> map = plainMap;
        Element element;
        ArrayList<Element> elements = new ArrayList<Element>();
        for (Entry<Serializable, Serializable> entry : map.entrySet()) {
            element = new Element(entry.getKey(), entry.getValue());
            elements.add(element);
        }
        ehCache.putAll(elements);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection values() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set entrySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
