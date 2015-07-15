package com.captechventures.strategy.config;

import com.captechventures.strategy.AnnotatedBean;
import com.captechventures.strategy.Strategy;
import com.captechventures.strategy.StrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.*;

public class StrategyBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private static final Logger LOG = LoggerFactory.getLogger(StrategyBeanPostProcessor.class);

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new BeanInitializationException("StrategyBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    /**
     * Finds all beans annotated with Strategy. Does a quick sanity
     * check so only one strategy exists for each value.
     *
     * @see Strategy
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (StrategyFactory.class.isAssignableFrom(bean.getClass())) {

            Map<String, Object> annotatedBeanClasses = beanFactory.getBeansWithAnnotation(Strategy.class);

            Map<Class<Object>, List<AnnotatedBean<Object>>> strategies = new HashMap<>();

            for (Object b : annotatedBeanClasses.values()) {
                Strategy strategyAnnotation = AnnotationUtils.findAnnotation(b.getClass(), Strategy.class);
                Class[] interfaces = b.getClass().getInterfaces();
                for (Class c : interfaces) {
                    if (!strategies.containsKey(c)) {
                        strategies.put(c, new ArrayList<>());
                    }
                    ifNotExistAdd(strategies, c, strategyAnnotation, b);
                }
            }
            removeStrategiesWithoutADefault(strategies);
            sanityCheck(strategies);
            ((StrategyFactory)bean).add(strategies);
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    /**
     * Checks that each strategy has only unique selectors.
     * @param strategies
     * @throws BeanInitializationException if a duplicate value is found
     */
    private void sanityCheck(Map<Class<Object>, List<AnnotatedBean<Object>>> strategies) {

        Set<String> selectors = new HashSet<>();
        for (Class<? extends Object> strategyClass : strategies.keySet()) {
            List<AnnotatedBean<Object>> annotatedBeans = strategies.get(strategyClass);

            for (AnnotatedBean annotatedBean : annotatedBeans) {
                String selector = annotatedBean.getStrategy().value();
                if (selector != null && !selector.equals("")) {
                    if (selectors.contains(strategyClass.getName() + selector)) {
                        throw new BeanInitializationException(String.format("The strategy's values must be unique, strategy of type {} has a duplicate value '{}'", strategyClass.getName(), selector));
                    }
                    selectors.add(strategyClass.getName() + selector);
                }
            }
        }
    }

    /**
     * Removes any class that doesn't have a default
     * @param strategies
     */
    private void removeStrategiesWithoutADefault(Map<Class<Object>, List<AnnotatedBean<Object>>> strategies) {

        for (Iterator<Class<Object>> iterator = strategies.keySet().iterator(); iterator.hasNext(); ) {
            Class<Object> strategyClass = iterator.next();
            List<AnnotatedBean<Object>> annotatedBeans = strategies.get(strategyClass);

            if (!hasDefaultStrategy(annotatedBeans)) {
                LOG.warn("Ignoring {} because it doesn't have a default strategy", strategyClass.getName());
                iterator.remove();
            }
        }
    }

    private boolean hasDefaultStrategy(List<AnnotatedBean<Object>> annotatedBeans) {
        for (AnnotatedBean annotatedBean : annotatedBeans) {
            if (annotatedBean.isDefaultStrategy()) {
                return true;
            }
        }
        return false;
    }

    private AnnotatedBean<? extends Object> ifNotExistAdd(Map<Class<Object>, List<AnnotatedBean<Object>>> strategies, Class strategyClass, Strategy strategyAnnotation, Object bean) {
        for (AnnotatedBean ab : strategies.get(strategyClass)) {
            if (ab.getBean().equals(bean)) {
                return ab;
            }
        }
        AnnotatedBean<? super Object> annotatedBean = new AnnotatedBean<>(bean, strategyAnnotation, isDefault(strategyAnnotation));
        strategies.get(strategyClass).add(annotatedBean);
        return annotatedBean;
    }

    private boolean isDefault(Strategy strategyAnnotation) {
        return (strategyAnnotation.value() == null || strategyAnnotation.value().equals(""));
    }

}