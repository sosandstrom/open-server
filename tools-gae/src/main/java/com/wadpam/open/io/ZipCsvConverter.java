/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

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
            final String zipFilename = getOutputFileName(daos);
            final FileService fileService = FileServiceFactory.getFileService();
            
            AppEngineFile file = fileService.createNewBlobFile("application/zip", zipFilename);
            SafeBlobstoreOutputStream blobOutputStream = new SafeBlobstoreOutputStream(file);
            ZipOutputStream zip = new ZipOutputStream(blobOutputStream);

            Exporter exporter = (Exporter) argExporter;
            
            String friendlyName, csvName;
            int daoIndex = 0;
            BlobKey csvKey;
            for (D dao : daos) {
                
                friendlyName = exporter.getExtractor().getTableName(null, dao);
                zip.putNextEntry(new ZipEntry(String.format("%s.csv", friendlyName)));
                
                // read from blob CSV, write to Zip
                csvName = TaskScheduler.getDaoFilename(daoIndex);
                csvKey = (BlobKey) TaskScheduler.getMemCached(csvName);
                BlobstoreInputStream inputStream = new BlobstoreInputStream(csvKey);
                
                byte b[] = new byte[65536];
                int count;
                while (0 < (count = inputStream.read(b))) {
                    zip.write(b, 0, count);
                }
                zip.closeEntry();
                inputStream.close();
                
                daoIndex++;
            }
            zip.close();
            return fileService.getBlobKey(file);
        } catch (IOException ex) {
            LOG.error("Error zipping CSVs", ex);
        }
        return null;
    }

    public String getOutputFileName(D[] daos) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        final String zipFilename = "DataStore-" + df.format(new Date()) + ".zip";
        return zipFilename;
    }

}
