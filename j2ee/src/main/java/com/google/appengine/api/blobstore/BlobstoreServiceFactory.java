package com.google.appengine.api.blobstore;

import com.wadpam.open.blob.FileSystemBlobstoreService;

/**
 *
 * @author os
 */
public class BlobstoreServiceFactory {
    private static final BlobstoreService _SERVICE = new FileSystemBlobstoreService();
    
    public static BlobstoreService getBlobstoreService() {
        return _SERVICE;
    }
}
