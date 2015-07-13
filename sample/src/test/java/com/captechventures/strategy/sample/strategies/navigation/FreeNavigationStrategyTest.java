package com.captechventures.strategy.sample.strategies.navigation;

import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FreeNavigationStrategyTest {

    private final FreeNavigationStrategy navigationStrategy = new FreeNavigationStrategy();

    @Test
    public void shouldHaveHomeLink() {
        ModelAndView modelAndView = new ModelAndView();
        navigationStrategy.createNavigation(modelAndView);
        @SuppressWarnings("unchecked")
        Map<String, String> navigation = (Map<String, String>) modelAndView.getModel().get("navigation");
        assertEquals("/", navigation.get("Home"));
    }

    @Test
    public void shouldHaveSignUpAndLoginLinks() {
        ModelAndView modelAndView = new ModelAndView();
        navigationStrategy.createNavigation(modelAndView);
        @SuppressWarnings("unchecked")
        Map<String, String> navigation = (Map<String, String>) modelAndView.getModel().get("navigation");
        assertEquals("/signup.html", navigation.get("Sign Up"));
        assertEquals("/login.html", navigation.get("Login"));
    }

}
