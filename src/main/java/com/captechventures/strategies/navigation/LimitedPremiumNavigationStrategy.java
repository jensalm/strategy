package com.captechventures.strategies.navigation;

import com.captechventures.strategy.Strategy;
import com.google.common.collect.Maps;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Strategy(type=NavigationStrategy.class,
        selector="#{#profile == T(com.captechventures.model.Profile).LIMITED or #profile == T(com.captechventures.model.Profile).PREMIUM}")
public class LimitedPremiumNavigationStrategy implements NavigationStrategy {

    public void createNavigation(ModelAndView modelAndView) {

        Map<String, String> navigation = Maps.newLinkedHashMap();

        navigation.put("Home", "/");
        navigation.put("Special", "/special.html");
        navigation.put("Signed In", "/signed_in.html");

        modelAndView.addObject("navigation", navigation);
    }

}
