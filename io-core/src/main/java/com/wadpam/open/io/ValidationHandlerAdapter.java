package com.wadpam.open.io;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sosandstrom
 */
public class ValidationHandlerAdapter implements ValidationHandler {
    protected static final Logger LOG = LoggerFactory.getLogger(ValidationHandlerAdapter.class);
    
    private Collection<String> validColumns;
    private Map<String, Integer> requiredColumns;
    private Map<String, Pattern> regexps;
    private Map<String, String> fileToDomainMap;

    protected final Map<String, Integer> colIndexMap = new HashMap<String, Integer>();
    protected final Map<Integer, String> indexColMap = new HashMap<Integer, String>();
    
    /**
     * To build internal maps from the header line.
     * @param line 
     */
    public void parseHeader(String[] line) throws Exception {
        colIndexMap.clear();
        indexColMap.clear();
        
        int i = 0;
        for (String colName : line) {
            // make sure column is valid
            if (!getValidColumns().contains(colName.trim())) {
                throw new Exception("No such column " + colName);
            }
            colIndexMap.put(colName, i);
            indexColMap.put(i, colName);
            i++;
        }
        
        // make sure all required columns are present
        for (Map.Entry<String, Integer> entry : getRequiredColumns().entrySet()) {
            if (entry.getValue().equals(JValidationFeedback.CODE_REQUIRED) && 
                    !colIndexMap.containsKey(entry.getKey())) {
                throw new Exception("Missing required column " + entry.getKey());
            }
        }
    }

    /**
     * Override to implement post-update behavior
     * @param rows 
     */
    @Override
    public void postUpdates(boolean merge, int rows) {
    }
    

    /**
     * This adapter implementation will PASS everything. 
     * Override to validate with your business logic
     * @param body
     * @param row
     * @param properties
     * @return JValidationFeedback if NOT PASSED, null if PASS
     */
    public JValidationFeedback validate(JUploadFeedback body, int row, Map<String, String> properties) {
        return null;
    }
    
    /**
     * Static and regexp validation implemented in Adapter.
     * @param body
     * @param row
     * @param properties will be populated by this implementation.
     * @return null if PASS
     */
    public JValidationFeedback validateStatic(JUploadFeedback body, int row, String[] line, Map<String, String> properties) {
        JValidationFeedback validationFeedback = null;
        String colName, token, propName;
        Integer colIndex;
        Pattern pattern;
        Matcher matcher;
        
        // populate present properties first:
        for (Entry<String, Integer> entry : colIndexMap.entrySet()) {
            colName = entry.getKey();
            colIndex = entry.getValue();
//            System.out.println(String.format("   mapping '%s' column index %d where length=%d", colName, colIndex, line.length));
            token = line[colIndex];
            propName = getFileToDomainMap().get(colName);
//            System.out.println(String.format("   mapping %s column value %s to property %s", colName, token, propName));
            if (null != propName) {
                properties.put(propName, token);
            }
            else {
                LOG.debug("Unmapped file column {}", colName);
            }
        }
        
        // do the static regular expressions validation
        for (Map.Entry<String, Pattern> entry : getRegexps().entrySet()) {
            colName = entry.getKey();
            colIndex = colIndexMap.get(colName);
            pattern = entry.getValue();
//            System.out.println(String.format("  checking regexp %s on column %s with index %d",
//                    pattern.pattern(), colName, colIndex));
            token = line[colIndex];

            matcher = pattern.matcher(token);
            if (!matcher.find()) {
                final String message = String.format("%s column value %s does not match pattern %s",
                        colName, token, pattern.pattern());
                LOG.debug(message);
                System.out.println(message);
                body.setValid(false);
                validationFeedback = new JValidationFeedback();
                validationFeedback.setColumnName(colName);
                validationFeedback.setId(row);
                validationFeedback.setMessage(message);
                body.getErrors().add(validationFeedback);
            }
        }
        return validationFeedback;
    }
    
    
    /**
     * This adapter implementation will do nothing.
     * Override to update your entities.
     * @param row
     * @param properties
     * @return updated or created Entity.
     */
    public AbstractCreatedUpdatedEntity update(int row, Map<String, String> properties, boolean mergeIfExist) {
        return null;
    }
    
    public static final Date toDate(Long from) {
        if (null == from) {
            return null;
        }
        return new Date(from);
    }
    
    public Collection<String> getValidColumns() {
        return validColumns;
    }

    public void setValidColumns(Collection<String> validColumns) {
        this.validColumns = validColumns;
    }

    public Map<String, Integer> getRequiredColumns() {
        return requiredColumns;
    }

    public void setRequiredColumns(Map<String, Integer> requiredColumns) {
        this.requiredColumns = requiredColumns;
    }

    public Map<String, Pattern> getRegexps() {
        return regexps;
    }

    public void setRegexps(Map<String, Pattern> regexps) {
        this.regexps = regexps;
    }

    public Map<String, String> getFileToDomainMap() {
        return fileToDomainMap;
    }

    public void setFileToDomainMap(Map<String, String> fileToDomainMap) {
        this.fileToDomainMap = fileToDomainMap;
    }
    
}
