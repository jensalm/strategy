package com.captechventures.strategy.sample.strategies.content;

import com.captechventures.strategy.Strategy;

@Strategy(selector = "LIMITED")
public class LimitedContentStrategy implements ContentStrategy {

    @Override
    public String getContent() {
        return "Limited Lorem Ipsum";
    }

}
