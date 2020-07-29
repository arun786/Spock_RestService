package com.arun.basicspockspringrest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author arun on 7/28/20
 */

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "client.url")
@Getter
@Setter
public class UrlConfig {
    private String mockUrl;
    private String mockActuatorUrl;
}
