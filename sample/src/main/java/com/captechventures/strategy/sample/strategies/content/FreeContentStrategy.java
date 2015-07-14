package com.captechventures.strategy.sample.strategies.content;

import com.captechventures.strategy.Strategy;

@Strategy(type = ContentStrategy.class)
public class FreeContentStrategy implements ContentStrategy {

    @Override
    public String getContent() {
        return "Free Lorem Ipsum";
    }

}
