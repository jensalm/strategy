package com.captechventures.strategy.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.captechventures.strategy"})
public class DelegatingStrategyConfiguration extends StrategyConfigurationSupport {
}
