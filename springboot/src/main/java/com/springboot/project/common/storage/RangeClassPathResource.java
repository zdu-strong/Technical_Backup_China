package com.springboot.project.common.storage;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.BoundedInputStream;
import org.springframework.core.io.ClassPathResource;

public class RangeClassPathResource extends ClassPathResource {
    private long startIndex;
    private long rangeContentLength;

    public RangeClassPathResource(String path, long startIndex, long rangeContentLength) {
        super(path);
        this.startIndex = startIndex;
        this.rangeContentLength = rangeContentLength;
    }

    @Override
    public long contentLength() {
        return this.rangeContentLength;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream input = null;
        try {
            input = super.getInputStream();
            input.skip(this.startIndex);
            return new BoundedInputStream(input, this.rangeContentLength);
        } catch (Throwable e) {
            if (input != null) {
                input.close();
            }
            throw e;
        }
    }

}
