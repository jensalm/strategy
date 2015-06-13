package com.captechventures.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import org.slf4j.*;

import java.util.*;

/**
 * ‘
 * Factory to look up different strategies at runtime.
 *
 * @see Strategy
 */
@Repository
public class StrategyFactory {

    private static final Logger LOG = LoggerFactory.getLogger(StrategyFactory.class);

    @Autowired
    private ApplicationContext applicationContext;

    private Map<Class, List<AnnotatedBean>> strategies = new HashMap<>();

    /**
     * Finds all beans annotated with Strategy. Does a quick sanity
     * check so only one strategy exists for each selector value.
     *
     * @see Strategy
     */
    @PostConstruct
    public void init() {

        Map<String, Object> annotatedBeanClasses = applicationContext.getBeansWithAnnotation(Strategy.class);

        for (Object bean : annotatedBeanClasses.values()) {
            Strategy strategyAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), Strategy.class);
            if (!strategies.containsKey(strategyAnnotation.type())) {
                strategies.put(strategyAnnotation.type(), new ArrayList<>());
            }
            ifNotExistAdd(strategies.get(strategyAnnotation.type()), strategyAnnotation, bean);
        }

        sanityCheck();
    }

    private void sanityCheck() {

        Map<String, AnnotatedBean> selectors = new HashMap<>();
        for (List<AnnotatedBean> annotatedBeans : strategies.values()) {
            for (AnnotatedBean annotatedBean : annotatedBeans) {
                String selector = annotatedBean.getStrategy().selector();
                if (selector != null && !selector.equals("")) {
                    AnnotatedBean otherBean = selectors.get(selector);
                    if (otherBean != null && otherBean.getStrategy().type().equals(annotatedBean.getStrategy().type())) {
                        throw new RuntimeException("Selectors must be unique for each strategy, duplicate selector '" + selector + "'");
                    }
                    selectors.put(selector, annotatedBean);
                }
            }
        }

        for (Class strategyClass : strategies.keySet()) {
            if (!hasDefaultStrategy(strategyClass)) {
                throw new RuntimeException("Each strategy must have a default fallback strategy, strategy missing default: " + strategyClass.getName());
            }
        }
    }

    private boolean hasDefaultStrategy(Class strategyClass) {
        List<AnnotatedBean> annotatedBeans = strategies.get(strategyClass);
        for (AnnotatedBean annotatedBean : annotatedBeans) {
            if (annotatedBean.isDefaultStrategy()) {
                return true;
            }
        }
        return false;
    }

    private AnnotatedBean ifNotExistAdd(List<AnnotatedBean> beans, Strategy strategyAnnotation, Object bean) {
        for (AnnotatedBean ab : beans) {
            if (ab.getBean().equals(bean)) {
                return ab;
            }
        }
        AnnotatedBean annotatedBean = new AnnotatedBean<>(bean, strategyAnnotation, isDefault(strategyAnnotation));
        strategies.get(strategyAnnotation.type()).add(annotatedBean);
        return annotatedBean;
    }

    private boolean isDefault(Strategy strategyAnnotation) {
        return (strategyAnnotation.selector() == null || strategyAnnotation.selector().equals(""));
    }

    public <T> T getStrategy(Class<T> strategyType, Map<String, Object> context) {
        return getStrategy(strategyType, new DefaultSelector<>(context));
    }

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
            if (isDefault(annotatedBean.getStrategy())) {
                //noinspection unchecked
                return (T) annotatedBean.getBean();
            }
        }

        throw new RuntimeException(String.format("No strategy found for type '%s'", strategyType));
    }

}