package com.captechventures.strategy.config;

import com.captechventures.strategy.DefaultStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrategyConfiguration {

    @Bean
    public StrategyBeanPostProcessor createStrategyBeanPostProcessor() {
        return new StrategyBeanPostProcessor();
    }

    @Bean
    public DefaultStrategyFactory createStrategyFactory() {
        return new DefaultStrategyFactory();
    }

}
