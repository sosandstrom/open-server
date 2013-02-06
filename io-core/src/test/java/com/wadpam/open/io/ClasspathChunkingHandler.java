/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 *
 * @author sosandstrom
 */
public class ClasspathChunkingHandler extends ChunkingHandler {

    public ClasspathChunkingHandler(Map<String, ValidationHandler> handlerMap, int chunkSize) {
        super(handlerMap, chunkSize);
    }

    @Override
    protected void onChunk(String daoClassName, String fileKey, int offset, ValidationHandler fileHandler) {
        JUploadFeedback feedback = new JUploadFeedback();
        InputStream in = openInputStream(fileKey);
        try {
            Importer.validateMerge(feedback, in, offset, this.chunkSize, fileHandler, true, true);
        } catch (UnsupportedEncodingException ex) {
            LOG.error("Importing chunk", ex);
        } catch (IOException ex) {
            LOG.error("Importing chunk", ex);
        } catch (Exception ex) {
            LOG.error("Importing chunk", ex);
        }
    }

    @Override
    protected InputStream openInputStream(String fileKey) {
        return getClass().getResourceAsStream(fileKey);
    }

}
