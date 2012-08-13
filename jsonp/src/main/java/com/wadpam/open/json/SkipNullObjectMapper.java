package com.wadpam.open.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * @author sosandstrom
 */
public class SkipNullObjectMapper extends ObjectMapper {

    public void init() {
        getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
    }
}
