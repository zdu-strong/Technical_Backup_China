package com.springboot.project.common.ClassPathStorage;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.springboot.project.common.storage.RangeClassPathResource;

/**
 * Define file only
 */
public enum ClassPathStorageEnum {

    EMAIL_TEMPLATE_FILE("c3bee66c-0529-11ee-8239-5f348e78e1fc/email.html", "email/email.html");

    private ClassPathStorageEnum(String relativePath, String pathOfClassPath) {
        this.relativePath = relativePath;
        this.pathOfClassPath = pathOfClassPath;
    }

    private String relativePath;
    private String pathOfClassPath;

    public String getRelativePath() {
        return this.relativePath;
    }

    public Resource getResource() {
        return new ClassPathResource(this.pathOfClassPath);
    }

    public Resource getResource(long startIndex, long rangeContentLength) {
        return new RangeClassPathResource(this.pathOfClassPath, startIndex, rangeContentLength);
    }

}
