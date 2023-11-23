package com.yasu.ftpLogic.configs;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;


@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Maksimum dosya boyutunu burada belirleyebilirsiniz
        factory.setMaxFileSize(DataSize.ofBytes(60389343000000L));
        // İstek boyutunu burada belirleyebilirsiniz
        factory.setMaxRequestSize(DataSize.ofBytes(60389343000000L));
        return factory.createMultipartConfig();
    }


}