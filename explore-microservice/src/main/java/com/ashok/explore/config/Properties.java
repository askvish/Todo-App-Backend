package com.ashok.explore.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class Properties {

    @Value("${test.message}")
    private String message;

}
