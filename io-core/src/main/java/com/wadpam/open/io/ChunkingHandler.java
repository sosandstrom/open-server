/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;

/**
 *
 * @author sosandstrom
 */
public abstract class ChunkingHandler extends ValidationHandlerAdapter {
    
    protected final int chunkSize;
    protected final Map<String, ValidationHandler> handlerMap;

    public ChunkingHandler(Map<String, ValidationHandler> handlerMap, int chunkSize) {
        this.handlerMap = handlerMap;
        this.chunkSize = chunkSize;
    }
    
    @Override
    public AbstractCreatedUpdatedEntity update(int row, Map<String, String> properties, boolean mergeIfExist) {
        BufferedReader br = null;
        try {
            String daoClassName = properties.get(Importer.COLUMN_DAOCLASSNAME);
            ValidationHandler fileHandler = handlerMap.get(daoClassName);
            if (null == fileHandler) {
                LOG.warn("No handler found for {}", daoClassName);
                return null;
            }
            
            String fileKey = properties.get(Importer.COLUMN_FILEKEY);
            InputStream in = openInputStream(fileKey);
            br = new BufferedReader(new InputStreamReader(in));
            int i = 0;
            String s;
            
            while (null != (s = br.readLine())) {
                
                i++;
                if (0 == i % chunkSize) {
                    onChunk(daoClassName, fileKey, i - chunkSize, fileHandler);
                }
            }
            
        } catch (IOException ex) {
            LOG.error("scanning for chunks", ex);
        }
        finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }
        return null;
    }

    protected abstract void onChunk(String daoClassName, String fileKey, int offset, ValidationHandler fileHandler);

    protected abstract InputStream openInputStream(String fileKey);
    
}
