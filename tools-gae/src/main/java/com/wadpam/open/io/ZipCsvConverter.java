/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sosandstrom
 */
public class ZipCsvConverter<D> extends CsvConverter<D> {

    static final Logger LOG = LoggerFactory.getLogger(ZipCsvConverter.class);
    
    /**
     * Zip the DaoN-yymmdd.csv files together
     * @param out
     * @param argExporter
     * @param preExport
     * @param postExport
     * @param daos
     * @return the BlobKey to the zip file
     */
    @Override
    public Object postExport(OutputStream ignored, Object argExporter, Object preExport, Object postExport, D[] daos) {
        try {
            
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            String zipFilename = "DataStore-" + df.format(new Date()) + ".zip";

            int daoIndex = 0;
            List<BlobInfo> blobInfos = new LinkedList<BlobInfo>();
            final BlobInfoFactory BLOB_INFO_FACTORY = new BlobInfoFactory();
            while (Scheduler.STATE_DONE.equals(TaskScheduler.getMemCached(TaskScheduler.getDaoKey(daoIndex)))) {
                String fileKey = TaskScheduler.getMemCached(Scheduler.getDaoFilenameKey(daoIndex)) + ".csv";
                BlobKey blobKey = (BlobKey) TaskScheduler.getMemCached(fileKey);
                LOG.info("BLOB KEY: {} from: {}", blobKey == null ? "null" : blobKey.getKeyString(), (Scheduler.getDaoFilenameKey(daoIndex) + ".csv"));
                BlobInfo blobInfo = BLOB_INFO_FACTORY.loadBlobInfo(blobKey);

                blobInfos.add(blobInfo);
                daoIndex++;
            }

            if (daoIndex == 1) {
                zipFilename = String.format("%s-%s.zip", blobInfos.get(0).getFilename(), df.format(new Date()));
            }
            LOG.info("============== ZIP FILE NAME: {}", zipFilename);

            final FileService fileService = FileServiceFactory.getFileService();

            AppEngineFile file = fileService.createNewBlobFile("application/zip", zipFilename);
            SafeBlobstoreOutputStream blobOutputStream = new SafeBlobstoreOutputStream(file);
            ZipOutputStream zip = new ZipOutputStream(blobOutputStream);

            for (BlobInfo blob : blobInfos ) {
                zip.putNextEntry(new ZipEntry(blob.getFilename()));

                // read from blob CSV, write to Zip
                BlobstoreInputStream inputStream = new BlobstoreInputStream(blob.getBlobKey());

                byte b[] = new byte[65536];
                int count;
                while (0 < (count = inputStream.read(b))) {
                    zip.write(b, 0, count);
                }
                zip.closeEntry();
                inputStream.close();
            }

            zip.close();
            return fileService.getBlobKey(file);

        } catch (IOException ex) {
            LOG.error("Error zipping CSVs", ex);
        } catch (Exception e) {
            LOG.error("Error zipping CSVs....", e);
        }
        return null;
    }

    public String getOutputFileName(D[] daos) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        final String zipFilename = "DataStore-" + df.format(new Date()) + ".zip";
        return zipFilename;
    }

}
