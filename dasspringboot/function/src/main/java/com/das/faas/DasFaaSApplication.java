package com.das.faas;

import com.das.faas.function.BusinessFunction;
import com.das.faas.function.dto.RequestBodyDTO;
import com.das.libcore.web.annotation.EnableJwtInfoAnnotation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.function.Function;

@SpringBootApplication(scanBasePackages = {"com.das"})
@EnableJwtInfoAnnotation
public class DasFaaSApplication {


    @Bean
    @PostMapping
    public Function<RequestBodyDTO, String> businessFunction() {
        return new BusinessFunction();
    }

    public static void main(String[] args) {
        SpringApplication.run(DasFaaSApplication.class, args);
    }
}
