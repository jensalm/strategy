package com.captechventures.config;

import com.captechventures.strategy.DefaultStrategyFactory;
import org.springframework.context.annotation.Bean;

public class WorkFlowConfigurationSupport {

    @Bean
    public StrategyBeanPostProcessor createStrategyBeanPostProcessor() {
        return new StrategyBeanPostProcessor();
    }

    @Bean
    public DefaultStrategyFactory createStrategyFactory() {
        return new DefaultStrategyFactory();
    }

}
