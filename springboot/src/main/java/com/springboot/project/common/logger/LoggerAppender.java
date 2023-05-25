package com.springboot.project.common.logger;

import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.service.LoggerService;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import jakarta.annotation.PostConstruct;

@Component
public class LoggerAppender extends AppenderBase<ILoggingEvent> {

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private StorageRootPathProperties storageRootPathProperties;

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (storageRootPathProperties.isTestEnviroment()) {
            return;
        }
        var message = eventObject.getMessage();
        if (StringUtils.isBlank(message)) {
            message = "";
        }
        try {
            loggerService.addLogger(message);
        } catch (Throwable e) {
            // do nothing
        }
    }

    @PostConstruct
    public void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLoggerList().forEach(new Consumer<Logger>() {

            @Override
            public void accept(Logger logger) {
                if ("ROOT".equals(logger.getName())) {
                    logger.addAppender(LoggerAppender.this);
                }
            }
        });

        setContext(context);
        start();
    }
}