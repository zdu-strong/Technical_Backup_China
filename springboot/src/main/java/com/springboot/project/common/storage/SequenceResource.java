package com.springboot.project.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.Iterator;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import com.google.common.collect.Lists;

/**
 * A resource which is the logical concatenation of other resources.
 */
public class SequenceResource extends AbstractResource {

    /**
     * The resources to be concatenated.
     */
    private final Iterable<Resource> resources;
    private String fileName;

    /**
     * Creates a new SequenceResource as the logical concatenation of the given
     * resources. Each resource is concatenated in iteration order as needed when
     * reading from the input stream of the SequenceResource. The mimetype of the
     * resulting concatenation is derived from the first resource.
     *
     * @param resources The resources to concatenate within the InputStream of this
     *                  SequenceResource.
     */
    public SequenceResource(String fileName, Iterable<Resource> resources) {
        super();
        this.resources = resources;
        this.fileName = fileName;
    }

    @Override
    public String getFilename() {
        return this.fileName;
    }

    @Override
    public InputStream getInputStream() {
        return new SequenceInputStream(new Enumeration<InputStream>() {

            /**
             * Iterator over all resources associated with this SequenceResource.
             */
            private final Iterator<Resource> resourceIterator = resources.iterator();

            @Override
            public boolean hasMoreElements() {
                return resourceIterator.hasNext();
            }

            @Override
            public InputStream nextElement() {
                try {
                    return resourceIterator.next().getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

        });
    }

    @Override
    public long contentLength() throws IOException {
        return JinqStream.from(Lists.newArrayList(resources)).select(s -> {
            try {
                return s.contentLength();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }).sumLong(s -> s);
    }

    @Override
    public String getDescription() {
        return null;
    }

}
