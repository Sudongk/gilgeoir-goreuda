package com.pd.gilgeorigoreuda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GilgeoriGoreudaApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GilgeoriGoreudaApplication.class);
        application.setAllowBeanDefinitionOverriding(true);
        SpringApplication.run(GilgeoriGoreudaApplication.class, args);
    }
}
