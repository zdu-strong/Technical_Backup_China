package com.springboot.project.test.service.LoggerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.springboot.project.model.LoggerModel;
import com.springboot.project.test.BaseTest;
import ch.qos.logback.classic.Level;

public class LoggerServiceCreateLoggerTest extends BaseTest {

    private LoggerModel loggerModel;

    @Test
    public void test() throws JsonProcessingException {
        var result = this.loggerService.createLogger(loggerModel);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(result.getHasException());
        assertEquals("Hello, World!", result.getMessage());
        assertEquals("java.lang.RuntimeException", result.getExceptionClassName());
        assertEquals("Bug", result.getExceptionMessage());
        assertEquals(75, result.getExceptionStackTrace().size());
        assertEquals("com.springboot.project.controller.HelloWorldController", result.getLoggerName());
        assertEquals(this.gitProperties.getCommitId(), result.getGitCommitId());
        assertEquals(Date.from(this.gitProperties.getCommitTime()), result.getGitCommitDate());
    }

    @BeforeEach
    public void BeforeEach() throws InterruptedException, JsonMappingException, JsonProcessingException {
        this.loggerModel = new LoggerModel().setLevel(Level.ERROR.levelStr).setMessage("Hello, World!")
                .setHasException(true)
                .setExceptionClassName("java.lang.RuntimeException")
                .setExceptionMessage("Bug")
                .setExceptionStackTrace(JinqStream.from(Lists.newArrayList(new RuntimeException().getStackTrace()))
                        .select(s -> s.toString())
                        .select(s -> "at " + s)
                        .toList())
                .setLoggerName("com.springboot.project.controller.HelloWorldController")
                .setGitCommitId(this.gitProperties.getCommitId())
                .setGitCommitDate(Date.from(this.gitProperties.getCommitTime()));
    }

}
