package com.captechventures.sample.config;

import com.captechventures.strategy.config.EnableStrategy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.captechventures.sample"})
@EnableStrategy
public class AppConfig {

}
