package com.captechventures.strategy.sample.strategies.navigation;

import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LimitedPremiumNavigationStrategyTest {

    private final LimitedPremiumNavigationStrategy navigationStrategy = new LimitedPremiumNavigationStrategy();

    @Test
    public void shouldHaveHomeLink() {
        ModelAndView modelAndView = new ModelAndView();
        navigationStrategy.createNavigation(modelAndView);
        @SuppressWarnings("unchecked")
        Map<String, String> navigation = (Map<String, String>) modelAndView.getModel().get("navigation");
        assertEquals("/", navigation.get("Home"));
    }

    @Test
    public void shouldHaveSpecialAndSignedIn() {
        ModelAndView modelAndView = new ModelAndView();
        navigationStrategy.createNavigation(modelAndView);
        @SuppressWarnings("unchecked")
        Map<String, String> navigation = (Map<String, String>) modelAndView.getModel().get("navigation");
        assertEquals("/signed_in.html", navigation.get("Signed In"));
        assertEquals("/special.html", navigation.get("Special"));
    }
}
