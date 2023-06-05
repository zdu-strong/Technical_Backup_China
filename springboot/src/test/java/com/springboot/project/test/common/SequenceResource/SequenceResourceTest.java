package com.springboot.project.test.common.SequenceResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import com.google.common.collect.Lists;
import com.springboot.project.common.storage.RangeClassPathResource;
import com.springboot.project.common.storage.SequenceResource;
import com.springboot.project.test.BaseTest;

public class SequenceResourceTest extends BaseTest {

    @Test
    public void test() throws IOException {
        var sequenceResource = new SequenceResource("default.jpg", Lists.newArrayList(
                new RangeClassPathResource("image/default.jpg", 800, 5),
                new RangeClassPathResource("image/default.jpg", 805, 6)));
        assertEquals(11, sequenceResource.contentLength());
        assertEquals("default.jpg", this.storage.getFileNameFromResource(sequenceResource));
    }

}
