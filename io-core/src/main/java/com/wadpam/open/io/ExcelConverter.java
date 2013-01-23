package com.wadpam.open.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
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
public class ExcelConverter<D> implements Converter<D>{
    
    private Workbook workbook;
    private static CreationHelper createHelper;
    private Sheet sheet;
    private boolean perDao;
    private static CellStyle dateStyle;
    private static CellStyle boldStyle;
    
    protected Workbook createWorkbook() {
        workbook = new HSSFWorkbook();
        createHelper = workbook.getCreationHelper();
        short dateFormat = createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss");
        dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(dateFormat);
        boldStyle = workbook.createCellStyle();
        Font bold = workbook.createFont();
        bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
        boldStyle.setFont(bold);
        
        return workbook;
    }

    @Override
    public Object preExport(OutputStream out, Object arg, Object preExport, D[] daos) {
        createWorkbook();
        return null;
    }

    @Override
    public Object postExport(OutputStream out, Object arg, Object preExport, Object postExport, D[] daos) {
        flush(out, arg);
        return null;
    }

    @Override
    public Object preDao(OutputStream out, Object arg, Object preExport, Object preDao, 
            String tableName, Iterable<String> columns, Map<String, String> headers, 
            int daoIndex, D dao) {
        perDao = null == workbook;
        if (perDao) {
            workbook = new HSSFWorkbook();
        }
        
        final String safeSheetName = WorkbookUtil.createSafeSheetName(tableName, '_');
        sheet = workbook.createSheet(safeSheetName);
        
        final Row header = sheet.createRow(0);
        String headerName;
        int i = 0;
        for (String col : columns) {
            Cell th = header.createCell(i);
            headerName = headers.get(col);
            th.setCellValue(null != headerName ? headerName : col);
            th.setCellStyle(boldStyle);
            i++;
        }
        
        return String.format("created sheet %s", safeSheetName);
    }

    @Override
    public Object postDao(OutputStream out, Object arg, Object preExport, Object preDao, Object postDao, D dao) {
        if (perDao) {
            flush(out, arg);
        }
        return null;
    }

    @Override
    public Object writeValues(OutputStream out, Object arg, Object preExport, Object preDao, 
            Iterable<String> columns, int daoIndex, D dao, int entityIndex, Object entity, 
            Map<String, Object> values) {
        final Row r = sheet.createRow(entityIndex + 1);
        int i = 0;
        for (String col : columns) {
            Cell td = r.createCell(i);
            Object value = values.get(col);
            setCellValue(td, value);
            i++;
        }
        return r;
    }
    
    protected static void setCellValue(Cell cell, Object value) {
        if (null == value) {
            return;
        }
        
        if (value instanceof Long) {
            cell.setCellValue(((Long) value).intValue());
        }
        else if (value instanceof Integer) {
            cell.setCellValue(((Integer) value).intValue());
        }
        else if (value instanceof Short) {
            cell.setCellValue(((Short) value).intValue());
        }
        else if (value instanceof Byte) {
            cell.setCellValue(((Byte) value).byteValue());
        }
        else if (value instanceof Boolean) {
            cell.setCellValue(((Boolean) value).booleanValue());
        }
        else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            cell.setCellStyle(dateStyle);
        }
        else {
            cell.setCellValue(value.toString());
        }
    }
    
    protected void flush(OutputStream out, Object arg) {
        try {
            workbook.write(out);
        } catch (IOException ex) {
            throw new RuntimeException("flushing workbook", ex);
        }
    }
}
