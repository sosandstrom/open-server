/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sf.mardao.core.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sosandstrom
 */
public class BlobstoreTaskingConverter extends CsvConverter<Dao> {
    static final Logger LOG = LoggerFactory.getLogger(BlobstoreTaskingConverter.class);

    private final HashMap<BlobKey, String> fileMap = new HashMap<BlobKey, String>();

    @Override
    public Object preExport(OutputStream out, Object arg, Object preExport, Dao[] daos) {
        try {
            FileService fileService = FileServiceFactory.getFileService();
            AppEngineFile masterFile = fileService.createNewBlobFile("text/csv", "MasterExport.csv");
            boolean lock = true;
            FileWriteChannel channel = fileService.openWriteChannel(masterFile, lock);
            OutputStream outputStream = Channels.newOutputStream(channel);
            
            // write the master headers
            final Object preDao = super.preDao(outputStream, null, null, null, "MasterExport", 
                    Importer.COLUMNS, Collections.EMPTY_MAP, 1, null);
            
            int entityIndex = 0;
            final Map<String, Object> values = new HashMap<String, Object>();
            for (Dao dao : daos) {
                // create a Blob for each dao
                AppEngineFile daoFile = fileService.createNewBlobFile("text/csv", 
                        String.format("%s.csv", dao.getTableName()));
                BlobKey daoKey = fileService.getBlobKey(daoFile);
                fileMap.put(daoKey, dao.getClass().getName());
                
                // write row to master file
                values.put(Importer.COLUMN_DAOCLASSNAME, dao.getClass().getName());
                values.put(Importer.COLUMN_FILEKEY, daoKey.getKeyString());
                super.writeValues(outputStream, null, null, preDao, 
                        Importer.COLUMNS, 0, dao, entityIndex, dao, values);
                entityIndex++;
            }
            super.postDao(outputStream, null, null, preDao, null, null);
            BlobKey masterKey = fileService.getBlobKey(masterFile);
            return masterKey;
        } catch (IOException ex) {
            LOG.error("Creating MasterExport.csv", ex);
        }
        return null;
    }

    @Override
    public Object postExport(OutputStream out, Object arg, Object preExport, Object postExport, Dao[] daos) {
        return preExport;
    }
    
    
    
}
