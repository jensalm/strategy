package com.captechventures.strategy;

import java.util.List;
import java.util.Map;

public interface StrategyFactory {

    /**
     * Find a strategy based on a context using an SpEL expression in the Strategy
     * @param strategyType the interface that represents the strategy
     * @param context the values to use as the context while evaluating which strategy to use
     * @param <T> an interface class that is used as a strategy
     * @return the matching implementation of this strategy
     */
    <T> T getStrategy(Class<T> strategyType, Map<String, Object> context);

    /**
     * Find a strategy based on a context using a selector
     * @param strategyType the interface that represents the strategy
     * @param selector an
     * @param <T> an interface class that is used as a strategy
     * @return the matching implementation of this strategy
     * @see Selector
     */
    <T> T getStrategy(Class<T> strategyType, Selector<T> selector);

    /**
     * Adds a new type of a strategy
     * @param clz the interface that this strategy uses
     * @param annotatedBeans a list of annotated beans
     * @see AnnotatedBean
     * @param <T> an interface class that is used as a strategy
     */
    <T> void add(Class<T> clz, List<AnnotatedBean<T>> annotatedBeans);


    /**
     * Add a set of annotated beans for a strategy
     * @param strategies a map of strategies, key is class of
     *                   the interface and value is a list of annotated beans
     */
    <T> void add(Map<Class<T>, List<AnnotatedBean<T>>> strategies);

    /**
     * Removes a type of strategy
     * @param clz the interface that this strategy uses
     * @see AnnotatedBean
     * @param <T> an interface class that is used as a strategy
     */
    <T> void remove(Class<T> clz);

    /**
     * Clears all strategies
     */
    void clear();
}
