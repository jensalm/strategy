package com.captechventures.strategy.sample.strategies.navigation;

import com.captechventures.strategy.Strategy;
import com.google.common.collect.Maps;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Strategy("#{#profile == T(com.captechventures.strategy.sample.model.Profile).LIMITED or " +
                "#profile == T(com.captechventures.strategy.sample.model.Profile).PREMIUM}")
public class LimitedPremiumNavigationStrategy implements NavigationStrategy {

    public void createNavigation(ModelAndView modelAndView) {

        Map<String, String> navigation = Maps.newLinkedHashMap();

        navigation.put("Home", "/");
        navigation.put("Only for Members 1", "/members1.html");
        navigation.put("Only for Members 2", "/members2.html");
        navigation.put("Sign Out", "/sign_out.html");

        modelAndView.addObject("navigation", navigation);
    }

}
