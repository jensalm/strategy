package com.captechventures.strategy;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * A strategy is a small(ish) class of code that can be applied in several
 * places or as a way of breaking out complex parts into it's own class so
 * it can be easily tested.
 * The StrategyFactory will pick the correct strategy for based on the selector used.
 * group.
 * @see DefaultStrategyFactory
 * @see Strategy#value()
 * @see Selector
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Strategy {

    /**
     * A value that distinguishes each implementation of the strategy.
     * @return a unique (for this strategy) value
     */
    String value() default "";

}
