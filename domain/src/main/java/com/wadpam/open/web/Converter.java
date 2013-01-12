package com.wadpam.open.web;

import com.wadpam.open.domain.DAppDomain;
import com.wadpam.open.json.JAppDomain;
import com.wadpam.open.json.JBaseObject;
import com.wadpam.open.json.JCursorPage;
import net.sf.mardao.core.CursorPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class implement various methods for converting from domain object to JSON objects.
 * @author mattiaslevin
 */
public class Converter extends BaseConverter {

    static final Logger LOG = LoggerFactory.getLogger(Converter.class);


    @Override
    public JBaseObject convertBase(Object from) {
        if (null == from) {
            return null;
        }

        JBaseObject to;
        if (from instanceof DAppDomain) {
            to = convert((DAppDomain) from);
        }
        else {
            throw new UnsupportedOperationException(String.format("No converter for:%s" + from.getClass().getSimpleName()));
        }

        return to;
    }

    // Convert domain
    public JAppDomain convert(DAppDomain from) {
        if (null == from) {
            return null;
        }

        final JAppDomain to = new JAppDomain();
        convert(from, to);
        to.setDomain(from.getAppDomain());
        to.setUsername(from.getUsername());
        to.setPassword(from.getPassword());
        to.setDescription(from.getDescription());
        if (null != from.getEmail())
            to.setEmail(from.getEmail().getEmail());
        to.setAnalyticsTrackingCode(from.getAnalyticsTrackingCode());

        return to;
    }

    // Convert collection
    public Collection<?> convert(Collection<?> from) {
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

    // Convert pages
    public JCursorPage<?> convert(CursorPage<?, ?> from) {
        final JCursorPage<JBaseObject> to = new JCursorPage<JBaseObject>();

        to.setPageSize(from.getRequestedPageSize());
        to.setCursorKey(from.getCursorKey());
        to.setItems((Collection<JBaseObject>)convert(from.getItems()));

        return to;
    }
}
