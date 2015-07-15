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
    public void shouldHaveMembersOnlyAndSignedIn() {
        ModelAndView modelAndView = new ModelAndView();
        navigationStrategy.createNavigation(modelAndView);
        @SuppressWarnings("unchecked")
        Map<String, String> navigation = (Map<String, String>) modelAndView.getModel().get("navigation");
        assertEquals("/sign_out.html", navigation.get("Sign Out"));
        assertEquals("/members1.html", navigation.get("Only for Members 1"));
        assertEquals("/members2.html", navigation.get("Only for Members 2"));
    }
}
