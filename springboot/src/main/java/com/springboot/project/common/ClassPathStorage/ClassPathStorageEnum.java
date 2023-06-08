package com.springboot.project.common.ClassPathStorage;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.springboot.project.common.storage.RangeClassPathResource;

/**
 * Define file only
 */
public enum ClassPathStorageEnum {

    EMAIL_TEMPLATE_FILE("7162b22b-05bf-11ee-aa4f-27fdc35ebd80/email.xml", "email/email.xml");

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
