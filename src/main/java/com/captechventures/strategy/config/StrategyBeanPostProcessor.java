package com.captechventures.strategy.config;

import com.captechventures.strategy.AnnotatedBean;
import com.captechventures.strategy.Strategy;
import com.captechventures.strategy.StrategyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrategyBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new IllegalArgumentException("StrategyBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    /**
     * Finds all beans annotated with Strategy. Does a quick sanity
     * check so only one strategy exists for each selector value.
     *
     * @see Strategy
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (bean.getClass().isAssignableFrom(StrategyFactory.class)) {

            Map<String, Object> annotatedBeanClasses = beanFactory.getBeansWithAnnotation(Strategy.class);

            Map<Class, List<AnnotatedBean>> strategies = new HashMap<>();

            for (Object b : annotatedBeanClasses.values()) {
                Strategy strategyAnnotation = AnnotationUtils.findAnnotation(b.getClass(), Strategy.class);
                if (!strategies.containsKey(strategyAnnotation.type())) {
                    strategies.put(strategyAnnotation.type(), new ArrayList<>());
                }
                ifNotExistAdd(strategies, strategies.get(strategyAnnotation.type()), strategyAnnotation, b);
            }
            sanityCheck(strategies);
            ((StrategyFactory)bean).add(strategies);
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return null;
    }

    private void sanityCheck(Map<Class, List<AnnotatedBean>> strategies) {

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
            if (!hasDefaultStrategy(strategies, strategyClass)) {
                throw new RuntimeException("Each strategy must have a default fallback strategy, strategy missing default: " + strategyClass.getName());
            }
        }
    }

    private boolean hasDefaultStrategy(Map<Class, List<AnnotatedBean>> strategies, Class strategyClass) {
        List<AnnotatedBean> annotatedBeans = strategies.get(strategyClass);
        for (AnnotatedBean annotatedBean : annotatedBeans) {
            if (annotatedBean.isDefaultStrategy()) {
                return true;
            }
        }
        return false;
    }

    private AnnotatedBean ifNotExistAdd(Map<Class, List<AnnotatedBean>> strategies, List<AnnotatedBean> beans, Strategy strategyAnnotation, Object bean) {
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

}