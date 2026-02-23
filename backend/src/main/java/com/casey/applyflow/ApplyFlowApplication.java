package com.casey.applyflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.casey.applyflow.config.RsaKeyProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class ApplyFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplyFlowApplication.class, args);
    }

}
