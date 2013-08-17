package com.captechventures.strategy;

import com.captechventures.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import org.slf4j.*;

import java.util.*;

/**
 * Factory to look up different strategies. Supports profile's based on the user's context.
 * @see Strategy
 */
@Repository
public class StrategyFactory {

    private static final Logger LOG = LoggerFactory.getLogger(StrategyFactory.class);
    
    @Autowired
    private ApplicationContext applicationContext;

    private Map<Class, List<Object>> annotatedTypes = new HashMap<>();
    private Map<Class, Strategy> strategyCache = new HashMap<>();

    /**
     * Finds all beans annotated with Strategy. Does a quick sanity
     * check so only one strategy exists for each profile.
     * @see Strategy
     */
    @PostConstruct
    public void init() {

        Map<String, Object> annotatedBeanClasses = applicationContext.getBeansWithAnnotation(Strategy.class);

        sanityCheck(annotatedBeanClasses.values());

        for (Object bean : annotatedBeanClasses.values()) {
            Strategy strategyAnnotation = strategyCache.get(bean.getClass());
            getBeansWithSameType(strategyAnnotation).add(bean);
        }

    }

    /**
     * Checks to make sure there is only one strategy of each type(Interface) annotated for each profile.
     * Will throw an exception on startup if multiple strategies are mapped to the same profile.
     * @param annotatedBeanClasses a list of beans from the spring application context
     */
    private void sanityCheck(Collection<Object> annotatedBeanClasses) {

        Set<String> usedStrategies = new HashSet<>();

        for (Object bean : annotatedBeanClasses) {

            Strategy strategyAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), Strategy.class);
            strategyCache.put(bean.getClass(), strategyAnnotation);

            if (isDefault(strategyAnnotation)) {
                ifNotExistAdd(strategyAnnotation.type(), "default", usedStrategies);
            }

            for (Profile profile : strategyAnnotation.profiles()) {
                ifNotExistAdd(strategyAnnotation.type(), profile, usedStrategies);
            }

        }
    }

    private void ifNotExistAdd(Class type, Profile profile, Set<String> usedStrategies) {
        ifNotExistAdd(type, profile.name(), usedStrategies);
    }

    private void ifNotExistAdd(Class type, String profile, Set<String> usedStrategies) {
        if (usedStrategies.contains(createKey(type, profile))) {
            throw new RuntimeException("There can only be a single strategy for each type, found multiple for type '"+type+"' and profile '"+profile+"'");
        }
        usedStrategies.add(createKey(type, profile));
    }

    private String createKey(Class type, String profile) {
        return (type+"_"+profile).toLowerCase();
    }

    private List<Object> getBeansWithSameType(Strategy strategyAnnotation) {
        List<Object> beansWithSameType = annotatedTypes.get(strategyAnnotation.type());
        if (beansWithSameType != null) {
            return beansWithSameType;
        } else {
            List<Object> newBeansList = new ArrayList<>();
            annotatedTypes.put(strategyAnnotation.type(), newBeansList);
            return newBeansList;
        }
    }

    private boolean isDefault(Strategy strategyAnnotation) {
        return (strategyAnnotation.profiles().length == 0);
    }

    public <T> T getStrategy(Class<T> strategyType, Profile currentProfile) {

        List<Object> strategyBeans = annotatedTypes.get(strategyType);
        Assert.notEmpty(strategyBeans, "No strategies found of type '"+ strategyType.getName()+"', are the strategies marked with @Strategy?");

        Object profileStrategy = findStrategyMatchingProfile(strategyBeans, currentProfile);
        if (profileStrategy == null) {
            throw new RuntimeException("No strategy found for type '"+ strategyType +"'");
        }
        //noinspection unchecked
        return (T)profileStrategy;
    }

    private Object findStrategyMatchingProfile(List<Object> strategyBeans, Profile currentProfile) {

        Object defaultStrategy = null;
        for (Object bean : strategyBeans) {
            Strategy strategyAnnotation = strategyCache.get(bean.getClass());
            if(currentProfile != null) {
                //Only iterate the profiles if a profile has been selected
                for (Profile profile : strategyAnnotation.profiles()) {
                    if (profile == currentProfile) {
                        LOG.debug("Found strategy of type '"+strategyAnnotation.type()+"' matching profile '"+currentProfile+"'");
                        return bean;
                    }
                }
            }

            if (isDefault(strategyAnnotation)) {
                defaultStrategy = bean;
                if(currentProfile == null) {
                    //In this case we can return the default and stop iterating, since we are only
                    //interested in the default strategy when no profile is selected. May save us a clock cycle or two.
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No profile selected, returning default strategy");
                    }
                    return defaultStrategy;
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            if (defaultStrategy != null) {
                LOG.debug("No profile specific strategy found, returning default strategy");
            }
        }
        return defaultStrategy;
    }

}