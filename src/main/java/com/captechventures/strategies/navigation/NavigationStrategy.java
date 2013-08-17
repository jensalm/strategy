package com.captechventures.strategies.navigation;

import org.springframework.web.servlet.ModelAndView;

/**
 * Strategy showing how to group the behavior for several Profiles.
 */
public interface NavigationStrategy {

    public void createNavigation(ModelAndView modelAndView);

}
