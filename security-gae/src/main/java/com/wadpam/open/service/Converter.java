package com.wadpam.open.service;

import com.google.appengine.api.datastore.Email;
import com.wadpam.open.domain.DApp;
import com.wadpam.open.domain.DAppAdmin;
import com.wadpam.open.json.JBaseObject;
import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.web.BaseConverter;
import com.wadpam.open.json.*;
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
        if (from instanceof DApp) {
            to = convert((DApp) from);
        }
        else if (from instanceof DAppAdmin) {
            to = convert((DAppAdmin) from);
        }
        else {
            throw new UnsupportedOperationException(String.format("No converter for:%s" + from.getClass().getSimpleName()));
        }

        return to;
    }

    // Convert app
    public JApp convert(DApp from) {
        if (null == from) {
            return null;
        }

        final JApp to = new JApp();
        convert(from, to);

        to.setDomain(from.getDomainName());

        Collection<String> emails = new ArrayList<String>(from.getAppAdmins().size());
        for (Email email : from.getAppAdmins())
            emails.add(email.getEmail());
        to.setAppAdmins(emails);

        to.setApiUser(from.getApiUser());
        to.setApiPassword(from.getApiPassword());
        to.setDescription(from.getDescription());

        return to;
    }

    // Convert admin
    public JAppAdmin convert(DAppAdmin from) {
        if (null == from) {
            return null;
        }

        final JAppAdmin to = new JAppAdmin();
        convert(from, to);

        to.setAdminId(from.getAdminId());
        to.setEmail(from.getEmail().getEmail());
        to.setName(from.getName());
        to.setAccountStatus(from.getAccountStatus());
        to.setMaxNumberOfApps(from.getMaxNumberOfApps());

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
    public JCursorPage<?> convert(CursorPage<?> from) {
        final JCursorPage<JBaseObject> to = new JCursorPage<JBaseObject>();

        to.setPageSize(from.getRequestedPageSize());
        to.setCursorKey(from.getCursorKey());
        to.setItems((Collection<JBaseObject>)convert(from.getItems()));

        return to;
    }

}
