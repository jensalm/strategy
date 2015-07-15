package com.captechventures.strategy;

import java.util.List;

/**
 * Contract for selecting the strategy to be used.
 * @param <T> the type of strategy
 * @see DefaultStrategyFactory
 */
public interface Selector<T> {

    T select(List<AnnotatedBean<T>> strategyBeans);

}
