package com.springboot.project.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties
@PropertySource("classpath:application.yml")
public class AuthorizationEmailProperties {
    @Autowired
    private Environment environment;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public String getSenderEmail() {
        return this.environment.getProperty("SPRING_EMAIL_USERNAME", this.senderEmail);
    }
}
