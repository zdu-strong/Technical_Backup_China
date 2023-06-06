package com.springboot.project.common.storage;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class BaseStorageIsDirectory extends BaseStorageGetResourceForRequest {

    public Boolean isDirectory(String relativePathOfResource) {
        var relativePath = this.getRelativePathFromResourcePath(relativePathOfResource);
        var request = new MockHttpServletRequest();
        request.setRequestURI(this.getResoureUrlFromResourcePath(relativePath));
        return this.isDirectory(request);
    }

    public Boolean isDirectory(HttpServletRequest request) {
        try {
            var resource = this.getResourceFromRequest(request);
            var isFolder = resource instanceof ByteArrayResource;
            return isFolder;
        } catch (Throwable e) {
            return false;
        }
    }

}
