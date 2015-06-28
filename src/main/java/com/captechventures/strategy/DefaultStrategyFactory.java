package com.captechventures.strategy;

import org.springframework.util.Assert;

import org.slf4j.*;

import java.util.*;

/**
 * ‘
 * Factory to look up different strategies at runtime.
 *
 * @see Strategy
 */
public class DefaultStrategyFactory implements StrategyFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultStrategyFactory.class);

    private Map<Class, List<AnnotatedBean>> strategies = new HashMap<>();

    @Override
    public <T> T getStrategy(Class<T> strategyType, Map<String, Object> context) {
        return getStrategy(strategyType, new DefaultSelector<>(context));
    }

    @Override
    public <T> T getStrategy(Class<T> strategyType, Selector<T> selector) {

        List<AnnotatedBean> strategyBeans = strategies.get(strategyType);
        Assert.notEmpty(strategyBeans, String.format("No strategies found of type '%s', are the strategies marked with @Strategy?", strategyType.getName()));

        T chosenStrategy = selector.select(strategyBeans);
        //noinspection unchecked
        if (chosenStrategy != null) {
            return chosenStrategy;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("No strategy selected, using default strategy");
        }
        for (AnnotatedBean annotatedBean : strategyBeans) {
            if (annotatedBean.isDefaultStrategy()) {
                //noinspection unchecked
                return (T) annotatedBean.getBean();
            }
        }

        throw new RuntimeException(String.format("No strategy found for type '%s'", strategyType));
    }

    @Override
    public void add(Class clz, List<AnnotatedBean> annotatedBeans) {
        this.strategies.put(clz, annotatedBeans);
    }

    @Override
    public void add(Map<Class, List<AnnotatedBean>> strategies) {
        this.strategies.putAll(strategies);
    }

    @Override
    public void remove(Class clz) {
        this.strategies.remove(clz);
    }

    @Override
    public void clear() {
        this.strategies.clear();
    }
}