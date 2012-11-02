package com.wadpam.open.web;

import com.google.appengine.api.datastore.GeoPt;
import com.wadpam.open.json.JBaseObject;
import com.wadpam.open.json.JCursorPage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.wadpam.open.json.JLocation;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;
import net.sf.mardao.core.domain.AbstractLongEntity;
import net.sf.mardao.core.geo.DLocation;


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

    // Convert iterable
    public Collection<?> convert(Iterable<?> from) {
        if (null == from)
            return new ArrayList<JBaseObject>();

        final Collection<JBaseObject> returnValue = new ArrayList<JBaseObject>();

        JBaseObject to;
        for (Object o : from) {
            to = convertBase(o);
            returnValue.add(to);
        }
        
        return returnValue;
    }

    // Convert GAE GeoPt
    static public JLocation convert(GeoPt from) {
        if (null == from) {
            return null;
        }

        return new JLocation(from.getLatitude(), from.getLongitude());
    }

    // Convert Mardao DLocation
    static public JLocation convert(DLocation from) {
        if (null == from) {
            return null;
        }

        return new JLocation(from.getLatitude(), from.getLongitude());
    }
    
    public <D extends AbstractLongEntity> JCursorPage<JBaseObject> convertPage(CursorPage<D, Long> from) {
        final JCursorPage<JBaseObject> to = new JCursorPage<JBaseObject>();
        
        to.setPageSize(from.getRequestedPageSize());
        to.setCursorKey(from.getCursorKey());
        to.setItems((Collection<JBaseObject>)convert((Collection<AbstractLongEntity>) from.getItems()));
        
        return to;
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

    public static Collection<Long> toLongs(Collection<String> from) {
        if (null == from) {
            return null;
        }

        final Collection<Long> to = new ArrayList<Long>();

        for(String s : from) {
            try {
                to.add(Long.parseLong(s));
            }
            catch (NumberFormatException sometimes) {
                to.add(null);
            }
        }

        return to;
    }

    public static Collection<String> toString(Collection<Long> from) {
        if (null == from) {
            return null;
        }

        final Collection<String> to = new ArrayList<String>(from.size());

        for(Long l : from) {
            to.add(l.toString());
        }

        return to;
    }
    
    
}
