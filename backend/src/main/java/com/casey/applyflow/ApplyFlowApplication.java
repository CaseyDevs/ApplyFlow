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
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();

            // Gmail app passwords are often copied as 4 groups separated by spaces.
            if ("MAIL_PASSWORD".equals(key) && value != null) {
                value = value.replace(" ", "").trim();
            }

            System.setProperty(key, value);
        });
        
        SpringApplication.run(ApplyFlowApplication.class, args);
    }

}
