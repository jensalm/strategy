package com.captechventures.strategy.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingStrategyConfiguration.class)
public @interface EnableStrategy {

}
