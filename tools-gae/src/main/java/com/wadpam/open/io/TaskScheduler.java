/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.wadpam.open.service.EmailSender;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Schedules using Tasks, and handles the Task callbacks.
 * @author sosandstrom
 */
@Controller
@RequestMapping(value={"_admin/{domain}/exporter", "{domain}/_admin/exporter"})
public class TaskScheduler<D> extends Scheduler<D> {
    /** Currently set to reschedule after 8 minutes */
    public static final long MILLIS_TO_RUN = 8L*60L*1000L;
    public static final String KEY_DATE_STRING = "Exporter.Scheduler.dateString";
    private static final MemcacheService MEM_CACHE = MemcacheServiceFactory.getMemcacheService();

    private final String apiUrl;
    private final String basePath;
    private String fromEmail;
    private String fromName;

    /**
     * @param exporter
     * @param serverUrl e.g. http://localhost:8929/api/wbt/
     * @param basePath e.g. /api/{domain}/_admin or /api/_admin/{domain}
     */
    public TaskScheduler(Exporter<D> exporter, String apiUrl, String basePath) {
        this.apiUrl = apiUrl;
        this.basePath = basePath;
        setExporter(exporter);
    }
    
    @Override
    public void preExport(Object arg) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String dateString = df.format(new Date());
        putCached(KEY_DATE_STRING, dateString);
        putCached(KEY_ARG, arg);
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
            ) {

        int status = HttpStatus.CREATED.value();
        long startMillis = System.currentTimeMillis();
        Integer off = offset;
        putCached(KEY_EXPORT_STATUS, STATE_RUNNING);

        // create output stream
        // Create a new Blob file with mime-type "text/plain"
        final String fileName = getMemCached(getDaoFilenameKey(daoIndex)) + ".csv";

        // Get a file service
        final FileService fileService = FileServiceFactory.getFileService();

        try {
            AppEngineFile file = fileService.createNewBlobFile("text/csv", fileName);
            SafeBlobstoreOutputStream out = new SafeBlobstoreOutputStream(file);

            // run for some minutes
            while (null != off && System.currentTimeMillis() < startMillis + MILLIS_TO_RUN) {
                off = exporter.exportDao(out, daoIndex, off, limit);
            }
            
            if (null != off) {
                // we will resume and append
                out.closeChannel(false);
                // re-schedule or zip-schedule?
                final String filePath = file.getFullPath();
                status = HttpStatus.NO_CONTENT.value();
                scheduleExportDaoResume(filePath, daoIndex, off, limit);
            }
            else {
                // close finally
                out.close();
                final BlobKey blobKey = fileService.getBlobKey(file);
                LOG.info("processExportDao {}, blobKey={}", fileName, blobKey);
                putCached(fileName, blobKey);
            }

        }
        catch (Exception any) {
            LOG.error(Integer.toString(offset), any);
            putCached(getDaoKey(daoIndex), STATE_DONE);
        }
        
        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    public void scheduleExportDaoResume(String filePath, int daoIndex, int offset, int limit) {
        // create a task
        TaskOptions task = TaskOptions.Builder.withUrl(
                String.format("%s/exporter/v10/%d", basePath, daoIndex))
                .retryOptions(RetryOptions.Builder.withTaskRetryLimit(0))
                .param("filePath", filePath)
                .param("offset", Integer.toString(offset))
                .param("limit", Integer.toString(limit));
        QueueFactory.getDefaultQueue().add(task);
    }

    @RequestMapping(value="v10/{daoIndex}", method = RequestMethod.POST, params = {"offset", "limit", "filePath"})
    public ResponseEntity resumeExportDao(
            @PathVariable int daoIndex,
            @RequestParam("filePath") String filePath,
            @RequestParam int offset,
            @RequestParam int limit
            ) {
        int status = HttpStatus.CREATED.value();
        long startMillis = System.currentTimeMillis();
        Integer off = offset;
        
        // Get a file service
        final FileService fileService = FileServiceFactory.getFileService();

        try {
            AppEngineFile file = new AppEngineFile(filePath);
            SafeBlobstoreOutputStream out = new SafeBlobstoreOutputStream(file);

            // Create a new Blob file with mime-type "text/plain"
            final String fileName = getMemCached(getDaoFilenameKey(daoIndex)) + ".csv";

            // run for some minutes
            while (null != off && System.currentTimeMillis() < startMillis + MILLIS_TO_RUN) {
                off = exporter.exportDao(out, daoIndex, off, limit);
            }

            // re-schedule or zip-schedule?
            if (null != off) {
                // we will resume and append
                out.closeChannel(false);
                status = HttpStatus.NO_CONTENT.value();
                scheduleExportDaoResume(filePath, daoIndex, off, limit);
            }
            else {
                // close finally
                out.close();
                final BlobKey blobKey = fileService.getBlobKey(file);
                LOG.info("processExportDao {}, blobKey={}", blobKey, blobKey);
                putCached(fileName, blobKey);
            }
        }
        catch (Exception any) {
            LOG.error(filePath, any);
            putCached(getDaoKey(daoIndex), STATE_DONE);
        }
        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    @Override
    protected void schedulePostExport(OutputStream out, Object arg) {
        // create a task
        putCached(Scheduler.KEY_EXPORT_STATUS, Scheduler.STATE_PENDING);
        TaskOptions task = TaskOptions.Builder.withUrl(
                String.format("%s/exporter/v10/done", basePath))
                .retryOptions(RetryOptions.Builder.withTaskRetryLimit(0));
        QueueFactory.getDefaultQueue().add(task);
    }
    
    @RequestMapping(value="v10/done", method = RequestMethod.POST)
    public ResponseEntity processPostExport() {
        try {
            putCached(Scheduler.KEY_EXPORT_STATUS, Scheduler.STATE_RUNNING);
            HashMap<String, Serializable> arg = (HashMap<String, Serializable>) getCached(KEY_ARG);
            String email = (String) arg.get("email");
            BlobKey zipKey = (BlobKey) exporter.postExport(null, exporter, email);
            BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(zipKey);

            String link = String.format("%sblob/v10?attachment=true&key=%s", apiUrl, zipKey.getKeyString());
            String html = String.format("Download <a href='%s'>here</a>", link);
            LOG.debug("EXPORTED BLOB: DOWNLOAD HERE: {}", html);

            EmailSender.sendEmail(fromEmail, fromName, Arrays.asList(email), null, null,
                    String.format("Datastore export - %s", blobInfo.getFilename()),
                    null, html, null, null, null);

            return new ResponseEntity(HttpStatus.OK);
        }
        finally {
            putCached(KEY_EXPORT_STATUS, STATE_DONE);
        }
    }

    @Override
    public Object getCached(Object key) {
        return MEM_CACHE.get(key);
    }

    @Override
    public void putCached(Object key, Object value) {
        MEM_CACHE.put(key, value);
    }

    @Override
    public void removeCached(Object key) {
        MEM_CACHE.delete(key);
    }

    public static Object getMemCached(Object key) {
        return MEM_CACHE.get(key);
    }
    

    // public static String getDaoFilename(int daoIndex) {
    // final String fileName = String.format("Dao%d.csv", daoIndex);
    // return fileName;
    // }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}
