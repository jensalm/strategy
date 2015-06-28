package com.captechventures.sample.strategies.navigation;

import com.captechventures.strategy.Strategy;
import com.google.common.collect.Maps;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Strategy(type=NavigationStrategy.class)
public class FreeNavigationStrategy implements NavigationStrategy {

    @Override
    public void createNavigation(ModelAndView modelAndView) {

        Map<String, String> navigation = Maps.newLinkedHashMap();

        navigation.put("Home", "/");
        navigation.put("Sign Up", "/signup.html");
        navigation.put("Login", "/login.html");

        modelAndView.addObject("navigation", navigation);

    }
}
