/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;
import static com.wadpam.open.io.Scheduler.KEY_PRE_EXPORT;
import com.wadpam.open.service.EmailSender;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author sosandstrom
 */
@Controller
@RequestMapping(value={"_admin/{domain}/exporter", "{domain}/_admin/exporter"})
public class TaskScheduler<D> extends Scheduler<D> {
    public static final String KEY_DATE_STRING = "Exporter.Scheduler.dateString";
    private static final MemcacheService MEM_CACHE = MemcacheServiceFactory.getMemcacheService();
    
    private final String basePath;
    private String fromEmail;
    private String fromName;

    /**
     * 
     * @param exporter
     * @param basePath e.g. /api/{domain}/_admin or /api/_admin/{domain}
     */
    public TaskScheduler(Exporter<D> exporter, String basePath) {
        this.basePath = basePath;
        setExporter(exporter);
    }
    
    @Override
    public void preExport(Object argEmail) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String dateString = df.format(new Date());
        putCached(KEY_DATE_STRING, dateString);
        
        // overwrite the preExport value
        putCached(KEY_PRE_EXPORT, argEmail);
    }
    
    @Override
    public void scheduleExportDao(OutputStream notUsed, int daoIndex, int offset, int limitIgnored) {
        // create a task
        TaskOptions task = TaskOptions.Builder.withUrl(
                String.format("%s/exporter/v10/%d", basePath, daoIndex))
                .retryOptions(RetryOptions.Builder.withTaskRetryLimit(0))
                .param("offset", Integer.toString(offset))
                .param("limit", "50");
        QueueFactory.getDefaultQueue().add(task);
    }

    @RequestMapping(value="v10/{daoIndex}", method = RequestMethod.POST, params = {"offset", "limit"})
    public ResponseEntity processExportDao(
            @PathVariable int daoIndex,
            @RequestParam int offset,
            @RequestParam int limit
            ) throws IOException {
        long startMillis = System.currentTimeMillis();
        Integer off = offset;
        
        // create output stream
        // Create a new Blob file with mime-type "text/plain"
        final String fileName = getDaoFilename(daoIndex);
        
        // Get a file service
        final FileService fileService = FileServiceFactory.getFileService();
        
        AppEngineFile file = fileService.createNewBlobFile("text/csv", fileName);
        SafeBlobstoreOutputStream out = new SafeBlobstoreOutputStream(file);
        
        // run for 9 minutes
        while (null != off && System.currentTimeMillis() < startMillis + 9L*60L*1000L) {
            off = exporter.exportDao(out, daoIndex, off, limit);
        }
        
        // FIXME: distribute file or blob key
        out.close();
        
        final BlobKey blobKey = fileService.getBlobKey(file);
        LOG.info("processExportDao {}, blobKey={}", fileName, blobKey);
        putCached(fileName, blobKey);
        
        // re-schedule or zip-schedule?
        int status = HttpStatus.NO_CONTENT.value();
        if (null == off) {
            status = onDone(daoIndex);
        }
        else {
            scheduleExportDao(null, daoIndex, offset, limit);
        }
        
        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    @Override
    protected void schedulePostExport() {
        // create a task
        TaskOptions task = TaskOptions.Builder.withUrl(
                String.format("%s/exporter/v10/done", basePath))
                .retryOptions(RetryOptions.Builder.withTaskRetryLimit(0));
        QueueFactory.getDefaultQueue().add(task);
    }
    
    @RequestMapping(value="v10/done", method = RequestMethod.POST)
    public ResponseEntity processPostExport() {
        String email = (String) getCached(KEY_PRE_EXPORT);
        BlobKey zipKey = (BlobKey) exporter.postExport(null, exporter, email);
        String html = String.format("%s/blob/v10?blobKey=%s", basePath, zipKey.getKeyString());
        EmailSender.sendEmail(fromEmail, fromName, Arrays.asList(email), null, null,
                "Datastore export", null, html, null, null, null);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Override
    public Object getCached(Object key) {
        return MEM_CACHE.get(key);
    }

    @Override
    public void putCached(Object key, Object value) {
        MEM_CACHE.put(key, value);
    }
    
    public static Object getMemCached(Object key) {
        return MEM_CACHE.get(key);
    }
    
    public static String getDaoFilename(int daoIndex) {
        final String fileName = String.format("Dao%d.csv", daoIndex);
        return fileName;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}
