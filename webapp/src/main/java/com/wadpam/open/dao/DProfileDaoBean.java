/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.dao;

import com.wadpam.open.domain.DProfile;
import com.wadpam.open.user.dao.DOpenUserDaoBean;
import com.wadpam.open.user.domain.DOpenUser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sosandstrom
 */
public class DProfileDaoBean extends DOpenUserDaoBean {

    public static final String COLUMN_NAME_PHONENUMBER = "phoneNumber";

    @Override
    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<String>(super.getColumnNames());
        columnNames.add(COLUMN_NAME_PHONENUMBER);
        return columnNames;
    }
    
    @Override
    protected Object getDomainProperty(DOpenUser domain, String name) {
        if (COLUMN_NAME_PHONENUMBER.equals(name)) {
            return ((DProfile) domain).getPhoneNumber();
        }
        return super.getDomainProperty(domain, name);
    }

    @Override
    public Class getColumnClass(String columnName) {
        if (COLUMN_NAME_PHONENUMBER.equals(columnName)) {
            return String.class;
        }
        return super.getColumnClass(columnName);
    }
    
    @Override
    protected void setDomainProperty(final DOpenUser domain, final String name, final Object value) {
        // simple key?
        if (COLUMN_NAME_PHONENUMBER.equals(name)) {
            ((DProfile) domain).setPhoneNumber((java.lang.String) value);
        }
        else {
            super.setDomainProperty(domain, name, value);
        }
    }
}
