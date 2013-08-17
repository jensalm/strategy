package com.captechventures.controllers;

import com.captechventures.model.Profile;
import com.captechventures.model.User;
import com.captechventures.strategies.navigation.NavigationStrategy;
import com.captechventures.service.UserService;
import com.captechventures.strategies.switcher.UserSwitcherStrategy;
import com.captechventures.strategy.StrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class StartPageController {

    private static final String SESSION_KEY = "user_id";

    @Autowired
    private StrategyFactory strategyFactory;

    @Autowired
    private UserService userService;

    @RequestMapping( value = "/" )
    public ModelAndView showStartPage(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("start");

        User user = getCurrentUser(request);
        modelAndView.addObject("user", user);
        Profile profile = user != null ? user.getProfile() : null;

        strategyFactory.getStrategy(NavigationStrategy.class, profile).createNavigation(modelAndView);

        Map<String, String> links = strategyFactory.getStrategy(UserSwitcherStrategy.class, profile).getLinks(request);
        modelAndView.addObject("links", links);

        return modelAndView;
    }

    @RequestMapping( value = "/user" )
    public ModelAndView changeUser(HttpServletRequest request, @RequestParam String id) {

        User user = userService.getUser(id);
        setCurrentUser(request, user);

        return new ModelAndView("redirect:"+request.getContextPath());
    }

    private User getCurrentUser(HttpServletRequest request) {
        String userId = (String) request.getSession().getAttribute(SESSION_KEY);
        if (userId != null && !userId.equals("")) {
            return userService.getUser(userId);
        }
        return null;
    }

    private void setCurrentUser(HttpServletRequest request, User user) {
        request.getSession().setAttribute(SESSION_KEY, user.getId());
    }
}
