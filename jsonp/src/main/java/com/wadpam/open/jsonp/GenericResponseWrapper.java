package com.wadpam.open.jsonp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private int                         httpStatus = 200;
    private String                      redirectUrl;

    public GenericResponseWrapper(HttpServletResponse response) {
        super(response);

        output = new ByteArrayOutputStream();
    }

    @Override
    public void sendError(int sc) throws IOException {
        httpStatus = sc;
        super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        httpStatus = sc;
        super.sendError(sc, msg);
    }


    @Override
    public void setStatus(int sc) {
        httpStatus = sc;
        super.setStatus(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        httpStatus = 302;
        redirectUrl = location;
        super.sendRedirect(location);
    }

    public int getStatus() {
        return httpStatus;
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

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
