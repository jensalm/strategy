package com.captechventures.strategy;

public class AnnotatedBean<T> {

    private final Strategy strategy;
    private final T bean;
    private boolean defaultStrategy;

    public AnnotatedBean(T bean, Strategy strategy, boolean defaultStrategy) {
        this.strategy = strategy;
        this.bean = bean;
        this.defaultStrategy = defaultStrategy;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public T getBean() {
        return bean;
    }

    public boolean isDefaultStrategy() {
        return defaultStrategy;
    }
}
