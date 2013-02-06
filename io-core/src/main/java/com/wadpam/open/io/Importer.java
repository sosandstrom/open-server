/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;

/**
 *
 * @author sosandstrom
 */
public class Importer extends ValidationHandlerAdapter {
    
    public static final String COLUMN_DAOCLASSNAME = "DaoClassName";
    public static final String COLUMN_FILEKEY = "FileKey";
    public static final List<String> COLUMNS = Arrays.asList(COLUMN_DAOCLASSNAME, COLUMN_FILEKEY);
    
    private ValidationHandler chunkingHandler;
    private Map<String, ValidationHandler> handlerMap;
    
    public Importer() {
        // valid columns are the two above only
        setValidColumns(COLUMNS);
        
        // one-to-one mapping of csv columns and properties
        final Map<String, String> map = new HashMap<String, String>();
        for (String col : COLUMNS) {
            map.put(col, col);
        }
        setFileToDomainMap(map);
        
        final Map<String, Integer> required = new HashMap<String, Integer>();
        for (String col : COLUMNS) {
            required.put(col, JValidationFeedback.CODE_REQUIRED);
        }
        setRequiredColumns(required);
        
        setRegexps(Collections.EMPTY_MAP);
    }
    
    /**
     * Imports a multi-file export based on the following master CSV:
     * || DaoClassName                 || FileKey              ||
     *  | com.wadpam.open.SampleDaoBean | SJEUJHDUCU9438djSJDH |
     * @param <D>
     * @param masterIn
     * @param handlerMap
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws Exception 
     */
    public <D extends Object> void importMaster(InputStream masterIn, 
            Map<String, ValidationHandler> handlerMap, ValidationHandler chunkingHandler) throws UnsupportedEncodingException, IOException, Exception {
        this.chunkingHandler = chunkingHandler;
        this.handlerMap = handlerMap;
        JUploadFeedback feedback = new JUploadFeedback();
        
        validateMerge(feedback, masterIn, 0, -1, this, true, true);
    }

    @Override
    public AbstractCreatedUpdatedEntity update(int row, Map<String, String> properties, boolean mergeIfExist) {
        return chunkingHandler.update(row, properties, mergeIfExist);
    }
    
    public static void validateMerge(JUploadFeedback body, InputStream in, Integer offset, Integer limit, 
            ValidationHandler validationHandler, boolean persist, boolean merge) throws UnsupportedEncodingException, IOException, Exception {
        CSVReader reader = null;

        body.setValid(true);
        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = 0; // with attempt to read all available lines
        }

        try {
            reader = new CSVReader(new InputStreamReader(in, "UTF-8"));
            String line[] = null;
            // first line is column names
            line = reader.readNext();
            LOG.info("First header is {}", line);
            // increase row number as we read a line for the column names
            validationHandler.parseHeader(line);
            int i = 0;

            final Map<String, String> properties = new TreeMap<String, String>();

            while (null != (line = reader.readNext()) && (limit <= 0 || i < offset + limit)) {
                if (i < offset) {
                    i++;
                    continue;
                }

                properties.clear();

                // do the static regular expressions validation
                validationHandler.validateStatic(body, i, line, properties);

                // business logic validation
                validationHandler.validate(body, i, properties);

                if (persist && body.isValid()) {
                    validationHandler.update(i, properties, merge);
                }
                else if (persist) {
                    // if we are applying changes, and validation failed, we should stop
                    // this should not happen.
                    break;
                }
                i++;
            }
            validationHandler.postUpdates(persist, i);

        }
        finally {
            if (reader != null) {
                try { reader.close(); } catch(Exception e) {}
            }
        }
    }

}
