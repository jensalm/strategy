package com.captechventures.strategies.switcher;

import com.captechventures.strategy.Strategy;
import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Strategy(type = UserSwitcherStrategy.class, selector = "#{#profile == T(com.captechventures.model.Profile).LIMITED}")
public class LimitedUserSwitcherStrategy implements UserSwitcherStrategy {

    @Override
    public Map<String, String> getLinks(HttpServletRequest request) {

        Map<String, String> links = Maps.newLinkedHashMap();

        links.put("Free", request.getServletPath()+"/user?id=0");
        links.put("Limited", null);
        links.put("Premium", request.getServletPath()+"/user?id=2");

        return links;
    }

}
