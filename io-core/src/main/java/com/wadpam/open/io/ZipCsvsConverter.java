package com.wadpam.open.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.management.RuntimeErrorException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;

/**
 *
 * @author os
 */
public class ZipCsvsConverter<D> extends CsvConverter<D> {

    @Override
    public Object preDao(OutputStream out, Object arg, Object preExport, Object preDao, 
            String tableName, Iterable<String> columns, Map<String, String> headers, 
            int daoIndex, D dao) {

        final String safeSheetName = WorkbookUtil.createSafeSheetName(tableName, '_');
        try {
            ((ZipOutputStream) out).putNextEntry(new ZipEntry(safeSheetName + ".csv"));

            return super.preDao(out, arg, preExport, preDao, tableName, columns, headers, daoIndex, dao);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object postDao(OutputStream out, Object arg, Object preExport, Object preDao, Object postDao, D dao) {
        Object res = super.postDao(out, arg, preExport, preDao, postDao, dao);

        try {
            ((ZipOutputStream) out).closeEntry();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

}
