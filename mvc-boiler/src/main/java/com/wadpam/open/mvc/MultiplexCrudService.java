package com.wadpam.open.mvc;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author sosandstrom
 */
public class MultiplexCrudService<T extends Object, ID extends Serializable>
        extends CrudServiceWrapper<T, ID, T>
        implements CrudService<T, ID> {

    /** Contains either the key, or a JSON map of serviceName-to-keys */
    protected static final ThreadLocal<String> MULTIPLEX_VALUE = new ThreadLocal<String>();
    
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    
    private final String serviceName;
    private Map<String, CrudService<T, ID>> multiplexMap;

    public MultiplexCrudService(String serviceName) {
        this.serviceName = serviceName;
    }
    
    /**
     * If multiplex value is a JSON-String Map, it picks the key from map.get(serviceName).
     * @return mapped serviceName key, or just the flat multiplex value
     */
    public String getMultiplexKey() {
        final String value = MULTIPLEX_VALUE.get();
        if (null == serviceName || null == value || null == multiplexMap) {
            return null;
        }
        
        if (value.startsWith("{")) {
            try {
                Map map = MAPPER.readValue(value, Map.class);
                Object key = map.get(serviceName);
                return null != key ? key.toString() : null;
            } catch (IOException ignore) {
            }
        }
        
        return value;
    }
    
    @Override
    public CrudService<T, ID> getDelegate() {
        final String key = getMultiplexKey();
        if (null != key && null != multiplexMap) {
            final CrudService<T, ID> service = multiplexMap.get(key);
            if (null != service) {
                return service;
            }
        }
        
        return super.getDelegate();
    }
    
    public static void setMultiplexKey(String key) {
        MULTIPLEX_VALUE.set(key);
    }

    public void setMultiplexMap(Map<String, CrudService<T, ID>> multiplexMap) {
        this.multiplexMap = multiplexMap;
    }

    public void setDefaultService(CrudService<T, ID> defaultService) {
        setDelegate(defaultService);
    }

    public String getServiceName() {
        return serviceName;
    }

}
