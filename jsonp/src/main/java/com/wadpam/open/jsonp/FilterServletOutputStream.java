package com.wadpam.open.jsonp;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterServletOutputStream extends ServletOutputStream {
    private static final Logger LOG = LoggerFactory.getLogger(FilterServletOutputStream.class);

    private final OutputStream  stream;

    public FilterServletOutputStream(OutputStream output) {
        stream = output; // new DataOutputStream(output);
    }

    @Override
    public void flush() throws IOException {
        stream.flush();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void write(int b) throws IOException {
        stream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b, off, len);
    }
}
