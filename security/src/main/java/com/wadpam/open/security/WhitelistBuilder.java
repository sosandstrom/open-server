/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.security;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author sosandstrom
 */
public class WhitelistBuilder {
    
    private ArrayList<Map.Entry<String, Collection<String>>> whitelist;
    
    public WhitelistBuilder with(String pathRegex, String... methods) {
        whitelist = new ArrayList<Map.Entry<String, Collection<String>>>();
        return add(pathRegex, methods);
    }
    
    public WhitelistBuilder add(String pathRegex, String... methods) {
        TreeSet<String> methodSet = new TreeSet<String>();
        for (String m : methods) {
            methodSet.add(m.toUpperCase());
        }
        whitelist.add(new AbstractMap.SimpleImmutableEntry<String, Collection<String>>(pathRegex, methodSet));
        return this;
    }
    
    public Collection<Map.Entry<String, Collection<String>>> build() {
        return whitelist;
    }
}
