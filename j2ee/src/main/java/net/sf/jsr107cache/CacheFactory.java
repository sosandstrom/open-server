/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.jsr107cache;

import java.util.Map;

/**
 *
 * @author os
 */
public interface CacheFactory {
    Cache createCache(Map map);
}
