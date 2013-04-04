package com.wadpam.open.web.admin;

import java.util.Map;

/**
 * 
 * @author os
 */
public interface AdminTask {
    Object processTask(String taskName, Map<String, String[]> parameterMap);
}


