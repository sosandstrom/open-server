package com.wadpam.open.web;

import com.wadpam.open.json.JBaseObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;
import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 *
 * @author os
 */
public abstract class BaseConverter {

    public abstract JBaseObject convertBase(Object from);
    
    public static void convert(AbstractCreatedUpdatedEntity from, JBaseObject to) {
        if (null == from || null == to) {
            return;
        }

        to.setCreatedBy(from.getCreatedBy());
        to.setCreatedDate(toLong(from.getCreatedDate()));
        to.setUpdatedBy(from.getUpdatedBy());
        to.setUpdatedDate(toLong(from.getUpdatedDate()));
    }

    public static void convert(AbstractLongEntity from, JBaseObject to) {
        if (null == from || null == to) {
            return;
        }

        convert((AbstractCreatedUpdatedEntity) from, to);
        
        to.setId(toString(from.getId()));
    }

    public static void convert(JBaseObject from, AbstractCreatedUpdatedEntity to) {
        if (null == from || null == to) {
            return;
        }

        to.setCreatedBy(from.getCreatedBy());
        to.setCreatedDate(toDate(from.getCreatedDate()));
        to.setUpdatedBy(from.getUpdatedBy());
        to.setUpdatedDate(toDate(from.getUpdatedDate()));
    }
    
    public static void convert(JBaseObject from, AbstractLongEntity to) {
        if (null == from || null == to) {
            return;
        }

        to.setId(toLong(from.getId()));
    }
    
    public Collection<JBaseObject> convert(Iterable<Object> from) {
        final Collection<JBaseObject> returnValue = new ArrayList<JBaseObject>();
        
        JBaseObject to;
        for (Object o : from) {
            to = convertBase(from);
            returnValue.add(to);
        }
        
        return returnValue;
    }

    public static Long toLong(Date from) {
        if (null == from) {
            return null;
        }
        return from.getTime();
    }

    public static Long toLong(String from) {
        if (null == from) {
            return null;
        }
        return Long.parseLong(from);
    }

    public static Date toDate(Long from) {
        if (null == from) {
            return null;
        }
        return new Date(from);
    }

    public static String toString(Long from) {
        if (null == from) {
            return null;
        }
        return Long.toString(from);
    }

    
}
