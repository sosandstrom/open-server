/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 *
 * @author sosandstrom
 */
public class BlobstoreTaskingHandler extends ChunkingHandler {
    
    private final String url;

    public BlobstoreTaskingHandler(Map<String, ValidationHandler> handlerMap, int chunkSize, String url) {
        super(handlerMap, chunkSize);
        this.url = url;
    }
    
    @Override
    protected void onChunk(String daoClassName, String fileKey, int offset, ValidationHandler fileHandler) {
        TaskOptions task = TaskOptions.Builder
                .withUrl(url)
                .param("offset", Integer.toString(offset))
                .param("limit", Integer.toString(chunkSize))
                .param("blobKey", fileKey)
                .param("daoClassName", daoClassName);
        QueueFactory.getDefaultQueue().add(task);
    }
    
    public void processTask(int offset, int limit, String fileKey, String daoClassName) {
        try {
            InputStream in = openInputStream(fileKey);
            ValidationHandler handler = handlerMap.get(daoClassName);
            JUploadFeedback feedback = new JUploadFeedback();
            Importer.validateMerge(feedback, in, offset, limit, handler, true, true);
        } catch (Exception ex) {
            LOG.error("processing Blob task", ex);
        }
    }

    @Override
    protected InputStream openInputStream(String fileKey) {
        BlobstoreInputStream in = null;
        try {
            BlobKey blobKey = new BlobKey(fileKey);
            in = new BlobstoreInputStream(blobKey);
        } catch (IOException ex) {
            LOG.error("Opening blobstore input stream", ex);
        }
        return in;
    }

}
