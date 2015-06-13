package com.captechventures.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DefaultSelector<T> implements Selector<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSelector.class);

    private Map<String, Object> context;

    public DefaultSelector(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public T select(List<AnnotatedBean> strategyBeans) {
        StrategySelectorEvaluator evaluator = new StrategySelectorEvaluator(context);
        for (AnnotatedBean bean : strategyBeans) {
            Strategy strategyAnnotation = bean.getStrategy();
            Boolean selected = evaluator.getSelector(strategyAnnotation.selector());
            if (selected != null && selected) {
                LOG.debug(String.format("Found strategy of type '%s' matching expression '%s'", strategyAnnotation.type(), strategyAnnotation.selector()));
                //noinspection unchecked
                return (T) bean.getBean();
            }
        }
        return null;
    }
}
