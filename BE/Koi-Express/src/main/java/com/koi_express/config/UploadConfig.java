package com.koi_express.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UploadConfig {

    private static final Logger logger = LoggerFactory.getLogger(UploadConfig.class);

    @Value("${spring.server.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.server.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @PostConstruct
    public void init() {
        logger.info("Configured max file size: {}", maxFileSize);
        logger.info("Configured max request size: {}", maxRequestSize);
    }
}
