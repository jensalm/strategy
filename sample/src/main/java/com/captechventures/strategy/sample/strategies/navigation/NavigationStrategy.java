package com.captechventures.strategy.sample.strategies.navigation;

import org.springframework.web.servlet.ModelAndView;

/**
 * Strategy showing how to group the behavior for several Profiles.
 */
public interface NavigationStrategy {

    void createNavigation(ModelAndView modelAndView);

}
