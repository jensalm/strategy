package com.captechventures.strategy.config;

import com.captechventures.strategy.DefaultStrategyFactory;
import org.springframework.context.annotation.Bean;

public class StrategyConfigurationSupport {

    @Bean
    public StrategyBeanPostProcessor createStrategyBeanPostProcessor() {
        return new StrategyBeanPostProcessor();
    }

    @Bean
    public DefaultStrategyFactory createStrategyFactory() {
        return new DefaultStrategyFactory();
    }

}
