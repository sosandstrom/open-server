package com.google.appengine.api.blobstore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author os
 */
public interface BlobstoreService {
    
    String createUploadUrl(String callbackUrl);
    
    void delete(BlobKey... blobKeys);
    
    java.util.Map<java.lang.String,java.util.List<BlobKey>> getUploads(HttpServletRequest request);
    
    void serve(BlobKey blobKey,
           HttpServletResponse response)
           throws java.io.IOException;
}
