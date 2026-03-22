package com.casey.applyflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import io.github.cdimascio.dotenv.Dotenv;
import com.casey.applyflow.config.RsaKeyProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class ApplyFlowApplication {

    public static void main(String[] args) {
        // Load .env file if it exists and set as system properties
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        
        SpringApplication.run(ApplyFlowApplication.class, args);
    }

}
