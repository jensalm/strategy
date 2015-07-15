package com.captechventures.strategy.sample.strategies.switcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Strategy showing a different implementation for each Profile and a
 * default for when no Profile is set at all.
 * Just returns a Map of links (key is text and value is url).
 */
public interface UserSwitcherStrategy {

    Map<String, String> getLinks(HttpServletRequest request);

}
