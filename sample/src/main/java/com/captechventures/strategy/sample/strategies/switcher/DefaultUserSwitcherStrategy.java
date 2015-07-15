package com.captechventures.strategy.sample.strategies.switcher;

import com.captechventures.strategy.Strategy;
import com.google.common.collect.Maps;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Strategy
public class DefaultUserSwitcherStrategy implements UserSwitcherStrategy {

    @Override
    public Map<String, String> getLinks(HttpServletRequest request) {

        Map<String, String> links = Maps.newLinkedHashMap();

        links.put("Free", "");
        links.put("Limited", ServletUriComponentsBuilder.fromCurrentContextPath().pathSegment("user").query("id=1").toUriString());
        links.put("Premium", ServletUriComponentsBuilder.fromCurrentContextPath().pathSegment("user").query("id=2").toUriString());

        return links;
    }

}
