package com.captechventures.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class DefaultStrategyFactoryTest {

    private DefaultStrategyFactory strategyFactory = new DefaultStrategyFactory();

    @Before
    public void init() {
        setStrategies(null);
    }

    @Test
    public void shouldGetNumberThreeTestStrategy() {
        Map<Class, List<AnnotatedBean>> annotatedBeans = new HashMap<>();
        annotatedBeans.put(TestStrategy.class, Arrays.asList(create(new DefaultTestStrategy(), true), create(new NumberTwoTestStrategy()), create(new NumberThreeTestStrategy())));
        annotatedBeans.put(SpecialStrategy.class, Arrays.asList(create(new DefaultSpecialStrategy(), true), create(new NumberTwoAndThreeSpecialStrategy())));
        setStrategies(annotatedBeans);

        TestStrategy strategy = strategyFactory.getStrategy(TestStrategy.class, Collections.singletonMap("number", 3));
        assertEquals("Strategy returned was for the wrong number", NumberThreeTestStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldGetNumberTwoSpecialStrategy() {
        Map<Class, List<AnnotatedBean>> annotatedBeans = new HashMap<>();
        annotatedBeans.put(TestStrategy.class, Arrays.asList(create(new DefaultTestStrategy(), true), create(new NumberTwoTestStrategy())));
        annotatedBeans.put(SpecialStrategy.class, Arrays.asList(create(new DefaultSpecialStrategy(), true), create(new NumberTwoAndThreeSpecialStrategy())));
        setStrategies(annotatedBeans);

        SpecialStrategy strategy = strategyFactory.getStrategy(SpecialStrategy.class, Collections.singletonMap("number", 2));
        assertEquals("Strategy returned was for the wrong number", NumberTwoAndThreeSpecialStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldGetTheDefaultStrategyWithNoNumberParam() {
        Map<Class, List<AnnotatedBean>> annotatedBeans = new HashMap<>();
        annotatedBeans.put(TestStrategy.class, Arrays.asList(create(new DefaultTestStrategy(), true), create(new NumberTwoTestStrategy())));
        annotatedBeans.put(SpecialStrategy.class, Arrays.asList(create(new DefaultSpecialStrategy(), true), create(new NumberTwoAndThreeSpecialStrategy())));
        setStrategies(annotatedBeans);

        TestStrategy strategy = strategyFactory.getStrategy(TestStrategy.class, Collections.emptyMap());
        assertEquals("Strategy returned was for the wrong number", DefaultTestStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldGetTheCorrectStrategyDespiteMultipleInterfaces() {
        Map<Class, List<AnnotatedBean>> annotatedBeans = new HashMap<>();
        annotatedBeans.put(TestStrategy.class, Arrays.asList(create(new DefaultTestStrategy(), true), create(new NumberTwoTestStrategy()), create(new NumberThreeTestStrategy())));
        annotatedBeans.put(SpecialStrategy.class, Arrays.asList(create(new DefaultSpecialStrategy(), true), create(new NumberTwoAndThreeSpecialStrategy())));
        annotatedBeans.put(MultipleInterfacesStrategy.class, Arrays.asList(create(new DefaultMultipleInterfacesStrategy(), true), create(new NumberOneMultipleInterfacesStrategy()), create(new NumberTwoMultipleInterfacesStrategy()), create(new NumberThreeMultipleInterfacesStrategy())));

        setStrategies(annotatedBeans);

        MultipleInterfacesStrategy strategy = strategyFactory.getStrategy(MultipleInterfacesStrategy.class, Collections.singletonMap("number", 3));
        assertEquals("Strategy returned was for the wrong number", NumberThreeMultipleInterfacesStrategy.class, strategy.getClass());
    }

    @Test(expected = BeanInitializationException.class)
    public void shouldNotFindAStrategy() {
        Map<Class, List<AnnotatedBean>> annotatedBeans = new HashMap<>();
        annotatedBeans.put(TestStrategy.class, Arrays.asList(create(new DefaultTestStrategy(), true), create(new NumberTwoTestStrategy()), create(new NumberThreeTestStrategy())));

        setStrategies(annotatedBeans);

        strategyFactory.getStrategy(SpecialStrategy.class, Collections.singletonMap("number", 2));
    }

    private interface TestStrategy {}

    @Strategy
    private static class DefaultTestStrategy implements TestStrategy {

    }
    @Strategy("#{#number == 2}")
    private static class NumberTwoTestStrategy implements TestStrategy {

    }
    @Strategy("#{#number == 3}")
    private static class NumberThreeTestStrategy implements TestStrategy {

    }

    private interface SpecialStrategy {}

    @Strategy
    private static class DefaultSpecialStrategy implements SpecialStrategy {

    }
    @Strategy("#{#number == 2 or #number == 3}")
    private static class NumberTwoAndThreeSpecialStrategy implements SpecialStrategy {

    }

    /**
     * Classes for testing multiple interfaces
     */
    private interface MultipleInterfacesStrategy {}
    private interface RegularInterface {}
    private interface OtherRegularInterface {}

    @Strategy
    private static class DefaultMultipleInterfacesStrategy implements MultipleInterfacesStrategy, RegularInterface {

    }
    @Strategy("#{#number == 1}")
    private static class NumberOneMultipleInterfacesStrategy implements MultipleInterfacesStrategy, OtherRegularInterface {

    }
    @Strategy("#{#number == 2}")
    private static class NumberTwoMultipleInterfacesStrategy implements MultipleInterfacesStrategy {

    }
    @Strategy("#{#number == 3}")
    private static class NumberThreeMultipleInterfacesStrategy implements MultipleInterfacesStrategy, RegularInterface, OtherRegularInterface {

    }

    private <T> AnnotatedBean<T> create(T strategy) {
        return create(strategy, false);
    }

    private <T> AnnotatedBean<T> create(T strategy, boolean defaultStrategy) {
        Strategy strategyAnnotation = AnnotationUtils.findAnnotation(strategy.getClass(), Strategy.class);
        return new AnnotatedBean<>(strategy, strategyAnnotation, defaultStrategy);
    }

    private void setStrategies(Map<Class, List<AnnotatedBean>> strategies) {
        Whitebox.setInternalState(strategyFactory, "strategies", strategies);
    }

}