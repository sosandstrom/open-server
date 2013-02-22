/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;

/**
 *
 * @author sosandstrom
 */
public class SafeBlobstoreOutputStream extends OutputStream {
    
    public static final long TTL = 25L*1000L;
    private final FileService FILE_SERVICE = FileServiceFactory.getFileService();
    
    private final AppEngineFile file;
    private FileWriteChannel channel = null;
    private OutputStream outputStream = null;
    private long timeOpened = -1L;

    public SafeBlobstoreOutputStream(AppEngineFile file) {
        this.file = file;
    }
    
    

    @Override
    public void write(int i) throws IOException {
        
        refresh();
        
        // write the data
        outputStream.write(i);
    }

    @Override
    public void write(byte[] b) throws IOException {
        refresh();
        
        // write the data
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        refresh();
        
        // write the data
        outputStream.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        // if not open, open to closeFinally
        openChannel();
        
        closeChannel(true);
    }
    
    private void refresh() throws IOException {
        // check if existing channel is too old
        final long now = System.currentTimeMillis();
        
        if (null != channel && timeOpened + TTL <= now) {
            closeChannel(false);
        }
        
        // make sure we have a channel to write to
        if (null == channel) {
            openChannel();
        }
        
    }

    private void closeChannel(boolean closeFinally) throws IOException {
        if (null != channel) {
            try {
                outputStream.flush();
                outputStream.close();
                timeOpened = -1L;
                if (closeFinally) {
                    channel.closeFinally();
                }
                else {
                    channel.close();
                }
            }
            finally {
                outputStream = null;
                channel = null;
            }
        }
    }

    private void openChannel() throws IOException {
        final boolean LOCK = true;
        if (null == channel) {
            try {
                channel = FILE_SERVICE.openWriteChannel(file, LOCK);
                outputStream = Channels.newOutputStream(channel);
                timeOpened = System.currentTimeMillis();
            }
            finally {
                channel = null;
                outputStream = null;
            }
        }
    }

    
}
