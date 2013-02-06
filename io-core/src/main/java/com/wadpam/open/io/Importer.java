/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author sosandstrom
 */
public class Importer {
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
            // increase row number as we read a line for the column names
            validationHandler.parseHeader(line);
            int i = 0;

            final Map<String, String> properties = new TreeMap<String, String>();

            while (null != (line = reader.readNext()) && (limit == 0 || i < offset + limit)) {
                if (i < offset) {
                    i++;
                    continue;
                }

                properties.clear();

                // do the static regular expressions validation
                validationHandler.validateStatic(body, i, line, properties);

                StringBuffer s = new StringBuffer();
                for(String l : line) {
                    s.append(l).append('|');
                }

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
