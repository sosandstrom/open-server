package com.wadpam.open.blob;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author os
 */
public class FileSystemBlobstoreService implements BlobstoreService {

    @Override
    public String createUploadUrl(String callbackUrl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(BlobKey... blobKeys) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, List<BlobKey>> getUploads(HttpServletRequest request) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void serve(BlobKey blobKey, HttpServletResponse response) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
