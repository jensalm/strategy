package com.captechventures.strategy;

import com.captechventures.config.StrategyBeanPostProcessor;
import com.captechventures.model.Profile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class DefaultStrategyFactoryTest {

    @InjectMocks
    private final StrategyBeanPostProcessor strategyBeanPostProcessor = new StrategyBeanPostProcessor();

    private DefaultStrategyFactory defaultStrategyFactory = new DefaultStrategyFactory();

    @Mock
    private ConfigurableListableBeanFactory beanFactory;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = RuntimeException.class)
    public void sanityCheckShouldFailBecauseOfSeveralStrategiesForTheProfile() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_premium_2", new PremiumTestStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.setBeanFactory(beanFactory);
        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");
    }

    @Test
    public void shouldGetRegularPremium() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());
        annotatedBeans.put("bean_limitedpremium_super", new LimitedPremiumSpecialStrategy());
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_default_super", new DefaultSuperSpecialStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");

        TestStrategy strategy = defaultStrategyFactory.getStrategy(TestStrategy.class, Collections.singletonMap("profile", Profile.PREMIUM));
        assertEquals("Strategy returned was for the wrong profile", strategy.getClass(), PremiumTestStrategy.class);
    }

    @Test
    public void shouldGetSpecialPremium() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());
        annotatedBeans.put("bean_limitedpremium_super", new LimitedPremiumSpecialStrategy());
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_default_super", new DefaultSuperSpecialStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");

        SuperSpecialStrategy strategy = defaultStrategyFactory.getStrategy(SuperSpecialStrategy.class, Collections.singletonMap("profile", Profile.PREMIUM));
        assertEquals("Strategy returned was for the wrong profile", strategy.getClass(), LimitedPremiumSpecialStrategy.class);
    }

    @Test
    public void shouldGetTheDefaultStrategyWithNullProfileParam() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());
        annotatedBeans.put("bean_limitedpremium_super", new LimitedPremiumSpecialStrategy());
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_default_super", new DefaultSuperSpecialStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");

        TestStrategy strategy = defaultStrategyFactory.getStrategy(TestStrategy.class, Collections.emptyMap());
        assertTrue("Strategy returned was for the wrong profile", strategy instanceof DefaultTestStrategy);
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotFindAStrategy() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_free", new FreeTestStrategy());
        annotatedBeans.put("bean_limited", new LimitedTestStrategy());
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());

        when(beanFactory.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        strategyBeanPostProcessor.postProcessAfterInitialization(defaultStrategyFactory, "defaultStrategyFactory");

        defaultStrategyFactory.getStrategy(SuperSpecialStrategy.class, Collections.singletonMap("profile", Profile.PREMIUM));
    }

    private interface TestStrategy {}

    @Strategy(type=TestStrategy.class)
    private static class DefaultTestStrategy implements TestStrategy {
    }

    @Strategy(type=TestStrategy.class, selector = "#{#profile == T(com.captechventures.model.Profile).FREE}")
    private static class FreeTestStrategy implements TestStrategy {
    }

    @Strategy(type=TestStrategy.class, selector = "#{#profile == T(com.captechventures.model.Profile).LIMITED}")
    private static class LimitedTestStrategy implements TestStrategy {
    }

    @Strategy(type=TestStrategy.class, selector = "#{#profile == T(com.captechventures.model.Profile).PREMIUM}")
    private static class PremiumTestStrategy implements TestStrategy {
    }

    @Strategy(type=TestStrategy.class,
            selector = "#{#profile == T(com.captechventures.model.Profile).FREE or #profile == T(com.captechventures.model.Profile).PREMIUM}")
    private static class FreeLimitedTestStrategy implements TestStrategy {
    }

    private interface SuperSpecialStrategy {}

    @Strategy(type=SuperSpecialStrategy.class)
    private static class DefaultSuperSpecialStrategy implements SuperSpecialStrategy {
    }

    @Strategy(type=SuperSpecialStrategy.class, selector = "#{#profile == T(com.captechventures.model.Profile).FREE}")
    private static class FreeSuperSpecialStrategy implements SuperSpecialStrategy {
    }

    @Strategy(type=SuperSpecialStrategy.class,
            selector = "#{#profile == T(com.captechventures.model.Profile).LIMITED or #profile == T(com.captechventures.model.Profile).PREMIUM}")
    private static class LimitedPremiumSpecialStrategy implements SuperSpecialStrategy {
    }

}