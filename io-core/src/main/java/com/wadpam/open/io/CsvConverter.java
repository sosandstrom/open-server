package com.wadpam.open.io;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author os
 */
public class CsvConverter<D> implements Converter<D> {
    
    protected final ThreadLocal<PrintWriter> pw = new ThreadLocal<PrintWriter>();

    public static String escapeCsv(Object s) {
        if (null == s) {
            return null;
        }
        // first, escape all double quotes:
        final String escaped = s.toString().replaceAll("\\\"", "\"\"");
        // then, qoute the string:
        final String quoted = String.format("\"%s\"", escaped);
        
        return quoted;
    }
    
    @Override
    public Object preExport(OutputStream out, Object arg, Object preExport, D[] daos) {
        return null;
    }

    @Override
    public Object postExport(OutputStream out, Object arg, Object preExport, Object postExport, D[] daos) {
        return null;
    }

    @Override
    public Object preDao(OutputStream out, Object arg, Object preExport, Object preDao, 
            String tableName, Iterable<String> columns, int daoIndex, D dao) {
        try {
            pw.set(new PrintWriter(new OutputStreamWriter(out, "UTF-8")));
        }
        catch (UnsupportedEncodingException willNeverHappen) {}
        final HashMap<String, Object> headers = new HashMap<String, Object>();
        for (String col : columns) {
            headers.put(col, col);
        }
        return writeValues(out, arg, preExport, preDao, columns, -1, dao, -1, null, headers);
    }

    @Override
    public Object postDao(OutputStream out, Object arg, Object preExport, Object preDao, Object postDao, D dao) {
        pw.get().flush();
        pw.remove();
        return null;
    }

    @Override
    public Object writeValues(OutputStream out, Object arg, Object preExport, Object preDao, 
            Iterable<String> columns, int daoIndex, D dao, int entityIndex, Object entity, 
            Map<String, Object> values) {
        String s = null;
        // skip this entity / row ?
        if (null != values) {
            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;
            for (String col : columns) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    sb.append(',');
                }
                sb.append(escapeCsv(values.get(col)));
            }

            s = sb.toString();
            pw.get().println(s);
        }
        return s;
    }

}
