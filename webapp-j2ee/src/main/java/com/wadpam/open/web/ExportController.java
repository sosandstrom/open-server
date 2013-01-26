package com.wadpam.open.web;

import com.wadpam.open.service.ExportService;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author os
 */
@Controller
@RequestMapping("{domain}/export")
public class ExportController {
    
    @Autowired
    private ExportService service;
    
    @RequestMapping(value="all.xls")
    public String exportAllAsExcel(HttpServletResponse response) throws IOException {
        OutputStream out = response.getOutputStream();
        final String contentType = "application/vnd.ms-excel";
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment;filename=OpenServer.xls");
        service.exportAll(out, contentType);
        out.flush();
        out.close();
        
        return null;
    }
    
    @RequestMapping(value="all.json")
    public String exportAllAsJson(HttpServletResponse response) throws IOException {
        OutputStream out = response.getOutputStream();
        final String contentType = "application/json";
        response.setContentType(contentType);
        service.exportAll(out, contentType);
        out.flush();
        out.close();
        
        return null;
    }
    
    @RequestMapping(value="{tableName}")
    public String exportDao(HttpServletResponse response,
            @PathVariable String tableName) throws IOException {
        OutputStream out = response.getOutputStream();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", String.format("attachment;filename=%s.csv", tableName));
        
        service.exportDao(out, tableName);
        out.flush();
        out.close();
        
        return null;
    }
    
}
