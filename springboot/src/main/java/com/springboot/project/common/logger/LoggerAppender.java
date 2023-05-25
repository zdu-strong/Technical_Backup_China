package com.springboot.project.common.logger;

import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.collect.Lists;
import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.service.LoggerService;
import ch.qos.logback.classic.Level;
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
        if (!Lists.newArrayList(Level.ERROR, Level.WARN).contains(eventObject.getLevel())) {
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
                if (Logger.ROOT_LOGGER_NAME.equals(logger.getName())) {
                    logger.addAppender(LoggerAppender.this);
                }
            }
        });

        setContext(context);
        start();
    }
}