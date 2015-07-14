package com.captechventures.strategy.sample.strategies.content;

import com.captechventures.strategy.Strategy;
import com.captechventures.strategy.sample.model.Profile;

@Strategy(type = ContentStrategy.class, selector = "PREMIUM")
public class PremiumContentStrategy implements ContentStrategy {

    @Override
    public String getContent() {
        return "Premium Lorem Ipsum";
    }

}
