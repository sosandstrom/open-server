package com.wadpam.open.io;

import com.wadpam.open.json.SkipNullObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author os
 */
public class JsonConverter<D> implements Converter<D> {
    
    private final ThreadLocal<PrintWriter> pw = new ThreadLocal<PrintWriter>();
    private final ThreadLocal<Boolean> perDao = new ThreadLocal<Boolean>();
    private final ObjectMapper MAPPER = new SkipNullObjectMapper();

    @Override
    public Object preExport(OutputStream out, Object arg, Object preExport, D[] daos) {
        pw.set(new PrintWriter(out));
        final String s = "{[";
        
        pw.get().println(s);
        
        return s;
    }

    @Override
    public Object postExport(OutputStream out, Object arg, Object preExport, Object postExport, D[] daos) {
        final String s = String.format("]}");
        pw.get().println(s);
        
        pw.get().flush();
        pw.remove();
        return s;
    }

    @Override
    public Object preDao(OutputStream out, Object arg, Object preExport, Object preDao, 
            String tableName, Iterable<String> columns, Map<String, String> headers, 
            int daoIndex, D dao) {
        perDao.set(null == pw.get());
        if (perDao.get()) {
            pw.set(new PrintWriter(out));
        }
        final String s = String.format("%s{\"tableName\":\"%s\", \"entities\":[", 
                0 < daoIndex ? "," : "", 
                tableName);
        pw.get().println(s);
        
        return s;
    }

    @Override
    public Object postDao(OutputStream out, Object arg, Object preExport, Object preDao, Object postDao, D dao) {
        final String s = String.format("]}");
        pw.get().println(s);
        
        pw.get().flush();
        if (perDao.get()) {
            pw.remove();
        }
        return s;
    }

    @Override
    public Object writeValues(OutputStream out, Object arg, Object preExport, Object preDao, 
            Iterable<String> columns, int daoIndex, D dao, int entityIndex, Object entity, 
            Map<String, Object> values) {
        String s = null;
        // skip this entity / row ?
        if (null != values) {
            StringBuffer sb = new StringBuffer();
            try {
                // prefix with comma?
                if (0 < entityIndex) {
                    sb.append(',');
                }
                sb.append(MAPPER.writeValueAsString(values));
                s = sb.toString();
                
            } catch (IOException ex) {
            }
            pw.get().println(s);
        }
        return s;
    }

}
