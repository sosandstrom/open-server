package com.wadpam.open.io;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;

/**
 *
 * @author sosandstrom
 */
public interface ValidationHandler {
    
    Collection<String> getValidColumns();
    Map<String, Integer> getRequiredColumns();
    Map<String, Pattern> getRegexps();
    Map<String, String> getFileToDomainMap();
    
    /**
     * To build internal maps from the header line.
     * @param line 
     */
    public void parseHeader(String[] line) throws Exception;
    
    /**
     * Invoked after all update() invocations.
     * @param merge
     * @param rows 
     */
    void postUpdates(boolean merge, int rows);    
    
    /**
     * Implement your business logic validation here.
     * @param body
     * @param row
     * @param properties
     * @return null if PASS
     */
    JValidationFeedback validate(JUploadFeedback body, int row, Map<String, String> properties);
    
    /**
     * Static and regexp validation implemented in Adapter.
     * @param body
     * @param row
     * @param properties
     * @return null if PASS
     */
    JValidationFeedback validateStatic(JUploadFeedback body, int row, String[] line, Map<String, String> properties);
    
    AbstractCreatedUpdatedEntity update(int row, Map<String, String> properties, boolean mergeIfExist);
    
    public static final String LOC_REGEXP_NUMBER = "\\A[\\d]+\\z";

}
