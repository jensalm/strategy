package com.captechventures.strategy.config;

import com.captechventures.strategy.DefaultStrategyFactory;
import com.captechventures.strategy.Strategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class StrategyBeanPostProcessorTest {

    @InjectMocks
    private final StrategyBeanPostProcessor strategyBeanPostProcessor = new StrategyBeanPostProcessor();

    private DefaultStrategyFactory defaultStrategyFactory = new DefaultStrategyFactory();

    @Mock
    private ConfigurableListableBeanFactory beanFactory;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = BeanInitializationException.class)
    public void sanityCheckShouldFailBecauseOfSeveralStrategiesWithSameSelector() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_three", new NumberThreeTestStrategy());
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_three_duplicate", new NumberThreeTestStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.setBeanFactory(beanFactory);
        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");
    }

    @Test
    public void shouldCreateThreeTestAndThreeSpecial() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_three", new NumberThreeTestStrategy());
        annotatedBeans.put("bean_default_special", new DefaultSpecialStrategy());
        annotatedBeans.put("bean_one_special", new NumberOneSpecialStrategy());
        annotatedBeans.put("bean_twothree_super", new NumberTwoAndThreeSpecialStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");

        TestStrategy strategy = defaultStrategyFactory.getStrategy(TestStrategy.class, Collections.singletonMap("number", 3));
        assertEquals("Strategy returned was for the wrong number", NumberThreeTestStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldCreateTwoOfEach() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_three", new NumberThreeTestStrategy());
        annotatedBeans.put("bean_default_special", new DefaultSpecialStrategy());
        annotatedBeans.put("bean_twothree_special", new NumberTwoAndThreeSpecialStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");

        assertEquals("Total number of strategies types is not correct", 2, defaultStrategyFactory.getStrategyTypes().size());
        List<TestStrategy> testStrategies = defaultStrategyFactory.getAllStrategies(TestStrategy.class);
        assertEquals("Number of strategies for type " + TestStrategy.class + " is not correct", 2, testStrategies.size());
        List<SpecialStrategy> specialStrategies = defaultStrategyFactory.getAllStrategies(SpecialStrategy.class);
        assertEquals("Number of strategies for type " + SpecialStrategy.class + " is not correct", 2, specialStrategies.size());
    }

    @Test
    public void shouldCreateFourTestAndOnlyDefaultSpecial() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_one", new NumberOneTestStrategy());
        annotatedBeans.put("bean_three", new NumberThreeTestStrategy());
        annotatedBeans.put("bean_default_special", new DefaultSpecialStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");

        assertEquals("Total number of strategies types is not correct", 2, defaultStrategyFactory.getStrategyTypes().size());
        List<TestStrategy> testStrategies = defaultStrategyFactory.getAllStrategies(TestStrategy.class);
        assertEquals("Number of strategies for type " + TestStrategy.class + " is not correct", 3, testStrategies.size());
        List<SpecialStrategy> specialStrategies = defaultStrategyFactory.getAllStrategies(SpecialStrategy.class);
        assertEquals("Number of strategies for type " + SpecialStrategy.class + " is not correct", 1, specialStrategies.size());
    }

    @Test
    public void shouldCreateMultipleInterfaces() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_multiple_default", new DefaultMultipleInterfacesStrategy());
        annotatedBeans.put("bean_multiple_one", new NumberOneMultipleInterfacesStrategy());
        annotatedBeans.put("bean_multiple_two", new NumberTwoMultipleInterfacesStrategy());
        annotatedBeans.put("bean_multiple_three", new NumberThreeMultipleInterfacesStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");

        assertEquals("Total number of strategies types is not correct", 2, defaultStrategyFactory.getStrategyTypes().size());
        List<RegularInterface> regularInterface = defaultStrategyFactory.getAllStrategies(RegularInterface.class);
        assertEquals("Number of strategies for type " + RegularInterface.class + " is not correct", 2, regularInterface.size());
        List<OtherRegularInterface> otherRegularInterface = defaultStrategyFactory.getAllStrategies(OtherRegularInterface.class);
        assertNull("Number of strategies for type " + OtherRegularInterface.class + " is not correct", otherRegularInterface);
        List<MultipleInterfacesStrategy> multipleInterfacesStrategeis = defaultStrategyFactory.getAllStrategies(MultipleInterfacesStrategy.class);
        assertEquals("Number of strategies for type " + MultipleInterfacesStrategy.class + " is not correct", 4, multipleInterfacesStrategeis.size());
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotFindAStrategy() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_one", new NumberOneTestStrategy());
        annotatedBeans.put("bean_two", new NumberTwoTestStrategy());
        annotatedBeans.put("bean_three", new NumberThreeTestStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");

        defaultStrategyFactory.getStrategy(SpecialStrategy.class, Collections.singletonMap("number", "THREE"));
    }

    private interface TestStrategy {}

    @Strategy
    private static class DefaultTestStrategy implements TestStrategy {
    }
    @Strategy("#{#number == 1}")
    private static class NumberOneTestStrategy implements TestStrategy {
    }
    // Has the same value as the next strategy on purpose to test the sanity check
    @Strategy("#{#number == 3}")
    private static class NumberTwoTestStrategy implements TestStrategy {
    }
    @Strategy("#{#number == 3}")
    private static class NumberThreeTestStrategy implements TestStrategy {
    }

    private interface SpecialStrategy {}

    @Strategy
    private static class DefaultSpecialStrategy implements SpecialStrategy {
    }
    @Strategy("#{#number == 1}")
    private static class NumberOneSpecialStrategy implements SpecialStrategy {
    }
    @Strategy("#{#number == 2 or #number == 3}")
    private static class NumberTwoAndThreeSpecialStrategy implements SpecialStrategy {
    }

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
}