package com.google.appengine.api.blobstore;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author os
 */
public class BlobstoreInputStream extends InputStream {

    public BlobstoreInputStream(BlobKey blobKey) throws IOException {
    }

    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
