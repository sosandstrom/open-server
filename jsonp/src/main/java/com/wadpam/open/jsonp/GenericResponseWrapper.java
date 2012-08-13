package com.wadpam.open.jsonp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericResponseWrapper extends HttpServletResponseWrapper {
    private static final Logger         LOG = LoggerFactory.getLogger(GenericResponseWrapper.class);

    private final ByteArrayOutputStream output;
    private int                         contentLength;

    public GenericResponseWrapper(HttpServletResponse response) {
        super(response);

        output = new ByteArrayOutputStream();
    }

    public byte[] getData() {
        return output.toByteArray();
    }

    public ServletOutputStream getOutputStream() {
        return new FilterServletOutputStream(output);
    }

    public PrintWriter getWriter() {
        return new PrintWriter(getOutputStream(), true);
    }

    public void setContentLength(int length) {
        this.contentLength = length;
        super.setContentLength(length);
    }

    public int getContentLength() {
        return contentLength;
    }

}
