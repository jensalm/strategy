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

    private Map strategies = new HashMap<>();

    /**
     * Uses Spring Expression Language (SpEL) to evaluate which strategy should be selected
     * @param strategyType the interface that represents the strategy
     * @param context the values to use as the context while evaluating which strategy to use
     * @param <T> an interface class that is used as a strategy
     * @return the matching implementation of this strategy
     */
    @Override
    public <T> T getStrategy(Class<T> strategyType, Map<String, Object> context) {
        return getStrategy(strategyType, strategyBeans -> {
            StrategySelectorEvaluator evaluator = new StrategySelectorEvaluator(context);
            for (AnnotatedBean<T> bean : strategyBeans) {
                Strategy strategyAnnotation = bean.getStrategy();
                Boolean selected = evaluator.getSelector(strategyAnnotation.selector());
                if (selected != null && selected) {
                    LOG.debug(String.format("Found strategy of type '%s' matching expression '%s'", strategyAnnotation.type(), strategyAnnotation.selector()));
                    return bean.getBean();
                }
            }
            return null;
        });
    }

    @Override
    public <T> T getStrategy(Class<T> strategyType, Selector<T> selector) {

        List<AnnotatedBean<T>> strategyBeans = (List<AnnotatedBean<T>>)strategies.get(strategyType);
        Assert.notEmpty(strategyBeans, String.format("No strategies found of type '%s', are the strategies marked with @Strategy?", strategyType.getName()));

        T chosenStrategy = selector.select(strategyBeans);
        //noinspection unchecked
        if (chosenStrategy != null) {
            return chosenStrategy;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("No strategy selected, using default strategy");
        }
        for (AnnotatedBean<T> annotatedBean : strategyBeans) {
            if (annotatedBean.isDefaultStrategy()) {
                //noinspection unchecked
                return annotatedBean.getBean();
            }
        }

        throw new RuntimeException(String.format("No strategy found for type '%s'", strategyType));
    }

    @Override
    public <T> void add(Class<T> clz, List<AnnotatedBean<T>> annotatedBeans) {
        this.strategies.put(clz, annotatedBeans);
    }

    @Override
    public <T> void add(Map<Class<T>, List<AnnotatedBean<T>>> strategies) {
        this.strategies.putAll(strategies);
    }

    @Override
    public <T> void remove(Class<T> clz) {
        this.strategies.remove(clz);
    }

    @Override
    public void clear() {
        this.strategies.clear();
    }


}